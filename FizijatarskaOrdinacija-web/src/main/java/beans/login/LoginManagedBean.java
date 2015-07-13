package beans.login;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import managers.OsobaManager;
import model.Osoba;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@SessionScoped
public class LoginManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private Osoba osoba;
	private String username;
	private String pass;

	public String logIn() 
	{
		Osoba osoba = OsobaManager.getOsobaWithUsername(username);
		if (osoba != null && osoba.getSifra().equals(pass)) 
		{
			this.osoba = osoba;
			// pacijent is logged in
			if (osoba.getPacijent() != null)
				return "page/pacijent/welcome.xhtml?faces-redirect=true";

			// fizijatar is logged in
			if (osoba.getRadnik().getFizijatar() != null)
				return "page/fizijatar/welcome.xhtml?faces-redirect=true";

			// fizioterapeut is logged in
			if (osoba.getRadnik().getFizioterapeut() != null)
				return "page/fizioterapeut/welcomeFizioterapeut.xhtml?faces-redirect=true";

			// sestra is logged in
			if (osoba.getRadnik().getSestra() != null)
				return "page/sestra/welcomeSestra.xhtml?faces-redirect=true";
		} 
		else {
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - LogIn", "Uneto je nepostojece korisnicko ime ili pogresna lozinka");
		}
		return null;
	}
	
	public String logOut() {
		removeOldSessions();
		return "/index.xhtml?faces-redirect=true";
	}

	private void removeOldSessions() {
		removeSessionBean("loginManagedBean");
		removeSessionBean("welcomeFizijatarManagedBean");
		removeSessionBean("welcomeFizioterapeutManagedBean ");
		removeSessionBean("welcomeSestraManagedBean ");
	}

	public static void removeSessionBean(String name) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.removeAttribute(name);
	}

	public static void addMessage(Severity type, String summary, String detail) {
		FacesMessage message = new FacesMessage(type, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	
	
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Osoba getOsoba() {
		return osoba;
	}

	public void setOsoba(Osoba osoba) {
		this.osoba = osoba;
	}

}
