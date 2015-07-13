package beans.fizijatar;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import beans.login.LoginManagedBean;
import managers.KartonManager;
import managers.PacijentManager;
import managers.PregledManager;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@RequestScoped
public class ShowPregledManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private String from;
	private Obaveza obaveza;
	private Pregled pregled;
	private Pacijent pacijent;

	@PostConstruct
	private void init() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();

		this.obaveza = (Obaveza) map.get("obaveza");
		this.pregled = obaveza.getPregled();
		this.pacijent = KartonManager.getPacijentForKatron(pregled.getKarton());
		this.from = (String) map.get("from");
	}

	public void updatePacijentKarton() {
		PacijentManager.updateKarton(pacijent);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, "Info - Unos",
				"Uspesno ste izmenili karton");
	}

	public void updatePregled() {
		PregledManager.updatePregled(pregled);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, "Info - Unos",
				"Uspesno ste sacuvali pregled");
	}

	/*
	 * usage of field "from"
	 * 
	 * if fizijatar requested this page from myAppointmets.xhtml then return him
	 * to myAppointments.xhtml
	 * 
	 * if fizijatar requested this page from pacijentProfile.xhtml then return
	 * him to pacijentProfile.xhtml
	 */
	public String back() {
		if (from.equals("fizijatar"))
			return "myAppointments.xhtml?faces-redirect=true";
		if (from.equals("pacijent"))
			return "pacijentProfile.xhtml?faces-redirect=true";
		return null;
	}

	public Obaveza getObaveza() {
		return obaveza;
	}

	public void setObaveza(Obaveza obaveza) {
		this.obaveza = obaveza;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public Pregled getPregled() {
		return pregled;
	}

	public void setPregled(Pregled pregled) {
		this.pregled = pregled;
	}

}
