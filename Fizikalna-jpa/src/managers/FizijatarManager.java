package managers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Fizijatar;
import model.Obaveza;
import model.Osoba;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class FizijatarManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	@SuppressWarnings("unchecked")
	public static boolean isFizijatarOccupiedInTermin(Integer id,
			Fizijatar fizijatar, Date date, String vremeOd, String vremeDo) {
		em = JPAUtil.getEntityManager();

		TypedQuery<Obaveza> query;
		// obaveza exist
		if (id != null) {
			query = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.id != :oId AND o.fizijatar.id = :id AND o.datum = :date");
			query.setParameter("date", createDate(date));
			query.setParameter("id", fizijatar.getId());
			query.setParameter("oId", id.intValue());
		} else {
			query = (TypedQuery<Obaveza>) em
					.createQuery("SELECT o FROM Obaveza o WHERE o.fizijatar.id = :id AND o.datum = :date");
			query.setParameter("date", createDate(date));
			query.setParameter("id", fizijatar.getId());
		}

		List<Obaveza> list = query.getResultList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getFizijatar().getId() != fizijatar.getId())
				list.remove(i);
		}

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

	public static Date createDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	@SuppressWarnings("unchecked")
	public static List<Fizijatar> getAllFizijatar() {
		TypedQuery<Fizijatar> query = (TypedQuery<Fizijatar>) em
				.createNamedQuery("Fizijatar.findAll");
		List<Fizijatar> list = query.getResultList();
		return list;
	}

	public static void updateFizijatar(Osoba osoba) {
		em.getTransaction().begin();
		{
			em.merge(osoba);
			em.merge(osoba.getRadnik());
			em.merge(osoba.getRadnik().getFizijatar());
			osoba = em.merge(osoba);
		}
		em.getTransaction().commit();
	}

	public static void removeObaveza(Fizijatar fizijatar, Obaveza obaveza) {
		fizijatar.removeObaveza(obaveza);
	}

}
