package managers;

import javax.persistence.EntityManager;

import model.Dijagnoza;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import model.Stanje;
import model.Terapija;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class PregledManager {
	private static EntityManager em = JPAUtil.getEntityManager();

	public static void createPregled(Obaveza obaveza, Pacijent pacijent) {
		em.getTransaction().begin();
		{
			Stanje stanje = new Stanje();
			em.persist(stanje);

			Dijagnoza dijagnoza = new Dijagnoza();
			em.persist(dijagnoza);

			Terapija terapija = new Terapija();
			em.persist(terapija);

			Pregled pregled = new Pregled();
			pregled.setId(obaveza.getId());
			pregled.setObaveza(obaveza);
			pregled.setKarton(pacijent.getKarton());
			pregled.setStanje(stanje);
			pregled.setDijagnoza(dijagnoza);
			pregled.setTerapija(terapija);

			em.persist(pregled);

			obaveza.setPregled(pregled);
			em.merge(obaveza);

			pacijent.getKarton().addPregled(obaveza.getPregled());
			em.merge(pacijent.getKarton());
			em.merge(pacijent);
			em.merge(pacijent.getOsoba());
		}
		em.getTransaction().commit();
	}

	public static void updatePregled(Pregled pregled) {
		em.getTransaction().begin();
		{
			em.merge(pregled.getStanje());
			em.merge(pregled.getDijagnoza());
			em.merge(pregled.getTerapija());
			pregled = em.merge(pregled);
		}
		em.getTransaction().commit();
	}

}
