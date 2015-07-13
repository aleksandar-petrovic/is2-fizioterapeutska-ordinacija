package beans.fizijatar;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import managers.FizijatarManager;
import managers.PacijentManager;
import model.Osoba;
import model.Pacijent;
import beans.login.LoginManagedBean;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@SessionScoped
public class WelcomeFizijatarManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private Osoba osoba; // this is logged fizijatar
	private String pacijentString;
	private Pacijent pacijent;
	

	@ManagedProperty("#{loginManagedBean}")
	private LoginManagedBean loginManagedBean;

	
	@PostConstruct
	private void init() {
		osoba = loginManagedBean.getOsoba();
	}

	public void updateFizijatar() {
		FizijatarManager.updateFizijatar(osoba);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
				"Info - Unos", "Izmene vaseg profila su sacuvane");
	}

	public String showPacijent() 
	{
		// if already exist instance of this session bean
		LoginManagedBean.removeSessionBean("editPacijentManagedBean");

		pacijent = PacijentManager.getOsobaByJMBGOrUsernameFizijatar(osoba,
				pacijentString);

		if (pacijent != null) {
			pacijentString = "";
			return "pacijentProfile.xhtml?faces-redirect=true";
		}

		LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
				"Greska - Pretraga", "Pacijent ne postoji ili nije vas pacijent");
		
		return null;
	}

	public String showObaveze() {
		return "myAppointments.xhtml?faces-redirect=true";
	}

	public String adminPage() {
		return "/page/admin/welcome.xhtml?faces-redirect=true";
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

	public Osoba getOsoba() {
		return osoba;
	}

	public void setOsoba(Osoba osoba) {
		this.osoba = osoba;
	}

	public LoginManagedBean getLoginManagedBean() {
		return loginManagedBean;
	}

	public void setLoginManagedBean(LoginManagedBean loginManagedBean) {
		this.loginManagedBean = loginManagedBean;
	}
}
