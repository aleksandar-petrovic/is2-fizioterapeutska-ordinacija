package beans.fizijatar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import managers.FizijatarManager;
import managers.JPAUtil;
import managers.ObavezaManager;
import managers.PacijentManager;
import model.Fizijatar;
import model.Obaveza;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@ViewScoped
public class ScheduleViewOnlyFizijatar implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 6830353326980602060L;
	private ScheduleModel eventModel;
	private ScheduleEvent editEvent = new DefaultScheduleEvent();

	private Fizijatar fizijatar;

	private Obaveza choosedObaveza;
	private Obaveza newObaveza;

	private boolean publicEvent;

	private String message;
	private String oldVremeOd;
	private String oldVremeDo;

	private String colorEventPublic = "eventPublic";
	private String colorEventPrivate = "eventPrivate";
	private String messageNeispravnoVreme = "Neispravno vreme";
	private String messageTerminJeZauzet = "Termin je zauzet";

	@ManagedProperty("#{welcomeFizijatarManagedBean}")
	private WelcomeFizijatarManagedBean welcomeFizijatarManagedBean;

	@PostConstruct
	public void init() {
		eventModel = new DefaultScheduleModel();

		fizijatar = welcomeFizijatarManagedBean.getOsoba().getRadnik()
				.getFizijatar();

		fizijatar = JPAUtil.getEntityManager().merge(fizijatar);

		List<Obaveza> obaveze = fizijatar.getObavezas();

		for (Obaveza obaveza : obaveze) {
			DefaultScheduleEvent event = new DefaultScheduleEvent(
					obaveza.getNaziv(), getStartDate(obaveza),
					getEndDate(obaveza), obaveza);

			// set color to event
			if (obaveza.getPregled() != null)
				event.setStyleClass(colorEventPublic);
			else
				event.setStyleClass(colorEventPrivate);

			eventModel.addEvent(event);
		}

	}

	public void onEventSelect(SelectEvent selectEvent) {
		editEvent = (ScheduleEvent) selectEvent.getObject();
		Obaveza obaveza = (Obaveza) editEvent.getData();
		choosedObaveza = obaveza;
		publicEvent = choosedObaveza.getPregled() != null;
		oldVremeOd = choosedObaveza.getVremeOd();
		oldVremeDo = choosedObaveza.getVremeDo();
		message = "";
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
			message = messageNeispravnoVreme;
			choosedObaveza.setVremeOd(oldVremeOd);
			choosedObaveza.setVremeDo(oldVremeDo);
			return;
		}
		message = "";

		// is termin occupied
		if (FizijatarManager.isFizijatarOccupiedInTermin(
				choosedObaveza.getId(), fizijatar, choosedObaveza.getDatum(),
				choosedObaveza.getVremeOd(), choosedObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			choosedObaveza.setVremeOd(oldVremeOd);
			choosedObaveza.setVremeDo(oldVremeDo);
			return;
		}
		message = "";

		eventModel.deleteEvent(editEvent);

		editEvent = new DefaultScheduleEvent(choosedObaveza.getNaziv(),
				getStartDate(choosedObaveza), getEndDate(choosedObaveza),
				choosedObaveza);

		// fizijatar can only have pregled or private obaveza
		((DefaultScheduleEvent) editEvent).setStyleClass(choosedObaveza
				.getPregled() == null ? colorEventPrivate : colorEventPublic);
		eventModel.addEvent(editEvent);

		// update obaveza in database
		ObavezaManager.updateObaveza(choosedObaveza);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('editEventDialog').hide();");
	}

	public void addEvent(ActionEvent actionEvent) {
		if (!ObavezaManager.checkInterval(newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageNeispravnoVreme;
			return;
		}
		message = "";

		if (FizijatarManager.isFizijatarOccupiedInTermin(null, fizijatar,
				newObaveza.getDatum(), newObaveza.getVremeOd(),
				newObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			return;
		}
		message = "";

		ScheduleEvent newEvent = new DefaultScheduleEvent(
				newObaveza.getNaziv(), getStartDate(newObaveza),
				getEndDate(newObaveza), newObaveza);

		// this is private event and it have unique color
		((DefaultScheduleEvent) newEvent).setStyleClass(colorEventPrivate);
		eventModel.addEvent(newEvent);

		// add obaveza to database
		ObavezaManager.persistObaveza(fizijatar, newObaveza);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('newEventDialog').hide();");
	}

	public void deleteEvent() {
		ObavezaManager.deleteObaveza(choosedObaveza);
		eventModel.deleteEvent(editEvent);
		FizijatarManager.removeObaveza(fizijatar, choosedObaveza);
		// remove pregled from pacijent
		if (choosedObaveza.getPregled() != null)
			PacijentManager.removePregled(choosedObaveza.getPregled()
					.getKarton().getPacijents().get(0),
					choosedObaveza.getPregled());
	}

	public String showPregled() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> map = context.getExternalContext().getSessionMap();
		map.put("obaveza", choosedObaveza);
		map.put("from", "fizijatar");

		return "showPregled.xhtml";
	}

	public static Date getEndDate(Obaveza obaveza) {
		Date date = obaveza.getDatum();
		int hours = Integer.parseInt(obaveza.getVremeDo().split(":")[0]);
		int minute = Integer.parseInt(obaveza.getVremeDo().split(":")[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public static Date getStartDate(Obaveza obaveza) {
		Date date = obaveza.getDatum();
		int hours = Integer.parseInt(obaveza.getVremeOd().split(":")[0]);
		int minute = Integer.parseInt(obaveza.getVremeOd().split(":")[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setFizijatar(Fizijatar fizijatar) {
		this.fizijatar = fizijatar;
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

	public WelcomeFizijatarManagedBean getWelcomeFizijatarManagedBean() {
		return welcomeFizijatarManagedBean;
	}

	public void setWelcomeFizijatarManagedBean(
			WelcomeFizijatarManagedBean welcomeFizijatarManagedBean) {
		this.welcomeFizijatarManagedBean = welcomeFizijatarManagedBean;
	}

	public boolean isPublicEvent() {
		return publicEvent;
	}

	public void setPublicEvent(boolean publicEvent) {
		this.publicEvent = publicEvent;
	}

}
