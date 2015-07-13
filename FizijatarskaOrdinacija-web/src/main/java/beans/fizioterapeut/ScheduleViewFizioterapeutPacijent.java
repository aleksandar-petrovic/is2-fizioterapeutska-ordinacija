package beans.fizioterapeut;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import managers.FizioterapeutManager;
import managers.JPAUtil;
import managers.KartonManager;
import managers.ObavezaManager;
import managers.PacijentManager;
import managers.VezbeManager;
import model.Fizioterapeut;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import model.Vezbe;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import beans.fizijatar.ScheduleViewOnlyFizijatar;

@ManagedBean
@ViewScoped
public class ScheduleViewFizioterapeutPacijent implements Serializable {
	private static final long serialVersionUID = 1L;

	private ScheduleModel eventModel;
	private ScheduleEvent editEvent;

	private Fizioterapeut fizioterapeut;
	private Pacijent pacijent;
	private Pregled pregled;

	private Obaveza choosedObaveza;
	private Obaveza newObaveza;

	private String message;
	private String oldVremeOd;
	private String oldVremeDo;

	private String colorEventPublicPacijent = "eventPublicPacijent";
	private String colorEventPublic = "eventPublic";
	private String colorEventPrivate = "eventPrivate";
	private String messageNeispavnoVreme = "Neispravno vreme";
	private String messageTerminJeZauzet = "Termin je zauzet";
	private String messagePacijentJeZauzetUTomTerminu = "Pacijent je zauzet u tom terminu";
	private String messageZauzetiSteUTomTerminu = "Zauzeti ste u tom terminu";

	@ManagedProperty("#{pacijentProfileManagedBean}")
	private PacijentProfileManagedBean pacijentProfileManagedBean;

	@PostConstruct
	public void init() {
		this.message = "";
		this.eventModel = new DefaultScheduleModel();
		this.editEvent = new DefaultScheduleEvent();

		this.fizioterapeut = pacijentProfileManagedBean.getFizioterapeut();
		this.pacijent = pacijentProfileManagedBean.getPacijent();

		this.pacijent = JPAUtil.getEntityManager().merge(pacijent); // update
		this.fizioterapeut = JPAUtil.getEntityManager().merge(fizioterapeut);

		// Obaveze of pacijent
		List<Pregled> preglediPacijenta = pacijent.getKarton().getPregleds();
		List<Vezbe> vezbePacijent = pacijent.getKarton().getVezbes();

		DefaultScheduleEvent event;
		List<Obaveza> obavezeFT = fizioterapeut.getObavezas();
		for (Obaveza ob : obavezeFT) {
			event = new DefaultScheduleEvent(ob.getNaziv(), getBegin(ob),
					getEnd(ob), ob);

			// set color to event
			String color = getEventColor(ob);
			event.setStyleClass(color);

			eventModel.addEvent(event);
		}

		for (Pregled pregled : preglediPacijenta) {
			event = new DefaultScheduleEvent(pregled.getObaveza().getNaziv(),
					getBegin(pregled.getObaveza()),
					getEnd(pregled.getObaveza()), pregled.getObaveza());

			event.setStyleClass(colorEventPublicPacijent);
			eventModel.addEvent(event);
		}

		for (Vezbe vezbe : vezbePacijent) {
			if (!this.isVezbeOfThisFizioterapeut(vezbe)) {
				event = new DefaultScheduleEvent(vezbe.getObaveza().getNaziv(),
						getBegin(vezbe.getObaveza()),
						getEnd(vezbe.getObaveza()), vezbe.getObaveza());

				event.setStyleClass(colorEventPublicPacijent);
				eventModel.addEvent(event);
			}
		}

		// for which pregled!!!
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		pregled = (Pregled) map.get("pregled");
	}

	public void onEventSelect(SelectEvent selectEvent) {
		editEvent = (ScheduleEvent) selectEvent.getObject();
		Obaveza obaveza = (Obaveza) editEvent.getData();
		choosedObaveza = obaveza;
		oldVremeOd = choosedObaveza.getVremeOd();
		oldVremeDo = choosedObaveza.getVremeDo();
		message = "";
	}

	public void onDateSelect(SelectEvent selectEvent) {
		Date date = (Date) selectEvent.getObject();
		newObaveza = new Obaveza();
		newObaveza.setDatum(date);
		newObaveza.setFizioterapeut(fizioterapeut);
		message = "";
	}

