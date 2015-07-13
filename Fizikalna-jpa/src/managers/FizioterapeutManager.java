package managers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Fizioterapeut;
import model.Obaveza;
import model.Osoba;
import model.Pacijent;
import model.Pregled;
import model.Stanje;
import model.Vezbe;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 * @coauthor MilanPavlovic MP101
 */

public class FizioterapeutManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	@SuppressWarnings("unchecked")
	public static boolean isFizioterapeutOccupiedInTermin(Integer id,
			Fizioterapeut fizioterapeut, Date date, String vremeOd,
			String vremeDo) {

		em = JPAUtil.getEntityManager();

		TypedQuery<Obaveza> query;
		// obaveza exist
		if (id != null) { // remove current obaveza, so we can ask if that changed obaveza is free
			query = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.id != :oId AND o.fizioterapeut.id = :id AND o.datum = :date");
			query.setParameter("date", createDate(date));
			query.setParameter("id", fizioterapeut.getId());
			query.setParameter("oId", id.intValue());
		} else {
			query = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.fizioterapeut.id = :id AND o.datum = :date");
			query.setParameter("date", createDate(date));
			query.setParameter("id", fizioterapeut.getId());
		}

		List<Obaveza> list = query.getResultList();

		int[] arrayP = getIntervalInMinutes(vremeOd, vremeDo);

		for (Obaveza obaveza : list) {

			em.getTransaction().begin();
			{
				obaveza = em.merge(obaveza);
			}
			em.getTransaction().commit();

			String vremeOdF = obaveza.getVremeOd();
			String vremeDoF = obaveza.getVremeDo();

			int[] arrayF = getIntervalInMinutes(vremeOdF, vremeDoF);

			if (arrayP[0] >= arrayF[0] && arrayP[0] < arrayF[1])
				return true;
			if (arrayP[1] > arrayF[0] && arrayP[1] < arrayF[1])
				return true;
			if (arrayP[0] <= arrayF[0] && arrayP[1] >= arrayF[1])
				return true;
			if (arrayP[0] <= arrayF[0] && arrayP[1] > arrayF[0]
					&& arrayP[1] <= arrayF[1])
				return true;
		}

		return false;
	}

	private static int[] getIntervalInMinutes(String vremeOd, String vremeDo) {
		int hoursOd = getIntArray(vremeOd)[0];
		int minutesOd = getIntArray(vremeOd)[1];
		int hoursDo = getIntArray(vremeDo)[0];
		int minutesDo = getIntArray(vremeDo)[1];

		return new int[] { hoursOd * 60 + minutesOd, hoursDo * 60 + minutesDo };
	}

	private static int[] getIntArray(String time) {
		String[] strA = time.split(":");
		int hours = Integer.parseInt(strA[0]);
		int minutes = Integer.parseInt(strA[1]);

		return new int[] { hours, minutes };
	}

	private static Date createDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	@SuppressWarnings("unchecked")
	public static List<Fizioterapeut> getAllFizioterapeut() {
		TypedQuery<Fizioterapeut> query = (TypedQuery<Fizioterapeut>) em
				.createNamedQuery("Fizioterapeut.findAll");
		List<Fizioterapeut> list = query.getResultList();
		return list;
	}

	public static boolean updateFizioterapeutInfo(Osoba fizioterapeut) {
		try {
			em.getTransaction().begin();
			{
				em.merge(fizioterapeut); // cudnovato
				em.merge(fizioterapeut.getRadnik());
				em.merge(fizioterapeut.getRadnik().getFizioterapeut());
				fizioterapeut = em.merge(fizioterapeut);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public static void updateNapomena(Pacijent pacijent) {
		try {
			em.getTransaction().begin();
			{
				em.merge(pacijent.getKarton());
				pacijent = em.merge(pacijent);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
		}
	}

	public static void updateNapomenaPregled(Pregled pregled) {
		em.getTransaction().begin();
		{
			em.merge(pregled);
		}
		em.getTransaction().commit();
	}

	public static void removeObaveza(Fizioterapeut fizioterapeut,
			Obaveza izabranaObaveza) {
		fizioterapeut.removeObaveza(izabranaObaveza);
	}

	public static void updateVezbe(Vezbe vezbe, Stanje stanje) {
		try {
			em.getTransaction().begin();
			{
				stanje.setDatum(new Date());
				em.merge(vezbe);
				em.merge(stanje);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
		}
	}

}
