package reportModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Pacijent;

public class PacijentFizijatarModel {

	private int idFizijatar;
	private String ime;
	private String prezime;
	private String jmbg;
	private String mesto;
	private String adresa;
	private String imeFizijatar;
	private String prezimeFizijatar;

	public static List<PacijentFizijatarModel> convertPacijent(
			List<Pacijent> list) {

		Collections.sort(list, cmp);

		List<PacijentFizijatarModel> newList = new LinkedList<PacijentFizijatarModel>();
		for (Pacijent pacijent : list) {
			PacijentFizijatarModel model = new PacijentFizijatarModel();

			model.setAdresa(pacijent.getOsoba().getAdresa());
			model.setIdFizijatar(pacijent.getFizijatar().getId());
			model.setIme(pacijent.getOsoba().getIme());
			model.setImeFizijatar(pacijent.getFizijatar().getRadnik()
					.getOsoba().getIme());
			model.setPrezime(pacijent.getOsoba().getPrezime());
			model.setPrezimeFizijatar(pacijent.getFizijatar().getRadnik()
					.getOsoba().getPrezime());
			model.setMesto(pacijent.getOsoba().getMesto());
			model.setJmbg(pacijent.getOsoba().getJmbg());
			newList.add(model);
		}
		return newList;
	}

	static Comparator<Pacijent> cmp = new Comparator<Pacijent>() {
		@Override
		public int compare(Pacijent p1, Pacijent p2) {
			return p1.getFizijatar().getId() - p2.getFizijatar().getId();
		}
	};

	public int getIdFizijatar() {
		return idFizijatar;
	}

	public void setIdFizijatar(int idFizijatar) {
		this.idFizijatar = idFizijatar;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getMesto() {
		return mesto;
	}

	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	public String getAdresa() {
		return adresa;
	}

	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	public String getImeFizijatar() {
		return imeFizijatar;
	}

	public void setImeFizijatar(String imeFizijatar) {
		this.imeFizijatar = imeFizijatar;
	}

	public String getPrezimeFizijatar() {
		return prezimeFizijatar;
	}

	public void setPrezimeFizijatar(String prezimeFizijatar) {
		this.prezimeFizijatar = prezimeFizijatar;
	}

}
