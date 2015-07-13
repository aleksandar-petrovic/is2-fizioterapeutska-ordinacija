package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the pregled database table.
 * 
 */
@Entity
@NamedQuery(name="Pregled.findAll", query="SELECT p FROM Pregled p")
public class Pregled implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String napomena;

	//bi-directional one-to-one association to Obaveza
	@OneToOne
	@JoinColumn(name="id")
	private Obaveza obaveza;

	//bi-directional many-to-one association to Karton
	@ManyToOne
	private Karton karton;

	//bi-directional many-to-one association to Dijagnoza
	@ManyToOne
	private Dijagnoza dijagnoza;

	//bi-directional many-to-one association to Stanje
	@ManyToOne
	private Stanje stanje;

	//bi-directional many-to-one association to Terapija
	@ManyToOne
	private Terapija terapija;

	public Pregled() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNapomena() {
		return this.napomena;
	}

	public void setNapomena(String napomena) {
		this.napomena = napomena;
	}

	public Obaveza getObaveza() {
		return this.obaveza;
	}

	public void setObaveza(Obaveza obaveza) {
		this.obaveza = obaveza;
	}

	public Karton getKarton() {
		return this.karton;
	}

	public void setKarton(Karton karton) {
		this.karton = karton;
	}

	public Dijagnoza getDijagnoza() {
		return this.dijagnoza;
	}

	public void setDijagnoza(Dijagnoza dijagnoza) {
		this.dijagnoza = dijagnoza;
	}

	public Stanje getStanje() {
		return this.stanje;
	}

	public void setStanje(Stanje stanje) {
		this.stanje = stanje;
	}

	public Terapija getTerapija() {
		return this.terapija;
	}

	public void setTerapija(Terapija terapija) {
		this.terapija = terapija;
	}

}