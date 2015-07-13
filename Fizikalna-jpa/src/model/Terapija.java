package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the terapija database table.
 * 
 */
@Entity
@NamedQuery(name="Terapija.findAll", query="SELECT t FROM Terapija t")
public class Terapija implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String opis;

	//bi-directional many-to-one association to Pregled
	@OneToMany(mappedBy="terapija")
	private List<Pregled> pregleds;

	//bi-directional many-to-one association to Vezbe
	@OneToMany(mappedBy="terapija")
	private List<Vezbe> vezbes;

	public Terapija() {
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
		pregled.setTerapija(this);

		return pregled;
	}

	public Pregled removePregled(Pregled pregled) {
		getPregleds().remove(pregled);
		pregled.setTerapija(null);

		return pregled;
	}

	public List<Vezbe> getVezbes() {
		return this.vezbes;
	}

	public void setVezbes(List<Vezbe> vezbes) {
		this.vezbes = vezbes;
	}

	public Vezbe addVezbe(Vezbe vezbe) {
		getVezbes().add(vezbe);
		vezbe.setTerapija(this);

		return vezbe;
	}

	public Vezbe removeVezbe(Vezbe vezbe) {
		getVezbes().remove(vezbe);
		vezbe.setTerapija(null);

		return vezbe;
	}

}