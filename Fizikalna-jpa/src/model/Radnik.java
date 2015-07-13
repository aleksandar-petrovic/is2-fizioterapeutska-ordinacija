package model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;

/**
 * The persistent class for the radnik database table.
 * 
 */
@Entity
@NamedQuery(name = "Radnik.findAll", query = "SELECT r FROM Radnik r")
public class Radnik implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private Boolean admin; // bio je referentni tip Boolean, zbog baze(tinyInt(4)) je int

	@Temporal(TemporalType.DATE)
	private Date datumZaposlenja;

	// bi-directional one-to-one association to Fizijatar
	@OneToOne(mappedBy = "radnik")
	private Fizijatar fizijatar;

	// bi-directional one-to-one association to Fizioterapeut
	@OneToOne(mappedBy = "radnik")
	private Fizioterapeut fizioterapeut;

	// bi-directional one-to-one association to Osoba
	@OneToOne
	@JoinColumn(name = "id")
	private Osoba osoba;

	// bi-directional one-to-one association to Sestra
	@OneToOne(mappedBy = "radnik")
	private Sestra sestra;

	public Radnik() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Date getDatumZaposlenja() {
		return this.datumZaposlenja;
	}

	public void setDatumZaposlenja(Date datumZaposlenja) {
		this.datumZaposlenja = datumZaposlenja;
	}

	public Fizijatar getFizijatar() {
		return this.fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
	}

	public Fizioterapeut getFizioterapeut() {
		return this.fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

	public Osoba getOsoba() {
		return this.osoba;
	}

	public void setOsoba(Osoba osoba) {
		this.osoba = osoba;
	}

	public Sestra getSestra() {
		return this.sestra;
	}

	public void setSestra(Sestra sestra) {
		this.sestra = sestra;
	}

}