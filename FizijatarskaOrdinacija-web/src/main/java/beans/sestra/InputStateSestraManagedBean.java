package beans.sestra;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import beans.login.LoginManagedBean;
import managers.RadnikManager;
import model.Pacijent;
import model.Sestra;

@ManagedBean
@SessionScoped
public class InputStateSestraManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Pacijent pacijent;
	private Sestra sestra;
	private String temp;
	private String disanje;
	private String pritisak;

	@ManagedProperty("#{welcomeSestraManagedBean}")
	private WelcomeSestraManagedBean welcomeSestraManagedBean;

	@PostConstruct
	public void init() {
		sestra = welcomeSestraManagedBean.getSestra().getRadnik().getSestra();
		pacijent = welcomeSestraManagedBean.getPacijent();
	}

	public void saveState() {
		if (RadnikManager.saveState(pacijent, sestra, temp, disanje, pritisak))
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
					"Info - unos", "Dnevno stanje je sacuvano");
		else
			LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
					"Greska - unos", "Nije sacuvano dnevno stanjes");
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public Sestra getSestra() {
		return sestra;
	}

	public void setSestra(Sestra sestra) {
		this.sestra = sestra;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getDisanje() {
		return disanje;
	}

	public void setDisanje(String disanje) {
		this.disanje = disanje;
	}

	public String getPritisak() {
		return pritisak;
	}

	public void setPritisak(String pritisak) {
		this.pritisak = pritisak;
	}

	public WelcomeSestraManagedBean getWelcomeSestraManagedBean() {
		return welcomeSestraManagedBean;
	}

	public void setWelcomeSestraManagedBean(
			WelcomeSestraManagedBean welcomeSestraManagedBean) {
		this.welcomeSestraManagedBean = welcomeSestraManagedBean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
