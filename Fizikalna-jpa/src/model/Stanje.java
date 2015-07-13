package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the stanje database table.
 * 
 */
@Entity
@NamedQuery(name="Stanje.findAll", query="SELECT s FROM Stanje s")
public class Stanje implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date datum;

	private String opisStanja;

	//bi-directional many-to-one association to Pregled
	@OneToMany(mappedBy="stanje")
	private List<Pregled> pregleds;

	//bi-directional many-to-one association to Vezbe
	@OneToMany(mappedBy="stanje")
	private List<Vezbe> vezbes;

	public Stanje() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDatum() {
		return this.datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getOpisStanja() {
		return this.opisStanja;
	}

	public void setOpisStanja(String opisStanja) {
		this.opisStanja = opisStanja;
	}

	public List<Pregled> getPregleds() {
		return this.pregleds;
	}

	public void setPregleds(List<Pregled> pregleds) {
		this.pregleds = pregleds;
	}

	public Pregled addPregled(Pregled pregled) {
		getPregleds().add(pregled);
		pregled.setStanje(this);

		return pregled;
	}

	public Pregled removePregled(Pregled pregled) {
		getPregleds().remove(pregled);
		pregled.setStanje(null);

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
		vezbe.setStanje(this);

		return vezbe;
	}

	public Vezbe removeVezbe(Vezbe vezbe) {
		getVezbes().remove(vezbe);
		vezbe.setStanje(null);

		return vezbe;
	}

}