	public void editEvent(ActionEvent actionEvent) {
		if (!ObavezaManager.checkInterval(choosedObaveza.getVremeOd(),
				choosedObaveza.getVremeDo())) {
			message = messageNeispavnoVreme;
			choosedObaveza.setVremeOd(oldVremeOd);
			choosedObaveza.setVremeDo(oldVremeDo);
			return;
		}
		message = "";

		// is termin occupied, !!
		// FT private
		if (choosedObaveza.getPregled() == null
				&& choosedObaveza.getVezbe() == null) {
			if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(
					choosedObaveza.getId(), fizioterapeut,
					choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
					choosedObaveza.getVremeDo())) {
				message = messageTerminJeZauzet;
				choosedObaveza.setVremeOd(oldVremeOd);
				choosedObaveza.setVremeDo(oldVremeDo);
				return;
			}
		} else {
			// pacijent pregled
			if (choosedObaveza.getPregled() != null
					&& choosedObaveza.getPregled().getKarton().getId() == pacijent
							.getKarton().getId()) {
				if (PacijentManager.isPacijentOccupiedInTermin(
						choosedObaveza.getId(), pacijent,
						choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
						choosedObaveza.getVremeDo())) {
					message = messagePacijentJeZauzetUTomTerminu;
					choosedObaveza.setVremeOd(oldVremeOd);
					choosedObaveza.setVremeDo(oldVremeDo);
					return;
				}
			}
			// pacijent vezbe
			else if (choosedObaveza.getVezbe() != null
					&& choosedObaveza.getVezbe().getKarton().getId() == pacijent
							.getKarton().getId()) {
				if (PacijentManager.isPacijentOccupiedInTermin(
						choosedObaveza.getId(), pacijent,
						choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
						choosedObaveza.getVremeDo())) {
					message = messagePacijentJeZauzetUTomTerminu;
					choosedObaveza.setVremeOd(oldVremeOd);
					choosedObaveza.setVremeDo(oldVremeDo);
					return;
				}
				if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(
						choosedObaveza.getId(), fizioterapeut,
						choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
						choosedObaveza.getVremeDo())) {
					message = messageZauzetiSteUTomTerminu;
					choosedObaveza.setVremeOd(oldVremeOd);
					choosedObaveza.setVremeDo(oldVremeDo);
					return;
				}
				// if event of old fizijatar set this new fizijatar
				choosedObaveza.setFizioterapeut(fizioterapeut);
			}

