package managers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Fizijatar;
import model.Fizioterapeut;
import model.Osoba;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class OsobaManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	@SuppressWarnings("unchecked")
	public static List<Osoba> getAllOsoba() {
		TypedQuery<Osoba> query = (TypedQuery<Osoba>) em
				.createQuery("SELECT o FROM Osoba o order by o.datumRodjenja");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static boolean doesOsobaWithUsernameExist(String username) {
		// doesn't work without transaction
		em.getTransaction().begin();
		TypedQuery<Osoba> query = (TypedQuery<Osoba>) em
				.createQuery("SELECT o FROM Osoba o WHERE o.korisnickoIme LIKE :username");
		query.setParameter("username", username);
		int size = query.getResultList().size();
		em.getTransaction().commit();
		System.out.println("Size: " + size);
		return size != 0;
	}

	@SuppressWarnings("unchecked")
	public static Osoba getOsobaWithUsername(String username) {
		em = JPAUtil.getEntityManager();

		TypedQuery<Osoba> query = (TypedQuery<Osoba>) em
				.createQuery("SELECT o FROM Osoba o WHERE o.korisnickoIme LIKE :username");
		query.setParameter("username", username);
		try {
			return query.getSingleResult();
		} catch (Exception e) {

		}
		return null;
	}

	public static void saveOsoba(Osoba osoba, Fizijatar oldFizijatar,
			Fizijatar fizijatar, Fizioterapeut oldFizioterapeut,
			Fizioterapeut fizioterapeut) {

		em.getTransaction().begin();
		{
			osoba.getPacijent().setFizijatar(fizijatar);
			osoba.getPacijent().setFizioterapeut(fizioterapeut);
			osoba.setPacijent(em.merge(osoba.getPacijent()));
			osoba = em.merge(osoba);

			if (oldFizijatar != fizijatar) {
				oldFizijatar.removePacijent(osoba.getPacijent());
				fizijatar.addPacijent(osoba.getPacijent());

				fizijatar = em.merge(fizijatar);
				oldFizijatar = em.merge(oldFizijatar);
			}
			if (oldFizioterapeut != fizioterapeut) {
				oldFizioterapeut.removePacijent(osoba.getPacijent());
				fizioterapeut.addPacijent(osoba.getPacijent());

				fizioterapeut = em.merge(fizioterapeut);
				oldFizioterapeut = em.merge(oldFizioterapeut);
			}
		}
		em.getTransaction().commit();
	}

}
