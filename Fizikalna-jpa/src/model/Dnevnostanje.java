package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the dnevnostanje database table.
 * 
 */
@Entity
@NamedQuery(name="Dnevnostanje.findAll", query="SELECT d FROM Dnevnostanje d")
public class Dnevnostanje implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date datum;

	private String disanje;

	private String pritisak;

	private String temperatura;

	//bi-directional many-to-one association to Karton
	@ManyToOne
	private Karton karton;

	//bi-directional many-to-one association to Sestra
	@ManyToOne
	private Sestra sestra;

	public Dnevnostanje() {
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

	public String getDisanje() {
		return this.disanje;
	}

	public void setDisanje(String disanje) {
		this.disanje = disanje;
	}

	public String getPritisak() {
		return this.pritisak;
	}

	public void setPritisak(String pritisak) {
		this.pritisak = pritisak;
	}

	public String getTemperatura() {
		return this.temperatura;
	}

	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}

	public Karton getKarton() {
		return this.karton;
	}

	public void setKarton(Karton karton) {
		this.karton = karton;
	}

	public Sestra getSestra() {
		return this.sestra;
	}

	public void setSestra(Sestra sestra) {
		this.sestra = sestra;
	}

}