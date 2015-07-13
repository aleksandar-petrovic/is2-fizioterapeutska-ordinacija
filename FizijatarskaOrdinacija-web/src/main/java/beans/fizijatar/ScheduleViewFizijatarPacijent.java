package beans.fizijatar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import managers.FizijatarManager;
import managers.JPAUtil;
import managers.KartonManager;
import managers.ObavezaManager;
import managers.PacijentManager;
import managers.PregledManager;
import model.Fizijatar;
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


/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

/**
 * I use this class when fizijatar want to create appointment for pacijent
 * Fizijatar can't see details of events, he is here only to create new pregled
 * **/
@ManagedBean
@ViewScoped
public class ScheduleViewFizijatarPacijent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6864134991111686823L;
	private ScheduleModel eventModel;
	private ScheduleEvent editEvent = new DefaultScheduleEvent();

	private Fizijatar fizijatar;
	private Pacijent pacijent;

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

	@ManagedProperty("#{editPacijentManagedBean}")
	private EditPacijentManagedBean editPacijentManagedBean;

	@PostConstruct
	public void init() {
		eventModel = new DefaultScheduleModel();

		fizijatar = editPacijentManagedBean.getFizijatar();
		pacijent = editPacijentManagedBean.getPacijent();

		// merge with existing instance of this object in memory
		pacijent = JPAUtil.getEntityManager().merge(pacijent);
		fizijatar = JPAUtil.getEntityManager().merge(fizijatar);

		List<Obaveza> obavezeFizijatra = fizijatar.getObavezas();
		// Obaveze of pacijent
		List<Pregled> preglediPacijenta = pacijent.getKarton().getPregleds();
		List<Vezbe> vezbePacijent = pacijent.getKarton().getVezbes();

		for (Obaveza obaveza : obavezeFizijatra) {
			DefaultScheduleEvent event = new DefaultScheduleEvent(
					obaveza.getNaziv(), getStartDate(obaveza),
					getEndDate(obaveza), obaveza);

			// set color to event
			String color = getEventColor(obaveza);
			event.setStyleClass(color);

			eventModel.addEvent(event);
		}

		for (Pregled pregled : preglediPacijenta) {
			if (!isPregledOfThisFizijatar(pregled)) {
				DefaultScheduleEvent event = new DefaultScheduleEvent(pregled
						.getObaveza().getNaziv(),
						getStartDate(pregled.getObaveza()),
						getEndDate(pregled.getObaveza()), pregled.getObaveza());

				event.setStyleClass(colorEventPublicPacijent);
				eventModel.addEvent(event);
			}
		}

		for (Vezbe vezbe : vezbePacijent) {
			DefaultScheduleEvent event = new DefaultScheduleEvent(vezbe
					.getObaveza().getNaziv(), getStartDate(vezbe.getObaveza()),
					getEndDate(vezbe.getObaveza()), vezbe.getObaveza());

			event.setStyleClass(colorEventPublicPacijent);
			eventModel.addEvent(event);
		}

	}

	public void onEventSelect(SelectEvent selectEvent) {
		editEvent = (ScheduleEvent) selectEvent.getObject();
		Obaveza obaveza = (Obaveza) editEvent.getData();
		choosedObaveza = obaveza;
		message = "";
		oldVremeOd = choosedObaveza.getVremeOd();
		oldVremeDo = choosedObaveza.getVremeDo();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		Date date = (Date) selectEvent.getObject();
		newObaveza = new Obaveza();
		newObaveza.setDatum(date);
		newObaveza.setFizijatar(fizijatar);
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

		// is termin occupied
		// fizijatar private
		if (choosedObaveza.getPregled() == null
				&& choosedObaveza.getVezbe() == null) {
			if (FizijatarManager.isFizijatarOccupiedInTermin(
					choosedObaveza.getId(), fizijatar,
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
				if (FizijatarManager.isFizijatarOccupiedInTermin(
						choosedObaveza.getId(), fizijatar,
						choosedObaveza.getDatum(), choosedObaveza.getVremeOd(),
						choosedObaveza.getVremeDo())) {
					message = messageZauzetiSteUTomTerminu;
					choosedObaveza.setVremeOd(oldVremeOd);
					choosedObaveza.setVremeDo(oldVremeDo);
					return;
				}
				// if event of old fizijatar set this new fizijatar
				choosedObaveza.setFizijatar(fizijatar);
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
			}
			// fizijatar pregled event
			else {
				if (FizijatarManager.isFizijatarOccupiedInTermin(
						choosedObaveza.getId(), fizijatar,
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
				getStartDate(choosedObaveza), getEndDate(choosedObaveza),
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
		if (FizijatarManager.isFizijatarOccupiedInTermin(null, fizijatar,
				newObaveza.getDatum(), newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageZauzetiSteUTomTerminu;
			return;
		}
		message = "";

		ScheduleEvent newEvent = new DefaultScheduleEvent(
				newObaveza.getNaziv(), getStartDate(newObaveza),
				getEndDate(newObaveza), newObaveza);

		// this is pacijent event and it has unique color
		((DefaultScheduleEvent) newEvent)
				.setStyleClass(colorEventPublicPacijent);
		eventModel.addEvent(newEvent);

		// add obaveza to database, pacijent, fizijatar
		ObavezaManager.persistObaveza(fizijatar, newObaveza);
		PregledManager.createPregled(newObaveza, pacijent);

		// update and hide dialog
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('newEventDialog').hide();");
	}

	public void deleteEvent() {
		eventModel.deleteEvent(editEvent);
		FizijatarManager.removeObaveza(fizijatar, choosedObaveza);

		// remove pregled for this pacijent
		if (choosedObaveza.getPregled() != null
				&& choosedObaveza.getPregled().getKarton().getId() == pacijent
						.getKarton().getId())
			PacijentManager
					.removePregled(pacijent, choosedObaveza.getPregled());

		// remove pregled from other pacijent
		else if (choosedObaveza.getPregled() != null) {
			PacijentManager.removePregled(KartonManager
					.getPacijentForKatron(choosedObaveza.getPregled()
							.getKarton()), choosedObaveza.getPregled());
		}
		ObavezaManager.deleteObaveza(choosedObaveza);
	}

	private boolean isPregledOfThisFizijatar(Pregled pregled) {
		List<Obaveza> obavezeFizijatra = fizijatar.getObavezas();
		for (Obaveza obaveza : obavezeFizijatra) {
			if (pregled.getId() == obaveza.getId())
				return true;
		}
		return false;
	}

	public Date getEndDate(Obaveza obaveza) {
		Date date = obaveza.getDatum();
		int hours = Integer.parseInt(obaveza.getVremeDo().split(":")[0]);
		int minute = Integer.parseInt(obaveza.getVremeDo().split(":")[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public Date getStartDate(Obaveza obaveza) {
		Date date = obaveza.getDatum();
		int hours = Integer.parseInt(obaveza.getVremeOd().split(":")[0]);
		int minute = Integer.parseInt(obaveza.getVremeOd().split(":")[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	private String getEventColor(Obaveza obaveza) {
		String color = "";
		// fizijatar private event
		if (obaveza.getPregled() == null && obaveza.getVezbe() == null) {
			color = colorEventPrivate;
		} else {
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
			else {
				color = colorEventPublic;
			}
		}
		return color;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleEvent getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(ScheduleEvent editEvent) {
		this.editEvent = editEvent;
	}

	public Fizijatar getFizijatar() {
		return fizijatar;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public EditPacijentManagedBean getEditPacijentManagedBean() {
		return editPacijentManagedBean;
	}

	public void setEditPacijentManagedBean(
			EditPacijentManagedBean editPacijentManagedBean) {
		this.editPacijentManagedBean = editPacijentManagedBean;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNewObaveza(Obaveza newObaveza) {
		this.newObaveza = newObaveza;
	}

}