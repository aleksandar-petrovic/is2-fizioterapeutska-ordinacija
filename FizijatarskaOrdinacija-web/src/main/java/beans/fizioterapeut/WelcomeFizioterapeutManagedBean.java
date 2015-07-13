package beans.fizioterapeut;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import managers.FizioterapeutManager;
import managers.PacijentManager;
import model.Osoba;
import model.Pacijent;
import beans.login.LoginManagedBean;

/**
 * 
 * @author MP101
 *
 */

@ManagedBean
@SessionScoped
public class WelcomeFizioterapeutManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private Osoba fizioterapeut;
	private Pacijent pacijent;
	private String pacijentString; // treba zbog pretrage

	
	@ManagedProperty("#{loginManagedBean}")
	private LoginManagedBean loginManagedbean;

	
	@PostConstruct
	public void init() {
		this.fizioterapeut = loginManagedbean.getOsoba();
	}

	
	public String showObaveze() {
		return "ftAppointments.xhtml?faces-redirect=true";
	}

	public String adminPage() {
		return "/page/admin/welcome.xhtml?faces-redirect=true";
	}

	
	public void updateFizioterapeut() 
	{
		if (FizioterapeutManager.updateFizioterapeutInfo(fizioterapeut))
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
				"Info - Unos", "Izmene su sacuvane");
		else
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_FATAL, 
				"Greska - Unos", "Nisu sacivane izmene, prekinuta je transkacija, ponovite");
	}

	public String findPacijent() 
	{
		LoginManagedBean.removeSessionBean("pacijentProfileManagedBean");

		pacijent = PacijentManager.getOsobaByJMBGOrUsernameFizioterapeut(
				fizioterapeut, pacijentString);
		
		if (pacijent != null) 
		{
			pacijentString = "";
			return "pacijentProfile.xhtml?faces-redirect=true";
		} else
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Pretraga", "Pacijent ne postoji ili nije vas pacijent");
		return null;
	}


	public Osoba getFizioterapeut() {
		return fizioterapeut;
	}

	public void setFizioterapeut(Osoba fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public String getPacijentString() {
		return pacijentString;
	}

	public void setPacijentString(String pacijentString) {
		this.pacijentString = pacijentString;
	}

	public LoginManagedBean getLoginManagedbean() {
		return loginManagedbean;
	}

	public void setLoginManagedbean(LoginManagedBean loginManagedbean) {
		this.loginManagedbean = loginManagedbean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
