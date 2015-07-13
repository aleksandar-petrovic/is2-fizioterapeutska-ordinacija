package managers;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Fizijatar;
import model.Fizioterapeut;
import model.Karton;
import model.Obaveza;
import model.Osoba;
import model.Pacijent;
import model.Pregled;
import model.Vezbe;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class PacijentManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	@SuppressWarnings("unchecked")
	public static boolean isPacijentOccupiedInTermin(Integer id,
			Pacijent pacijent, Date date, String vremeOd, String vremeDo) {
		em = JPAUtil.getEntityManager();

		TypedQuery<Obaveza> queryP;
		TypedQuery<Obaveza> queryV;
		// obaveza exist
		if (id != null) {
			queryP = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.id != :oId AND o.pregled.karton.id = :id AND o.datum = :date");
			queryV = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.id != :oId AND o.vezbe.karton.id = :id AND o.datum = :date");

			queryP.setParameter("date", createDate(date));
			queryP.setParameter("id", pacijent.getKarton().getId());
			queryP.setParameter("oId", id.intValue());
			queryV.setParameter("date", createDate(date));
			queryV.setParameter("id", pacijent.getKarton().getId());
			queryV.setParameter("oId", id.intValue());
		} else {
			queryP = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.pregled.karton.id = :id AND o.datum = :date");
			queryV = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.vezbe.karton.id = :id AND o.datum = :date");

			queryP.setParameter("date", createDate(date));
			queryP.setParameter("id", pacijent.getKarton().getId());
			queryV.setParameter("date", createDate(date));
			queryV.setParameter("id", pacijent.getKarton().getId());
		}

		List<Obaveza> listP = queryP.getResultList();
		List<Obaveza> listV = queryV.getResultList();

		List<Obaveza> list = new LinkedList<Obaveza>();
		list.addAll(listP);
		list.addAll(listV);

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

	public static void removePregled(Pacijent pacijent, Pregled pregled) {
		em.getTransaction().begin();
		{
			pacijent.getKarton().removePregled(pregled);
			pacijent = em.merge(pacijent);
		}
		em.getTransaction().commit();
	}

	public static void removeVezbe(Pacijent pacijent, Vezbe vezbe) {
		try {
			em.getTransaction().begin();
			{
				pacijent.getKarton().removeVezbe(vezbe);
				em.merge(pacijent);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
		}
	}

	public static boolean registerPacijent(String username, String pass,
			Fizijatar fizijatar, Fizioterapeut fizioterapeut) {
		boolean exist = OsobaManager.doesOsobaWithUsernameExist(username);

		if (!exist) {
			em.getTransaction().begin();
			{
				Osoba osoba = new Osoba();
				osoba.setKorisnickoIme(username);
				osoba.setSifra(pass);
				em.persist(osoba);

				Pacijent pacijent = new Pacijent();
				pacijent.setId(osoba.getId());
				pacijent.setOsoba(osoba);
				pacijent.setFizijatar(fizijatar);
				pacijent.setFizioterapeut(fizioterapeut);

				Karton karton = new Karton();
				em.persist(karton);

				pacijent.setKarton(karton);
				em.persist(pacijent);

				osoba.setPacijent(pacijent);
				fizijatar.addPacijent(pacijent);
				fizioterapeut.addPacijent(pacijent);

				em.persist(osoba);
				em.merge(fizijatar);
				em.merge(fizioterapeut);
			}
			em.getTransaction().commit();
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	public static Pacijent getOsobaByJMBGOrUsernameFizijatar(Osoba osoba,
			String str) {
		em.getTransaction().begin();

		TypedQuery<Osoba> query = (TypedQuery<Osoba>) em
				.createQuery("SELECT o FROM Osoba o WHERE o.pacijent.fizijatar.id = :id AND ( o.korisnickoIme LIKE '"
						+ str + "' OR o.jmbg LIKE '" + str + "' )");
		query.setParameter("id", osoba.getId());

		List<Osoba> list = query.getResultList();

		em.getTransaction().commit();

		if (list.size() != 0)
			return list.get(0).getPacijent();
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Pacijent getOsobaByJMBGOrUsernameFizioterapeut(Osoba osoba,
			String str) {
		em.getTransaction().begin();

		TypedQuery<Osoba> query = (TypedQuery<Osoba>) em
				.createQuery("SELECT o FROM Osoba o WHERE o.pacijent.fizioterapeut.id = :id AND ( o.korisnickoIme LIKE '"
						+ str + "' OR o.jmbg LIKE '" + str + "' )");
		query.setParameter("id", osoba.getId());

		List<Osoba> list = query.getResultList();

		em.getTransaction().commit();

		if (list.size() != 0)
			return list.get(0).getPacijent();
		return null;
	}

	public static void updateKarton(Pacijent pacijent) {
		em.getTransaction().begin();
		{
			em.merge(pacijent);
			em.merge(pacijent.getKarton());
		}
		em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public static List<Pacijent> getAllPacijent() {
		TypedQuery<Pacijent> query = (TypedQuery<Pacijent>) em
				.createNamedQuery("Pacijent.findAll");
		return query.getResultList();
	}
}
