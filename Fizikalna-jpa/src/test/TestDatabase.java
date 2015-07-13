package test;

import java.util.Date;

import javax.persistence.EntityManager;

import managers.JPAUtil;
import model.Fizijatar;
import model.Osoba;
import model.Radnik;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDatabase {

	private EntityManager em;

	@Before
	public void init() {
		em = JPAUtil.getEntityManager();
	}

	@Test
	// example of persisting fizijatar in database
	public void test1() {

		em.getTransaction().begin();
		Osoba osoba = new Osoba();
		osoba.setIme("David");
		osoba.setPrezime("Hlapovic");
		em.persist(osoba);

		Radnik radnik = new Radnik();
		radnik.setId(osoba.getId());
		radnik.setOsoba(osoba);
		radnik.setDatumZaposlenja(new Date());
		em.persist(radnik);

		Fizijatar fizijatar = new Fizijatar();
		fizijatar.setId(radnik.getId());
		fizijatar.setSpec("Ruke");
		fizijatar.setRadnik(radnik);
		em.persist(fizijatar);

		osoba.setRadnik(radnik);
		radnik.setFizijatar(fizijatar);
		em.persist(osoba);
		em.persist(radnik);
		em.getTransaction().commit();
		

		Fizijatar fizijatarInBase = em.find(Fizijatar.class, fizijatar.getId());

		Assert.assertNotNull(fizijatarInBase);
		Assert.assertEquals(fizijatar.getId(), fizijatarInBase.getId());
		Assert.assertEquals(fizijatar.getSpec(), fizijatarInBase.getSpec());
		Assert.assertNotNull(fizijatarInBase.getRadnik());
		Assert.assertNotNull(fizijatarInBase.getRadnik().getOsoba());
		Assert.assertEquals(osoba.getIme(), fizijatarInBase.getRadnik()
				.getOsoba().getIme());
	}

}
