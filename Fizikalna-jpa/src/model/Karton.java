package model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;


/**
 * The persistent class for the karton database table.
 * 
 */
@Entity
@NamedQuery(name="Karton.findAll", query="SELECT k FROM Karton k")
public class Karton implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String alergija;

	private String bolesti;

	private String napomena;

	private String sistematski;

	//bi-directional many-to-one association to Dnevnostanje
	@OneToMany(mappedBy="karton")
	private List<Dnevnostanje> dnevnostanjes;

	//bi-directional many-to-one association to Pacijent
	@OneToMany(mappedBy="karton")
	private List<Pacijent> pacijents;

	//bi-directional many-to-one association to Pregled
	@OneToMany(mappedBy="karton")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Pregled> pregleds;

	//bi-directional many-to-one association to Vezbe
	@OneToMany(mappedBy="karton")
	private List<Vezbe> vezbes;

	public Karton() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlergija() {
		return this.alergija;
	}

	public void setAlergija(String alergija) {
		this.alergija = alergija;
	}

	public String getBolesti() {
		return this.bolesti;
	}

	public void setBolesti(String bolesti) {
		this.bolesti = bolesti;
	}

	public String getNapomena() {
		return this.napomena;
	}

	public void setNapomena(String napomena) {
		this.napomena = napomena;
	}

	public String getSistematski() {
		return this.sistematski;
	}

	public void setSistematski(String sistematski) {
		this.sistematski = sistematski;
	}

	public List<Dnevnostanje> getDnevnostanjes() {
		return this.dnevnostanjes;
	}

	public void setDnevnostanjes(List<Dnevnostanje> dnevnostanjes) {
		this.dnevnostanjes = dnevnostanjes;
	}

	public Dnevnostanje addDnevnostanje(Dnevnostanje dnevnostanje) {
		getDnevnostanjes().add(dnevnostanje);
		dnevnostanje.setKarton(this);

		return dnevnostanje;
	}

	public Dnevnostanje removeDnevnostanje(Dnevnostanje dnevnostanje) {
		getDnevnostanjes().remove(dnevnostanje);
		dnevnostanje.setKarton(null);

		return dnevnostanje;
	}

	public List<Pacijent> getPacijents() {
		return this.pacijents;
	}

	public void setPacijents(List<Pacijent> pacijents) {
		this.pacijents = pacijents;
	}

	public Pacijent addPacijent(Pacijent pacijent) {
		getPacijents().add(pacijent);
		pacijent.setKarton(this);

		return pacijent;
	}

	public Pacijent removePacijent(Pacijent pacijent) {
		getPacijents().remove(pacijent);
		pacijent.setKarton(null);

		return pacijent;
	}

	public List<Pregled> getPregleds() {
		return this.pregleds;
	}

	public void setPregleds(List<Pregled> pregleds) {
		this.pregleds = pregleds;
	}

	public Pregled addPregled(Pregled pregled) {
		getPregleds().add(pregled);
		pregled.setKarton(this);

		return pregled;
	}

	public Pregled removePregled(Pregled pregled) {
		getPregleds().remove(pregled);
		pregled.setKarton(null);

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
		vezbe.setKarton(this);

		return vezbe;
	}

	public Vezbe removeVezbe(Vezbe vezbe) {
		getVezbes().remove(vezbe);
		vezbe.setKarton(null);

		return vezbe;
	}

}