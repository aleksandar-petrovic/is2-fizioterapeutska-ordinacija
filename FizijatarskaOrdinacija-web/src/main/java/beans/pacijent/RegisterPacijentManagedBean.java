package beans.pacijent;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import beans.login.LoginManagedBean;
import managers.PacijentManager;
import model.Fizijatar;
import model.Fizioterapeut;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@RequestScoped
public class RegisterPacijentManagedBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String pass1;
	private String pass2;
	private Fizijatar fizijatar;
	private Fizioterapeut fizioterapeut;

	public void register() 
	{
		// registration of pacijent
		if (!pass1.equals(pass2)) 
		{
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Register", "Lozinke nisu identicne");
			return;
		}

		if (PacijentManager.registerPacijent(username, pass1, fizijatar,
				fizioterapeut))
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
				"Info - Register", "Pacijent '"+username+"' je uspesno registrovan");
		else
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_ERROR, 
					"Greska - Register", "Korisnicko ime "+username+" vec postoji u bazi");
	}

	public Fizioterapeut getFizioterapeut() {
		return fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

	public Fizijatar getFizijatar() {
		return fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass1() {
		return pass1;
	}

	public void setPass1(String pass1) {
		this.pass1 = pass1;
	}

	public String getPass2() {
		return pass2;
	}

	public void setPass2(String pass2) {
		this.pass2 = pass2;
	}

}
