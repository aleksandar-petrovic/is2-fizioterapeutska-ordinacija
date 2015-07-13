package beans.fizioterapeut;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import beans.login.LoginManagedBean;
import managers.FizijatarManager;
import managers.FizioterapeutManager;
import managers.JPAUtil;
import managers.ObavezaManager;
import model.Dnevnostanje;
import model.Fizioterapeut;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import model.Vezbe;

/**
 * 
 * @author MP101
 *
 */

@ManagedBean
@SessionScoped
public class PacijentProfileManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private Fizioterapeut fizioterapeut;
	private Pacijent pacijent;
	private Pregled pregled;
	private Vezbe todayVezba;

	@ManagedProperty("#{welcomeFizioterapeutManagedBean}")
	private WelcomeFizioterapeutManagedBean welcomeFTManagedBean;

	
	@PostConstruct
	public void init() 
	{
		this.fizioterapeut = welcomeFTManagedBean.getFizioterapeut()
				.getRadnik().getFizioterapeut();
		this.pacijent = welcomeFTManagedBean.getPacijent();
		pacijent = JPAUtil.getEntityManager().merge(pacijent); // update

		// max pregled from today
		Pregled maxPregled = null;
		Date datum = FizijatarManager.createDate(new Date());
		for (Pregled tekPregled : pacijent.getKarton().getPregleds()) {
			if (maxPregled == null)
				maxPregled = tekPregled;
			else {
				if (tekPregled.getObaveza().getDatum().compareTo(datum) <= 0
						&& tekPregled.getObaveza().getDatum()
								.compareTo(maxPregled.getObaveza().getDatum()) >= 0)
					maxPregled = tekPregled;
			}
		}
		pregled = maxPregled;
	}

	
	public void updateNapomena() 
	{
		FizioterapeutManager.updateNapomena(pacijent);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, 
				"Info - Unos", "Napomena je sacuvana");
	}

	public String makeAppointmentVezbe() 
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();

		if (pregled != null) {
			map.put("pregled", pregled);
			return "makeAppointmentVezbe.xhtml?faces-redirect=true";
		} else
			return "pregledDontExist.xhtml?faces-redirect=true";
	}

	public String showCurrentVezbe(Vezbe vezbe) 
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		map.put("vezbe", vezbe);

		return "showCurrentVezbe.xhtml?faces-redirect=true";
	}

	public String showTodaysVezbe() {
		Obaveza obaveza = ObavezaManager.getObavezaForTodayFT(fizioterapeut,
				pacijent);
		if (obaveza != null) {
			this.todayVezba = obaveza.getVezbe();
			return "showTodaysVezbe.xhtml";
		} else
			return "todaysVezbeDontExist.xhtml";
	}
	

	public List<Vezbe> getAllVezbe() {
		List<Vezbe> vezbe = pacijent.getKarton().getVezbes();
		Collections.sort(vezbe, uporediV); // opadajuce odradjene vezbe
		return vezbe;
	}

	Comparator<Vezbe> uporediV = new Comparator<Vezbe>() {
		@Override
		public int compare(Vezbe A, Vezbe B) {
			return B.getObaveza().getDatum()
					.compareTo(A.getObaveza().getDatum());
		}
	};

	public List<Dnevnostanje> getAllDnevnaStanja() {
		List<Dnevnostanje> dnvStanje = pacijent.getKarton().getDnevnostanjes();
		Collections.sort(dnvStanje, uporediDS);
		return dnvStanje;
	}

	Comparator<Dnevnostanje> uporediDS = new Comparator<Dnevnostanje>() {
		@Override
		public int compare(Dnevnostanje A, Dnevnostanje B) {
			return B.getDatum().compareTo(A.getDatum());
		}
	};

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

	public Vezbe getTodayVezba() {
		return todayVezba;
	}

	public void setTodayVezba(Vezbe todayVezba) {
		this.todayVezba = todayVezba;
	}

	public WelcomeFizioterapeutManagedBean getWelcomeFTManagedBean() {
		return welcomeFTManagedBean;
	}

	public void setWelcomeFTManagedBean(
			WelcomeFizioterapeutManagedBean welcomeFTManagedBean) {
		this.welcomeFTManagedBean = welcomeFTManagedBean;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
