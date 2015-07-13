package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the obaveza database table.
 * 
 */
@Entity
@NamedQuery(name = "Obaveza.findAll", query = "SELECT o FROM Obaveza o")
public class Obaveza implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date datum;

	private String naziv;

	private String opis;

	private String vremeOd;

	private String vremeDo;

	// bi-directional many-to-one association to Fizijatar
	@ManyToOne
	private Fizijatar fizijatar;

	// bi-directional many-to-one association to Fizioterapeut
	@ManyToOne
	private Fizioterapeut fizioterapeut;

	// bi-directional one-to-one association to Pregled
	@OneToOne(mappedBy = "obaveza")
	private Pregled pregled;

	// bi-directional one-to-one association to Vezbe
	@OneToOne(mappedBy = "obaveza")
	private Vezbe vezbe;

	public Obaveza() {
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

	public String getNaziv() {
		return this.naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public String getOpis() {
		return this.opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
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

	public Pregled getPregled() {
		return this.pregled;
	}

	public String getVremeOd() {
		return vremeOd;
	}

	public void setVremeOd(String vremeOd) {
		this.vremeOd = vremeOd;
	}

	public String getVremeDo() {
		return vremeDo;
	}

	public void setVremeDo(String vremeDo) {
		this.vremeDo = vremeDo;
	}

	public void setPregled(Pregled pregled) {
		this.pregled = pregled;
	}

	public Vezbe getVezbe() {
		return this.vezbe;
	}

	public void setVezbe(Vezbe vezbe) {
		this.vezbe = vezbe;
	}

}