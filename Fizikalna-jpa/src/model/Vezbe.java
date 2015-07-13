package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vezbe database table.
 * 
 */
@Entity
@NamedQuery(name="Vezbe.findAll", query="SELECT v FROM Vezbe v")
public class Vezbe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String program;

	//bi-directional one-to-one association to Obaveza
	@OneToOne
	@JoinColumn(name="id")
	private Obaveza obaveza;

	//bi-directional many-to-one association to Karton
	@ManyToOne
	private Karton karton;

	//bi-directional many-to-one association to Terapija
	@ManyToOne
	private Terapija terapija;

	//bi-directional many-to-one association to Stanje
	@ManyToOne
	private Stanje stanje;

	public Vezbe() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProgram() {
		return this.program;
	}

	public void setProgram(String program) {
		this.program = program;
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

	public Terapija getTerapija() {
		return this.terapija;
	}

	public void setTerapija(Terapija terapija) {
		this.terapija = terapija;
	}

	public Stanje getStanje() {
		return this.stanje;
	}

	public void setStanje(Stanje stanje) {
		this.stanje = stanje;
	}

}