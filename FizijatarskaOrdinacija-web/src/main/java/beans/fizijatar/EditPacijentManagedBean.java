package beans.fizijatar;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import managers.JPAUtil;
import managers.ObavezaManager;
import managers.PacijentManager;
import model.Dnevnostanje;
import model.Fizijatar;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import beans.login.LoginManagedBean;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@SessionScoped
public class EditPacijentManagedBean implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2345464234245323L;
	private Pacijent pacijent;
	private Fizijatar fizijatar;

	@ManagedProperty("#{welcomeFizijatarManagedBean}")
	private WelcomeFizijatarManagedBean welcomeFizijatarManagedBean;

	
	@PostConstruct
	private void init() {
		pacijent = welcomeFizijatarManagedBean.getPacijent();
		fizijatar = welcomeFizijatarManagedBean.getOsoba().getRadnik()
				.getFizijatar();
		pacijent = JPAUtil.getEntityManager().merge(pacijent); // update
	}

	public void updatePacijentKarton() 
	{
		PacijentManager.updateKarton(pacijent);
		LoginManagedBean.addMessage(FacesMessage.SEVERITY_INFO, "Info - Unos",
				"Uspesno ste izmenili karton");
	}

	public String makeAppointment() {
		return "makeAppointment.xhtml?faces-redirect=true";
	}

	public String showPregled(Pregled pregled) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		map.put("obaveza", pregled.getObaveza());
		map.put("from", "pacijent");

		return "showPregled.xhtml?faces-redirect=true";
	}

	public String scheduledPregled() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		Obaveza obaveza = ObavezaManager
				.getObavezaForToday(fizijatar, pacijent);
		if (obaveza != null) {
			map.put("obaveza", obaveza);
			map.put("from", "pacijent");
			return "scheduledPregled.xhtml?faces-redirect=true";
		} else {
			return "scheduledPregledDontExist.xhtml?faces-redirect=true";
		}
	}

	public List<Pregled> getAllPregledi() {
		List<Pregled> list = pacijent.getKarton().getPregleds();
		Collections.sort(list, cmpPregled);
		return list;
	}

	public List<Dnevnostanje> getAllDnevnaStanja() {
		List<Dnevnostanje> list = pacijent.getKarton().getDnevnostanjes();
		Collections.sort(list, cmpDnevnoStanje);
		return list;
	}

	Comparator<Pregled> cmpPregled = new Comparator<Pregled>() {

		@Override
		public int compare(Pregled obj1, Pregled obj2) {
			return -obj1.getObaveza().getDatum()
					.compareTo(obj2.getObaveza().getDatum());
		}
	};

	Comparator<Dnevnostanje> cmpDnevnoStanje = new Comparator<Dnevnostanje>() {

		@Override
		public int compare(Dnevnostanje obj1, Dnevnostanje obj2) {
			return -obj1.getDatum().compareTo(obj2.getDatum());
		}
	};

	public Pacijent getPacijent() {
		return pacijent;
	}

	public Fizijatar getFizijatar() {
		return fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public WelcomeFizijatarManagedBean getWelcomeFizijatarManagedBean() {
		return welcomeFizijatarManagedBean;
	}

	public void setWelcomeFizijatarManagedBean(
			WelcomeFizijatarManagedBean welcomeFizijatarManagedBean) {
		this.welcomeFizijatarManagedBean = welcomeFizijatarManagedBean;
	}

}
