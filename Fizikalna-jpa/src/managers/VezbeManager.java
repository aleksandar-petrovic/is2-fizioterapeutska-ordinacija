package managers;

import javax.persistence.EntityManager;

import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import model.Stanje;
import model.Vezbe;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class VezbeManager {
	private static EntityManager em = JPAUtil.getEntityManager();

	public static void updateVezbe(Vezbe vezbe) {
		em.getTransaction().begin();
		{
			vezbe = em.merge(vezbe);
		}
		em.getTransaction().commit();
	}

	public static void createVezbe(Obaveza newObaveza, Pacijent pacijent,
			Pregled pregled) {
		em.getTransaction().begin();
		{
			Stanje stanje = new Stanje();
			em.persist(stanje);

			Vezbe vezbe = new Vezbe();
			vezbe.setId(newObaveza.getId());
			vezbe.setStanje(stanje);
			vezbe.setKarton(pacijent.getKarton());
			vezbe.setTerapija(pregled.getTerapija());
			em.persist(vezbe);

			newObaveza.setVezbe(vezbe);
			pregled.getTerapija().addVezbe(vezbe);
			em.merge(newObaveza);
			em.merge(pregled);
		}
		em.getTransaction().commit();
	}
}
