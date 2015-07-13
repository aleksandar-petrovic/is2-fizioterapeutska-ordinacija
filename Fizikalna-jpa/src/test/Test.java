package test;

import java.util.Date;

import javax.persistence.EntityManager;

import managers.JPAUtil;
import model.Fizijatar;
import model.Fizioterapeut;
import model.Osoba;
import model.Radnik;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class Test {

	static EntityManager em = JPAUtil.getEntityManager();

	public static void main(String[] args) {
		//
		// Osoba osoba = em.find(Osoba.class, 8);
		// System.out.println(osoba.getIme() + " " + osoba.getPrezime());
		// System.out.println(osoba.getRadnik().getFizioterapeut().getSpec());
		//
		// Fizioterapeut fizioterapeut = osoba.getRadnik().getFizioterapeut();
		//
		// System.out.println(fizioterapeut.getRadnik().getOsoba().getIme());
		//
		// System.out.println("Gotovo");

		test1();
		test2();
		test3();
	}
	
	private static void test3() // kreira admina
	{
		Osoba osoba = new Osoba();
		osoba.setIme("Milan");
		osoba.setPrezime("Pavlovic");
		osoba.setKorisnickoIme("mp");
		osoba.setSifra("1");
		em.persist(osoba);

		Radnik radnik = new Radnik();
		radnik.setId(osoba.getId());
		radnik.setOsoba(osoba);
		radnik.setDatumZaposlenja(new Date());
		radnik.setAdmin(true);
		em.persist(radnik);

		Fizioterapeut fizioterapeut = new Fizioterapeut();
		fizioterapeut.setId(radnik.getId());
		fizioterapeut.setSpec("Grudi");
		fizioterapeut.setRadnik(radnik);
		em.persist(fizioterapeut);

		osoba.setRadnik(radnik);
		radnik.setFizioterapeut(fizioterapeut);
		em.merge(osoba);
		em.merge(radnik);
	}

	// persist fizijatar
	private static void test1() {
		em.getTransaction().begin();
		{
			Osoba osoba = new Osoba();
			osoba.setIme("Jelena");
			osoba.setPrezime("Krstic");
			osoba.setKorisnickoIme("jeca");
			osoba.setSifra("1");
			em.persist(osoba);

			Radnik radnik = new Radnik();
			radnik.setId(osoba.getId());
			radnik.setOsoba(osoba);
			radnik.setDatumZaposlenja(new Date());
			em.persist(radnik);

			Fizijatar fizijatar = new Fizijatar();
			fizijatar.setId(radnik.getId());
			fizijatar.setSpec("Kicma");
			fizijatar.setRadnik(radnik);
			em.persist(fizijatar);

			osoba.setRadnik(radnik);
			radnik.setFizijatar(fizijatar);
			em.persist(osoba);
			em.persist(radnik);
		}
		em.getTransaction().commit();
	}

	// persist fizioterapeut
	private static void test2() {
		em.getTransaction().begin();
		{
			Osoba osoba = new Osoba();
			osoba.setIme("Miroslav");
			osoba.setPrezime("Pavlovic");
			osoba.setKorisnickoIme("mica");
			osoba.setSifra("1");
			em.persist(osoba);

			Radnik radnik = new Radnik();
			radnik.setId(osoba.getId());
			radnik.setOsoba(osoba);
			radnik.setDatumZaposlenja(new Date());
			em.persist(radnik);

			Fizioterapeut fizioterapeut = new Fizioterapeut();
			fizioterapeut.setId(radnik.getId());
			fizioterapeut.setSpec("Rame");
			fizioterapeut.setRadnik(radnik);
			em.persist(fizioterapeut);

			osoba.setRadnik(radnik);
			radnik.setFizioterapeut(fizioterapeut);
			em.merge(osoba);
			em.merge(radnik);
		}
		em.getTransaction().commit();
	}
}
