package reportModel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.Fizijatar;

public class FizijatarModel {

	private Integer id;
	private String ime;
	private String prezime;
	private String spec;
	private Date datumZaposlenja;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Date getDatumZaposlenja() {
		return datumZaposlenja;
	}

	public void setDatumZaposlenja(Date datumZaposlenja) {
		this.datumZaposlenja = datumZaposlenja;
	}

	public static List<FizijatarModel> convertListOfFizijatarToFizijatarModel(
			List<Fizijatar> list) {
		List<FizijatarModel> nlist = new LinkedList<FizijatarModel>();
		for (Fizijatar fizijatar : list) {
			FizijatarModel fizijatarModel = new FizijatarModel();
			fizijatarModel.setId(fizijatar.getId());
			fizijatarModel.setDatumZaposlenja(fizijatar.getRadnik()
					.getDatumZaposlenja());
			fizijatarModel.setIme(fizijatar.getRadnik().getOsoba().getIme());
			fizijatarModel.setPrezime(fizijatar.getRadnik().getOsoba()
					.getPrezime());
			fizijatarModel.setSpec(fizijatar.getSpec());

			nlist.add(fizijatarModel);
		}

		return nlist;
	}

}
