package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the pacijent database table.
 * 
 */
@Entity
@NamedQuery(name="Pacijent.findAll", query="SELECT p FROM Pacijent p")
public class Pacijent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String brojZdravstveneKnjizice;

	//bi-directional one-to-one association to Osoba
	@OneToOne
	@JoinColumn(name="id")
	private Osoba osoba;

	//bi-directional many-to-one association to Fizijatar
	@ManyToOne
	private Fizijatar fizijatar;

	//bi-directional many-to-one association to Karton
	@ManyToOne
	private Karton karton;

	//bi-directional many-to-one association to Fizioterapeut
	@ManyToOne
	private Fizioterapeut fizioterapeut;

	public Pacijent() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBrojZdravstveneKnjizice() {
		return this.brojZdravstveneKnjizice;
	}

	public void setBrojZdravstveneKnjizice(String brojZdravstveneKnjizice) {
		this.brojZdravstveneKnjizice = brojZdravstveneKnjizice;
	}

	public Osoba getOsoba() {
		return this.osoba;
	}

	public void setOsoba(Osoba osoba) {
		this.osoba = osoba;
	}

	public Fizijatar getFizijatar() {
		return this.fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
	}

	public Karton getKarton() {
		return this.karton;
	}

	public void setKarton(Karton karton) {
		this.karton = karton;
	}

	public Fizioterapeut getFizioterapeut() {
		return this.fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

}