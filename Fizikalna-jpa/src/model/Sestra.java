package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the sestra database table.
 * 
 */
@Entity
@NamedQuery(name = "Sestra.findAll", query = "SELECT s FROM Sestra s")
public class Sestra implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String specijalnost;

	// bi-directional many-to-one association to Dnevnostanje
	@OneToMany(mappedBy = "sestra")
	private List<Dnevnostanje> dnevnostanjes;

	// bi-directional one-to-one association to Radnik
	@OneToOne
	@JoinColumn(name = "id")
	private Radnik radnik;

	public Sestra() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpecijalnost() {
		return this.specijalnost;
	}

	public void setSpecijalnost(String specijalnost) {
		this.specijalnost = specijalnost;
	}

	public List<Dnevnostanje> getDnevnostanjes() {
		return this.dnevnostanjes;
	}

	public void setDnevnostanjes(List<Dnevnostanje> dnevnostanjes) {
		this.dnevnostanjes = dnevnostanjes;
	}

	public Dnevnostanje addDnevnostanje(Dnevnostanje dnevnostanje) {
		getDnevnostanjes().add(dnevnostanje);
		dnevnostanje.setSestra(this);

		return dnevnostanje;
	}

	public Dnevnostanje removeDnevnostanje(Dnevnostanje dnevnostanje) {
		getDnevnostanjes().remove(dnevnostanje);
		dnevnostanje.setSestra(null);

		return dnevnostanje;
	}

	public Radnik getRadnik() {
		return this.radnik;
	}

	public void setRadnik(Radnik radnik) {
		this.radnik = radnik;
	}

}