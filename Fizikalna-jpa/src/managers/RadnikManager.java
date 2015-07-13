package managers;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import model.Dnevnostanje;
import model.Fizijatar;
import model.Fizioterapeut;
import model.Karton;
import model.Osoba;
import model.Pacijent;
import model.Radnik;
import model.Sestra;

/**
 * 
 * @author MP101
 *
 */
public class RadnikManager {

	private static EntityManager em = JPAUtil.getEntityManager();

	public static boolean registerRadnik(String korisnicko, String ime,
			String prez, String email, String loz, String prof, String spec,
			boolean admin) {

		em = JPAUtil.getEntityManager();

		if (OsobaManager.getOsobaWithUsername(korisnicko) != null)
			return false; // false, ako postoji osoba sa takvim korsinickim
							// imenom

		try
		{
			em.getTransaction().begin();
			{
				Osoba osoba = new Osoba();
				osoba.setKorisnickoIme(korisnicko);
				osoba.setIme(ime);
				osoba.setPrezime(prez);
				osoba.setEmail(email);
				osoba.setSifra(loz);
				em.persist(osoba);

				Radnik radnik = new Radnik();
				radnik.setId(osoba.getId());
				radnik.setOsoba(osoba);
				radnik.setAdmin(admin); // jer je tinyInt u bazi
				radnik.setDatumZaposlenja(new Date());
				em.persist(radnik);

				switch (prof) {
				case "sestra":
					Sestra s = new Sestra();
					s.setId(radnik.getId());
					s.setRadnik(radnik);
					s.setSpecijalnost(spec);
					em.persist(s);

					radnik.setSestra(s);
					break;
				case "fizioterapeut":
					Fizioterapeut ft = new Fizioterapeut();
					ft.setId(radnik.getId());
					ft.setRadnik(radnik);
					ft.setSpec(spec);
					em.persist(ft);
				
					radnik.setFizioterapeut(ft);
					break;
				case "fizijatar":
					Fizijatar fz = new Fizijatar();
					fz.setId(radnik.getId());
					fz.setRadnik(radnik);
					fz.setSpec(spec);
					em.persist(fz);
				
					radnik.setFizijatar(fz);
					break;
				default:
					return false;
				}
				
				osoba.setRadnik(radnik);
				em.merge(osoba.getRadnik());
				osoba = em.merge(osoba);
			}
			em.getTransaction().commit();
		}
		catch(Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static void saveSestra(Osoba osoba) {
		try {
			em.getTransaction().begin();
			{
				em.merge(osoba);
				em.merge(osoba.getRadnik());
				em.merge(osoba.getRadnik().getSestra());
				osoba = em.merge(osoba);
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static Pacijent findPacijent(String ident) {
		try {
			Osoba osoba = null;
			em.getTransaction().begin();
			{
				TypedQuery<Osoba> upit = (TypedQuery<Osoba>) em
						.createQuery("SELECT o FROM Osoba o "
								+ "WHERE (o.korisnickoIme = :ident OR o.jmbg = :ident) "
								+ "AND o.pacijent.id = o.id"); // mora biti
																// pacijent, a
																// ne radnik
				upit.setParameter("ident", ident);
				osoba = upit.getSingleResult();
			}
			em.getTransaction().commit();
			return osoba.getPacijent();
		} catch (Exception ex) {
			em.getTransaction().rollback();
		}
		return null;
	}

	// Sacuvaj dnevno stanje i azuriraj karton pacijenta
	public static boolean saveState(Pacijent pacijent, Sestra sestra,
			String temp, String disanje, String pritisak) {
		try {
			em.getTransaction().begin();
			{
				Dnevnostanje ds = new Dnevnostanje();
				ds.setDisanje(disanje);
				ds.setPritisak(pritisak);
				ds.setTemperatura(temp);
				ds.setSestra(sestra);
				ds.setKarton(pacijent.getKarton());
				ds.setDatum(new Date());
				em.persist(ds);

				Karton k = pacijent.getKarton();
				k.addDnevnostanje(ds);
				em.merge(k);

				em.merge(pacijent.getOsoba()); // cudnovato
				pacijent = em.merge(pacijent);
				
				
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			return false;
		}
		return true;
	}
}
