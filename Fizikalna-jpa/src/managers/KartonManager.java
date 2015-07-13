package managers;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Karton;
import model.Pacijent;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

public class KartonManager {
	private static EntityManager em = JPAUtil.getEntityManager();

	public static void main(String[] args) {
	}

	@SuppressWarnings("unchecked")
	public static Pacijent getPacijentForKatron(Karton karton) {
		TypedQuery<Pacijent> query = (TypedQuery<Pacijent>) em
				.createQuery("SELECT p FROM Pacijent p WHERE p.karton.id = :id");
		query.setParameter("id", karton.getId());
		return query.getSingleResult();
	}

}
