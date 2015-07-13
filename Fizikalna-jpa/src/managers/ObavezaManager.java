package managers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Fizijatar;
import model.Fizioterapeut;
import model.Obaveza;
import model.Pacijent;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class ObavezaManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	public static void main(String[] args) {

	}

	@SuppressWarnings("unchecked")
	public static List<Obaveza> getAllObaveze() {
		TypedQuery<Obaveza> query = (TypedQuery<Obaveza>) em
				.createQuery("SELECT o FROM Obaveza o");
		return query.getResultList();
	}

	public static boolean checkInterval(String vremeOd, String vremeDo) {
		int[] arrayO = getIntArray(vremeOd);
		int[] arrayD = getIntArray(vremeDo);
		if (arrayO[0] == arrayD[0])
			return arrayO[1] <= arrayD[1];
		if (arrayO[0] > arrayD[0])
			return false;
		return true;
	}

	private static int[] getIntArray(String time) {
		String[] strA = time.split(":");
		int hours = Integer.parseInt(strA[0]);
		int minutes = Integer.parseInt(strA[1]);

		return new int[] { hours, minutes };
	}

	@SuppressWarnings("unchecked")
	public static Obaveza getObavezaForToday(Fizijatar fizijatar,
			Pacijent pacijent) {
		TypedQuery<Obaveza> query = (TypedQuery<Obaveza>) em
				.createQuery("SELECT o FROM Obaveza o WHERE o.datum = :date AND o.fizijatar.id = :fid AND o.pregled.karton.id = :pid");
		query.setParameter("date", createDate());
		query.setParameter("fid", fizijatar.getId());
		query.setParameter("pid", pacijent.getKarton().getId());

		List<Obaveza> list = query.getResultList();
		return list.size() == 0 ? null : list.get(0);
	}

	@SuppressWarnings("unchecked")
	public static Obaveza getObavezaForTodayFT(Fizioterapeut fizioterapeut,
			Pacijent pacijent) {
		em = JPAUtil.getEntityManager();

		TypedQuery<Obaveza> upit = (TypedQuery<Obaveza>) em
				.createQuery("SELECT o FROM Obaveza o "
						+ "WHERE (o.datum = :datum) AND (o.fizioterapeut.id = :idFT) AND (o.vezbe.karton.id = :Vid)");
		upit.setParameter("idFT", fizioterapeut.getId());
		upit.setParameter("Vid", pacijent.getKarton().getId());
		upit.setParameter("datum", createDate());

		try {
			return upit.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	private static Date createDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static void updateObaveza(Obaveza obaveza) {
		em.getTransaction().begin();
		{
			obaveza = em.merge(obaveza);
		}
		em.getTransaction().commit();
	}

	public static void persistObaveza(Fizijatar fizijatar, Obaveza obaveza) {
		em.getTransaction().begin();
		{
			em.persist(obaveza);
			fizijatar.addObaveza(obaveza);
			fizijatar = em.merge(fizijatar);
		}
		em.getTransaction().commit();
	}

	public static void persistObavezaFT(Fizioterapeut fizioterapeut,
			Obaveza obaveza) {
		try {
			em.getTransaction().begin();
			{
				em.persist(obaveza);
				fizioterapeut.addObaveza(obaveza);
				em.merge(fizioterapeut);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
		}
	}

	public static void deleteObaveza(Obaveza obaveza) {
		em.getTransaction().begin();
		{
			em.remove(em.contains(obaveza) ? obaveza : em.merge(obaveza));
		}
		em.getTransaction().commit();
	}

	// sample obaveza
	public static void createObaveza() {
		Fizijatar fizijatar = em.find(Fizijatar.class, 2);

		em.getTransaction().begin();
		{
			Obaveza obaveza = new Obaveza();
			obaveza.setNaziv("Standardni pregled");
			obaveza.setDatum(new Date());
			obaveza.setOpis("Detaljan pregled kicme...");
			obaveza.setFizijatar(fizijatar);
			obaveza.setVremeOd("10:00");
			obaveza.setVremeDo("12:30");

			em.persist(obaveza);

			fizijatar.addObaveza(obaveza);
			fizijatar = em.merge(fizijatar);
		}
		em.getTransaction().commit();
	}
}