			// FT vezbe event
			else {
				if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(
						choosedObaveza.getId(), fizioterapeut,
						choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
						choosedObaveza.getVremeDo())) {
					message = messageZauzetiSteUTomTerminu;
					choosedObaveza.setVremeOd(oldVremeOd);
					choosedObaveza.setVremeDo(oldVremeDo);
					return;
				}
			}
		}

		eventModel.deleteEvent(editEvent);

		editEvent = new DefaultScheduleEvent(choosedObaveza.getNaziv(),
				getBegin(choosedObaveza), getEnd(choosedObaveza),
				choosedObaveza);

		// more colors, private, public, pacijent
		String color = getEventColor(choosedObaveza);

		((DefaultScheduleEvent) editEvent).setStyleClass(color);
		eventModel.addEvent(editEvent);

		// update obaveza in database
		ObavezaManager.updateObaveza(choosedObaveza);

		// update and hide dialog
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('editEventDialog').hide();");
	}

	public void addEvent(ActionEvent actionEvent) {
		if (!ObavezaManager.checkInterval(newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageNeispavnoVreme;
			return;
		}
		message = "";

		if (PacijentManager.isPacijentOccupiedInTermin(null, pacijent,
				newObaveza.getDatum(), newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			return;
		}
		if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(null,
				fizioterapeut, newObaveza.getDatum(), newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageZauzetiSteUTomTerminu;
			return;
		}
		message = "";

		ScheduleEvent newEvent = new DefaultScheduleEvent(
				newObaveza.getNaziv(), getBegin(newObaveza),
				getEnd(newObaveza), newObaveza);

		// this is pacijent event and it has unique color
		((DefaultScheduleEvent) newEvent)
				.setStyleClass(colorEventPublicPacijent);
		eventModel.addEvent(newEvent);

		// add obaveza to database, pacijent, fizioterapeut
		ObavezaManager.persistObavezaFT(fizioterapeut, newObaveza);
		VezbeManager.createVezbe(newObaveza, pacijent, pregled);

		// update and hide dialog
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('newEventDialog').hide();");
	}

	public void deleteEvent() {
		eventModel.deleteEvent(editEvent);
		FizioterapeutManager.removeObaveza(fizioterapeut, choosedObaveza);

		// remove vezbe for this pacijent
		if (choosedObaveza.getVezbe() != null
				&& choosedObaveza.getVezbe().getKarton().getId() == pacijent
						.getKarton().getId())
			PacijentManager.removeVezbe(pacijent, choosedObaveza.getVezbe());

		// remove vezbe from other pacijent
		else if (choosedObaveza.getVezbe() != null) {
			PacijentManager
					.removeVezbe(KartonManager
							.getPacijentForKatron(choosedObaveza.getVezbe()
									.getKarton()), choosedObaveza.getVezbe());
		}

		ObavezaManager.deleteObaveza(choosedObaveza);
	}

	private boolean isVezbeOfThisFizioterapeut(Vezbe vezbe) {
		List<Obaveza> obavezeFT = fizioterapeut.getObavezas();
		for (Obaveza obaveza : obavezeFT) {
			if (vezbe.getId() == obaveza.getId())
				return true;
		}
		return false;
	}

	private String getEventColor(Obaveza obaveza) {
		String color = "";
		// FT private event
		if (obaveza.getPregled() == null && obaveza.getVezbe() == null)
			color = colorEventPrivate;
		else {
			// pregled
			if (obaveza.getPregled() != null
					&& obaveza.getPregled().getKarton().getId() == pacijent
							.getKarton().getId()) {
				color = colorEventPublicPacijent;
			}
			// vezbe
			else if (obaveza.getVezbe() != null
					&& obaveza.getVezbe().getKarton().getId() == pacijent
							.getKarton().getId()) {
				color = colorEventPublicPacijent;
			}
			// fizijatar pregled event
			else
				color = colorEventPublic;
		}
		return color;
	}

	private Date getBegin(Obaveza ob) {
		return ScheduleViewOnlyFizijatar.getStartDate(ob);
	}

	private Date getEnd(Obaveza ob) {
		return ScheduleViewOnlyFizijatar.getEndDate(ob);
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public ScheduleEvent getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(ScheduleEvent editEvent) {
		this.editEvent = editEvent;
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

	public Obaveza getChoosedObaveza() {
		return choosedObaveza;
	}

	public void setChoosedObaveza(Obaveza choosedObaveza) {
		this.choosedObaveza = choosedObaveza;
	}

	public Obaveza getNewObaveza() {
		return newObaveza;
	}

	public void setNewObaveza(Obaveza newObaveza) {
		this.newObaveza = newObaveza;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOldVremeOd() {
		return oldVremeOd;
	}

	public Pregled getPregled() {
		return pregled;
	}

	public void setPregled(Pregled pregled) {
		this.pregled = pregled;
	}

	public void setOldVremeOd(String oldVremeOd) {
		this.oldVremeOd = oldVremeOd;
	}

	public String getOldVremeDo() {
		return oldVremeDo;
	}

	public void setOldVremeDo(String oldVremeDo) {
		this.oldVremeDo = oldVremeDo;
	}

	public String getColorEventPublicPacijent() {
		return colorEventPublicPacijent;
	}

	public void setColorEventPublicPacijent(String colorEventPublicPacijent) {
		this.colorEventPublicPacijent = colorEventPublicPacijent;
	}

	public String getColorEventPublic() {
		return colorEventPublic;
	}

	public void setColorEventPublic(String colorEventPublic) {
		this.colorEventPublic = colorEventPublic;
	}

	public String getColorEventPrivate() {
		return colorEventPrivate;
	}

	public void setColorEventPrivate(String colorEventPrivate) {
		this.colorEventPrivate = colorEventPrivate;
	}

	public String getMessageNeispavnoVreme() {
		return messageNeispavnoVreme;
	}

	public void setMessageNeispavnoVreme(String messageNeispavnoVreme) {
		this.messageNeispavnoVreme = messageNeispavnoVreme;
	}

	public String getMessageTerminJeZauzet() {
		return messageTerminJeZauzet;
	}

	public void setMessageTerminJeZauzet(String messageTerminJeZauzet) {
		this.messageTerminJeZauzet = messageTerminJeZauzet;
	}

	public String getMessagePacijentJeZauzetUTomTerminu() {
		return messagePacijentJeZauzetUTomTerminu;
	}

	public void setMessagePacijentJeZauzetUTomTerminu(
			String messagePacijentJeZauzetUTomTerminu) {
		this.messagePacijentJeZauzetUTomTerminu = messagePacijentJeZauzetUTomTerminu;
	}

	public String getMessageZauzetiSteUTomTerminu() {
		return messageZauzetiSteUTomTerminu;
	}

	public void setMessageZauzetiSteUTomTerminu(
			String messageZauzetiSteUTomTerminu) {
		this.messageZauzetiSteUTomTerminu = messageZauzetiSteUTomTerminu;
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
