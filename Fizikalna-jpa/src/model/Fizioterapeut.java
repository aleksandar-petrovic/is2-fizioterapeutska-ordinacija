package model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;

/**
 * The persistent class for the fizioterapeut database table.
 * 
 */
@Entity
@NamedQuery(name = "Fizioterapeut.findAll", query = "SELECT f FROM Fizioterapeut f")
public class Fizioterapeut implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String spec;

	// bi-directional one-to-one association to Radnik
	@OneToOne
	@JoinColumn(name = "id")
	private Radnik radnik;

	// bi-directional many-to-one association to Obaveza
	@OneToMany(mappedBy = "fizioterapeut")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Obaveza> obavezas;

	// bi-directional many-to-one association to Pacijent
	@OneToMany(mappedBy = "fizioterapeut")
	private List<Pacijent> pacijents;

	public Fizioterapeut() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpec() {
		return this.spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Radnik getRadnik() {
		return this.radnik;
	}

	public void setRadnik(Radnik radnik) {
		this.radnik = radnik;
	}

	public List<Obaveza> getObavezas() {
		return this.obavezas;
	}

	public void setObavezas(List<Obaveza> obavezas) {
		this.obavezas = obavezas;
	}

	public Obaveza addObaveza(Obaveza obaveza) {
		getObavezas().add(obaveza);
		obaveza.setFizioterapeut(this);

		return obaveza;
	}

	public Obaveza removeObaveza(Obaveza obaveza) {
		getObavezas().remove(obaveza);
		obaveza.setFizioterapeut(null);

		return obaveza;
	}

	public List<Pacijent> getPacijents() {
		return this.pacijents;
	}

	public void setPacijents(List<Pacijent> pacijents) {
		this.pacijents = pacijents;
	}

	public Pacijent addPacijent(Pacijent pacijent) {
		getPacijents().add(pacijent);
		pacijent.setFizioterapeut(this);

		return pacijent;
	}

	public Pacijent removePacijent(Pacijent pacijent) {
		getPacijents().remove(pacijent);
		pacijent.setFizioterapeut(null);

		return pacijent;
	}

}