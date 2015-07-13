package beans.fizioterapeut;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import beans.login.LoginManagedBean;
import managers.FizioterapeutManager;
import model.Fizioterapeut;
import model.Pacijent;
import model.Pregled;
import model.Stanje;
import model.Vezbe;

@ManagedBean
@RequestScoped
public class ShowCurrentVezbeManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private Fizioterapeut fizioterapeut;
	private Pacijent pacijent;
	private Pregled pregled;

	private Vezbe vezbe; // current
	private Stanje stanje;


	@ManagedProperty("#{pacijentProfileManagedBean}")
	private PacijentProfileManagedBean pacijentProfileManagedBean;

	
	@PostConstruct
	public void init() {
		this.fizioterapeut = pacijentProfileManagedBean.getFizioterapeut();
		this.pacijent = pacijentProfileManagedBean.getPacijent();

		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();

		vezbe = (Vezbe) map.get("vezbe");
		stanje = vezbe.getStanje();

		pregled = vezbe.getTerapija().getPregleds().get(0);
	}

	
	public void updateVezbe() 
	{
		FizioterapeutManager.updateVezbe(vezbe, stanje);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO,
				"Info - Unos", "Vezbe su uspesno sacuvane");
	}

	public void updateNapomena() 
	{
		FizioterapeutManager.updateNapomenaPregled(pregled);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO,
				"Info - Unos", "Napomena je uspesno sacuvana");
	}

	
	
	public Fizioterapeut getFizioterapeut() {
		return fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
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

	public Vezbe getVezbe() {
		return vezbe;
	}

	public void setVezbe(Vezbe vezbe) {
		this.vezbe = vezbe;
	}

	public Stanje getStanje() {
		return stanje;
	}

	public void setStanje(Stanje stanje) {
		this.stanje = stanje;
	}

	public PacijentProfileManagedBean getPacijentProfileManagedBean() {
		return pacijentProfileManagedBean;
	}

	public void setPacijentProfileManagedBean(
			PacijentProfileManagedBean pacijentProfileManagedBean) {
		this.pacijentProfileManagedBean = pacijentProfileManagedBean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
