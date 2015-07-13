package beans.admin;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import beans.login.LoginManagedBean;
import managers.RadnikManager;

/**
 * 
 * @author MP101
 *
 */

@ManagedBean
@RequestScoped
public class RegisterAdminManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String korisnicko;
	private String ime;
	private String prezime;
	private String email;
	private String lozinka;
	private String lozinkaPon;

	private String spec;
	private String profesija;

	private boolean admin;
	

	public void register() 
	{
		if (!lozinka.equals(lozinkaPon)) {
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Register", "Lozinke nisu identicne");
			return;
		}

		if (RadnikManager.registerRadnik(korisnicko, ime, prezime, email,
				lozinka, profesija, spec, admin)) 
		{
			System.out.println("REGISTROVAO RADNIKA U BAZU");
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO,
					"Info - Register", "Uspesno je zaposlen radnik "+ime+" "+prezime+" '"+korisnicko+"'");
		} 
		else
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Register", "Korisnicko ime '"+korisnicko+"' vec postoji u bazi");
	}

	
	public String getKorisnicko() {
		return korisnicko;
	}

	public void setKorisnicko(String korisnicko) {
		this.korisnicko = korisnicko;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public String getLozinkaPon() {
		return lozinkaPon;
	}

	public void setLozinkaPon(String lozinkaPon) {
		this.lozinkaPon = lozinkaPon;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getProfesija() {
		return profesija;
	}

	public void setProfesija(String profesija) {
		this.profesija = profesija;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isAdmin() {
		return admin;
	}

}
