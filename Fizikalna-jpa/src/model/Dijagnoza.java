package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dijagnoza database table.
 * 
 */
@Entity
@NamedQuery(name="Dijagnoza.findAll", query="SELECT d FROM Dijagnoza d")
public class Dijagnoza implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String opis;

	//bi-directional many-to-one association to Pregled
	@OneToMany(mappedBy="dijagnoza")
	private List<Pregled> pregleds;

	public Dijagnoza() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOpis() {
		return this.opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public List<Pregled> getPregleds() {
		return this.pregleds;
	}

	public void setPregleds(List<Pregled> pregleds) {
		this.pregleds = pregleds;
	}

	public Pregled addPregled(Pregled pregled) {
		getPregleds().add(pregled);
		pregled.setDijagnoza(this);

		return pregled;
	}

	public Pregled removePregled(Pregled pregled) {
		getPregleds().remove(pregled);
		pregled.setDijagnoza(null);

		return pregled;
	}

}