package beans.pacijent;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import validator.TimeValidator;
import managers.FizijatarManager;
import managers.FizioterapeutManager;
import managers.JPAUtil;
import managers.ObavezaManager;
import managers.PacijentManager;
import managers.PregledManager;
import managers.VezbeManager;
import model.Obaveza;
import model.Pacijent;
import model.Pregled;
import model.Vezbe;
import beans.login.LoginManagedBean;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@ViewScoped
public class ScheduleViewPacijent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6810469032320451461L;

	private Pacijent pacijent;

	private ScheduleModel eventModel;
	private ScheduleEvent editEvent = new DefaultScheduleEvent();

	private Vezbe choosedVezbe;
	private Pregled choosedPregled;
	private Obaveza newObaveza; // this is new pregled
	private String oldVremeOd;

	// this is discussable
	private int eventDuration = 15; // min
	private int eventDurationOld;
	private String message;

	private String colorEventPacijentPregled = "eventPacijentPregled";
	private String colorEventPacijentVezbe = "eventPacijentVezbe";
	private String colorEventFizijatar = "eventFizijatar";
	private String colorEventFizioterapeut = "eventFizioterapeut";
	private String messageTerminJeZauzet = "Termin je zauzet";
	private String messageEventFizijatarZauzeto = "Fizijatar - zauzeto";
	private String messageEventFizioterapeutZauzeto = "Fizioterapeut - zauzeto";

	@ManagedProperty("#{loginManagedBean}")
	private LoginManagedBean loginManagedBean;

	@PostConstruct
	private void init() {
		eventModel = new DefaultScheduleModel();

		pacijent = loginManagedBean.getOsoba().getPacijent();

		// merge with existing instance of this object in memory
		pacijent = JPAUtil.getEntityManager().merge(pacijent);

		List<Obaveza> obavezeFizijatra = pacijent.getFizijatar().getObavezas();
		List<Obaveza> obavezeFizioterapeuta = pacijent.getFizioterapeut()
				.getObavezas();
		List<Pregled> preglediPacijenta = pacijent.getKarton().getPregleds();
		List<Vezbe> vezbePacijenta = pacijent.getKarton().getVezbes();

		fillScheduleModel(obavezeFizijatra, obavezeFizioterapeuta,
				preglediPacijenta, vezbePacijenta);
	}

	public void onEventSelect(SelectEvent selectEvent) {
		editEvent = (ScheduleEvent) selectEvent.getObject();

		// pacijent has no right to see or change fizijatar or fizioterapeut
		// pacijent only can change his own pregled or vezbe 
		// events
		if (editEvent.getData().getClass() == Obaveza.class)
			return;

		RequestContext rc = RequestContext.getCurrentInstance();
		if (editEvent.getData().getClass() == Pregled.class) {
			choosedPregled = (Pregled) editEvent.getData();
			eventDurationOld = TimeValidator.getDurationOfInterval(
					choosedPregled.getObaveza().getVremeOd(), choosedPregled
							.getObaveza().getVremeDo());
			oldVremeOd = choosedPregled.getObaveza().getVremeOd();
			rc.execute("PF('editEventPregledDialog').show();");
		}
		if (editEvent.getData().getClass() == Vezbe.class) {
			choosedVezbe = (Vezbe) editEvent.getData();

			System.out.println("uso u vezbe: "
					+ choosedVezbe.getObaveza().getNaziv());

			eventDurationOld = TimeValidator.getDurationOfInterval(choosedVezbe
					.getObaveza().getVremeOd(), choosedVezbe.getObaveza()
					.getVremeDo());
			oldVremeOd = choosedVezbe.getObaveza().getVremeOd();
			rc.execute("PF('editEventVezbeDialog').show();");
		}
		message = "";
	}

	public void onDateSelect(SelectEvent selectEvent) {
		Date date = (Date) selectEvent.getObject();
		newObaveza = new Obaveza();
		newObaveza.setDatum(date);
		newObaveza.setFizijatar(pacijent.getFizijatar());
		message = "";
	}

	public void editEventVezbe(ActionEvent actionEvent) {

		// is termin occupied
		if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(choosedVezbe
				.getId(), pacijent.getFizioterapeut(), choosedVezbe
				.getObaveza().getDatum(), choosedVezbe.getObaveza()
				.getVremeOd(), TimeValidator.getTimeAddDuration(choosedVezbe
				.getObaveza().getVremeOd(), eventDurationOld))) {
			message = messageTerminJeZauzet;
			choosedVezbe.getObaveza().setVremeOd(oldVremeOd);
			return;
		}
		if (PacijentManager.isPacijentOccupiedInTermin(choosedVezbe.getId(),
				pacijent, choosedVezbe.getObaveza().getDatum(), choosedVezbe
						.getObaveza().getVremeOd(), TimeValidator
						.getTimeAddDuration(choosedVezbe.getObaveza()
								.getVremeOd(), eventDurationOld))) {
			message = messageTerminJeZauzet;
			choosedVezbe.getObaveza().setVremeOd(oldVremeOd);
			return;
		}
		message = "";

		eventModel.deleteEvent(editEvent);

		// set new interval
		choosedVezbe.getObaveza().setVremeDo(
				TimeValidator.getTimeAddDuration(choosedVezbe.getObaveza()
						.getVremeOd(), eventDurationOld));

		/*
		 * when editing some event in future, but pacijent changed his
		 * fizioterapeut earlier?!
		 */
		choosedVezbe.getObaveza().setFizioterapeut(pacijent.getFizioterapeut());

		editEvent = new DefaultScheduleEvent(choosedVezbe.getObaveza()
				.getNaziv(), getStartDate(choosedVezbe.getObaveza()),
				getEndDate(choosedVezbe.getObaveza()), choosedVezbe);

		// color
		((DefaultScheduleEvent) editEvent)
				.setStyleClass(colorEventPacijentVezbe);
		eventModel.addEvent(editEvent);

		// update vezbe in database
		ObavezaManager.updateObaveza(choosedVezbe.getObaveza());
		VezbeManager.updateVezbe(choosedVezbe);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('editEventVezbeDialog').hide();");
	}

	public void editEventPregled(ActionEvent actionEvent) {

		// is termin occupied
		if (FizijatarManager.isFizijatarOccupiedInTermin(
				choosedPregled.getId(), pacijent.getFizijatar(), choosedPregled
						.getObaveza().getDatum(), choosedPregled.getObaveza()
						.getVremeOd(), TimeValidator.getTimeAddDuration(
						choosedPregled.getObaveza().getVremeOd(),
						eventDurationOld))) {
			message = messageTerminJeZauzet;
			choosedPregled.getObaveza().setVremeOd(oldVremeOd);
			return;
		}
		if (PacijentManager.isPacijentOccupiedInTermin(choosedPregled.getId(),
				pacijent, choosedPregled.getObaveza().getDatum(),
				choosedPregled.getObaveza().getVremeOd(), TimeValidator
						.getTimeAddDuration(choosedPregled.getObaveza()
								.getVremeOd(), eventDurationOld))) {
			message = messageTerminJeZauzet;
			choosedPregled.getObaveza().setVremeOd(oldVremeOd);
			return;
		}
		message = "";

		eventModel.deleteEvent(editEvent);

		// set new interval
		choosedPregled.getObaveza().setVremeDo(
				TimeValidator.getTimeAddDuration(choosedPregled.getObaveza()
						.getVremeOd(), eventDurationOld));

		/*
		 * when editing some event in future, but pacijent changed his fizijatar
		 * earlier. Set new fizijatar?!
		 */
		choosedPregled.getObaveza().setFizijatar(pacijent.getFizijatar());

		editEvent = new DefaultScheduleEvent(choosedPregled.getObaveza()
				.getNaziv(), getStartDate(choosedPregled.getObaveza()),
				getEndDate(choosedPregled.getObaveza()), choosedPregled);

		// color
		((DefaultScheduleEvent) editEvent)
				.setStyleClass(colorEventPacijentPregled);
		eventModel.addEvent(editEvent);

		// update pregled in database
		ObavezaManager.updateObaveza(choosedPregled.getObaveza());
		PregledManager.updatePregled(choosedPregled);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('editEventPregledDialog').hide();");
	}

	public void addEvent(ActionEvent actionEvent) {

		// add obaveza to database, pacijent, fizijatar
		// eventDuration is important
		newObaveza.setVremeDo(TimeValidator.getTimeAddDuration(
				newObaveza.getVremeOd(), eventDuration));

		// is termin occupied
		if (FizijatarManager.isFizijatarOccupiedInTermin(null,
				pacijent.getFizijatar(), newObaveza.getDatum(),
				newObaveza.getVremeOd(), newObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			return;
		}
		if (PacijentManager.isPacijentOccupiedInTermin(null, pacijent,
				newObaveza.getDatum(), newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			return;
		}
		message = "";

		// persist in database
		ObavezaManager.persistObaveza(pacijent.getFizijatar(), newObaveza);
		PregledManager.createPregled(newObaveza, pacijent);

		ScheduleEvent newEvent = new DefaultScheduleEvent(
				newObaveza.getNaziv(), getStartDate(newObaveza),
				getEndDate(newObaveza), newObaveza.getPregled());

		// this is pacijent event and it has unique color
		((DefaultScheduleEvent) newEvent).setStyleClass("eventPacijentPregled");
		eventModel.addEvent(newEvent);

		// update and hide dialog
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('newEventDialog').hide();");
	}

	// pacijent can't delete vezbe
	public void deleteEventPregled() {
		Obaveza obaveza = choosedPregled.getObaveza();
		ObavezaManager.deleteObaveza(obaveza);
		eventModel.deleteEvent(editEvent);
		FizijatarManager.removeObaveza(pacijent.getFizijatar(), obaveza);
		PacijentManager.removePregled(pacijent, choosedPregled);
	}

	private void fillScheduleModel(List<Obaveza> obavezeFizijatra,
			List<Obaveza> obavezeFizioterapeuta,
			List<Pregled> preglediPacijenta, List<Vezbe> vezbePacijenta) {

		for (Pregled pregled : preglediPacijenta) {
			Obaveza obaveza = pregled.getObaveza();
			DefaultScheduleEvent event = new DefaultScheduleEvent(
					obaveza.getNaziv(), getStartDate(obaveza),
					getEndDate(obaveza), pregled);

			event.setStyleClass(colorEventPacijentPregled);
			eventModel.addEvent(event);
		}

		for (Vezbe vezbe : vezbePacijenta) {
			Obaveza obaveza = vezbe.getObaveza();
			DefaultScheduleEvent event = new DefaultScheduleEvent(
					obaveza.getNaziv(), getStartDate(obaveza),
					getEndDate(obaveza), vezbe);

			event.setStyleClass(colorEventPacijentVezbe);
			eventModel.addEvent(event);
		}

		for (Obaveza obaveza : obavezeFizijatra) {
			if (!isPreglediContainsID(preglediPacijenta, obaveza.getId())) {
				DefaultScheduleEvent event = new DefaultScheduleEvent(
						messageEventFizijatarZauzeto, getStartDate(obaveza),
						getEndDate(obaveza), obaveza);

				event.setStyleClass(colorEventFizijatar);
				eventModel.addEvent(event);
			}
		}

		for (Obaveza obaveza : obavezeFizioterapeuta) {
			if (!isVezbeContainsID(vezbePacijenta, obaveza.getId())) {
				DefaultScheduleEvent event = new DefaultScheduleEvent(
						messageEventFizioterapeutZauzeto,
						getStartDate(obaveza), getEndDate(obaveza), obaveza);

				event.setStyleClass(colorEventFizioterapeut);
				eventModel.addEvent(event);
			}
		}

	}

	private boolean isPreglediContainsID(List<Pregled> list, int id) {
		for (Pregled pregled : list) {
			if (pregled.getId() == id)
				return true;
		}
		return false;
	}

	private boolean isVezbeContainsID(List<Vezbe> list, int id) {
		for (Vezbe vezbe : list) {
			if (vezbe.getId() == id)
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

	public LoginManagedBean getLoginManagedBean() {
		return loginManagedBean;
	}

	public void setLoginManagedBean(LoginManagedBean loginManagedBean) {
		this.loginManagedBean = loginManagedBean;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Vezbe getChoosedVezbe() {
		return choosedVezbe;
	}

	public void setChoosedVezbe(Vezbe choosedVezbe) {
		this.choosedVezbe = choosedVezbe;
	}

	public Pregled getChoosedPregled() {
		return choosedPregled;
	}

	public void setChoosedPregled(Pregled choosedPregled) {
		this.choosedPregled = choosedPregled;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public Obaveza getNewObaveza() {
		return newObaveza;
	}

	public void setNewObaveza(Obaveza newObaveza) {
		this.newObaveza = newObaveza;
	}

	public ScheduleEvent getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(ScheduleEvent editEvent) {
		this.editEvent = editEvent;
	}

}
