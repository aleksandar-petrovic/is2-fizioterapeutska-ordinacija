package beans.sestra;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import beans.login.LoginManagedBean;
import managers.RadnikManager;
import model.Osoba;
import model.Pacijent;

/**
 * 
 * @author MilanPavlovic MP101
 *
 */

@ManagedBean
@SessionScoped
public class WelcomeSestraManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Osoba sestra;
	private Pacijent pacijent;
	private String pacijentString;

	@ManagedProperty("#{loginManagedBean}")
	private LoginManagedBean loginManagedBean;

	@PostConstruct
	private void init() {
		sestra = loginManagedBean.getOsoba(); // uzima osobu koja se ulogovala
	}

	public String showMyProfile() {
		return "showSestraProfile.xhtml?faces-redirect=true";
	}

	public void updateSestra() {
		RadnikManager.saveSestra(sestra); // izmenjena osoba koja je sestra
	}

	public String showPacijent() {
		pacijent = RadnikManager.findPacijent(pacijentString);

		if (pacijent != null) {
			pacijentString = "";
			return "pacijentProfile.xhtml?faces-redirect=true";
		} 
		else {
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Pretraga", "Pacijent ne postoji u bazi");
			return null;
		}
	}

	public String adminPage() {
		return "/page/admin/welcome.xhtml?faces-redirect=true";
	}

	public Osoba getSestra() {
		return sestra;
	}

	public void setSestra(Osoba sestra) {
		this.sestra = sestra;
	}

	public String getPacijentString() {
		return pacijentString;
	}

	public void setPacijentString(String pacijentString) {
		this.pacijentString = pacijentString;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public LoginManagedBean getLoginManagedBean() {
		return loginManagedBean;
	}

	public void setLoginManagedBean(LoginManagedBean loginManagedBean) {
		this.loginManagedBean = loginManagedBean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
