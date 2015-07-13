package beans.pacijent;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import managers.OsobaManager;
import model.Fizijatar;
import model.Fizioterapeut;
import model.Osoba;
import beans.login.LoginManagedBean;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@RequestScoped
public class WelcomePacijentManagedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4662903355121126117L;
	private Osoba osoba;
	private Fizijatar fizijatar;
	private Fizioterapeut fizioterapeut;

	@ManagedProperty("#{loginManagedBean}")
	private LoginManagedBean loginManagedBean;

	// combobox doesn't put already choosed value!!! error!!!
	@PostConstruct
	private void init() {
		osoba = loginManagedBean.getOsoba();
		fizijatar = osoba.getPacijent().getFizijatar();
		fizioterapeut = osoba.getPacijent().getFizioterapeut();
	}

	public void updatePacijent() {
		Fizijatar oldFizijatar = osoba.getPacijent().getFizijatar();
		Fizioterapeut oldFizioterapeut = osoba.getPacijent().getFizioterapeut();
		OsobaManager.saveOsoba(osoba, oldFizijatar, fizijatar,
				oldFizioterapeut, fizioterapeut);

		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, "Info - Unos",
				"Promene su uspesno sacuvane");
	}

	public String showProfile() {
		return "showProfile.xhtml?faces-redirect=true";
	}

	
	
	public Fizioterapeut getFizioterapeut() {
		return fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

	public Osoba getOsoba() {
		return osoba;
	}

	public Fizijatar getFizijatar() {
		return fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
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
