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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import beans.fizijatar.ScheduleViewOnlyFizijatar;
import managers.FizioterapeutManager;
import managers.JPAUtil;
import managers.ObavezaManager;
import managers.PacijentManager;
import model.Fizioterapeut;
import model.Obaveza;

/**
 * 
 * @author MP101
 *
 */

@ManagedBean
@ViewScoped
public class ScheduleViewFizioterapeut implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Fizioterapeut fizioterapeut;
	private Obaveza izabranaObaveza;
	private Obaveza novaObaveza;
	
	private ScheduleEvent editEvent;
	
	private ScheduleModel eventModel;
	
	private boolean publicEvent;
	private String oldVremeOd;
	private String oldVremeDo;
	
	@ManagedProperty("#{welcomeFizioterapeutManagedBean}")
	private WelcomeFizioterapeutManagedBean welcomeFTManagedBean;
	
	private String message;
	private String colorEventPublic = "eventPublic";
	private String colorEventPrivate = "eventPrivate";
	private String messageNeispravnoVreme = "Neispravno vreme";
	private String messageTerminJeZauzet = "Termin je zauzet";
	
	
	@PostConstruct
	public void init()
	{
		this.message = "";
		this.fizioterapeut = welcomeFTManagedBean.getFizioterapeut().getRadnik().getFizioterapeut();
		this.fizioterapeut = JPAUtil.getEntityManager().merge(fizioterapeut); // update
		
		this.eventModel = new DefaultScheduleModel();
		
		this.editEvent = new DefaultScheduleEvent();
		
		DefaultScheduleEvent event;
		List<Obaveza> obaveze = fizioterapeut.getObavezas();
		for(Obaveza ob : obaveze)
		{
			event = new DefaultScheduleEvent(ob.getNaziv(), getBegin(ob), getEnd(ob), ob);
			if( ob.getVezbe() != null ) // if exists, than it is
				event.setStyleClass(colorEventPublic);
			else
				event.setStyleClass(colorEventPrivate);
			
			this.eventModel.addEvent(event);  // dodaj obaveze fizioterapeuta na kalendar
		}
	}

	
	/**
	 * If free date has selected
	 * @param selectEvent is selected date
	 */
	public void onDateSelect(SelectEvent selectEvent)
	{
		Date datum = (Date) selectEvent.getObject();
		this.novaObaveza = new Obaveza();
		this.novaObaveza.setDatum(datum);
		this.novaObaveza.setFizioterapeut(fizioterapeut);
		this.message = "";
	}	
	
	/**
	 * If obaveza is selected
	 * @param selectEvent is selected obaveza
	 */
	public void onEventSelect(SelectEvent selectEvent)
	{
		editEvent = (ScheduleEvent) selectEvent.getObject();
		Obaveza ob = (Obaveza) editEvent.getData(); // zasto sa getDate ?
		this.izabranaObaveza = ob;
		this.oldVremeOd = izabranaObaveza.getVremeOd();
		this.oldVremeDo = izabranaObaveza.getVremeDo();
		this.publicEvent = (izabranaObaveza.getVezbe() != null); // if not vezbe, its private
		this.message = "";
	}
	
	
	/**
	 * Delete obaveza, update pacijent if obaveza is vezbe
	 */
	public void deleteEvent()
	{
		ObavezaManager.deleteObaveza(izabranaObaveza);
		this.eventModel.deleteEvent(editEvent); // delete selected obaveza
		
		FizioterapeutManager.removeObaveza(fizioterapeut, izabranaObaveza);
		
		// remove vezbe from pacijent
		if (izabranaObaveza.getVezbe() != null)
		{
			PacijentManager.removeVezbe(
				izabranaObaveza.getVezbe().getKarton().getPacijents().get(0),
				izabranaObaveza.getVezbe());
		}
	}
	
	
	public void addEvent(ActionEvent actionEvent)
	{
		if( !ObavezaManager.checkInterval(novaObaveza.getVremeOd(), novaObaveza.getVremeDo())) {
			message = messageNeispravnoVreme;
			return;
		}
		message = "";
		
		if( FizioterapeutManager.isFizioterapeutOccupiedInTermin(null, fizioterapeut,
				novaObaveza.getDatum(), novaObaveza.getVremeOd(), novaObaveza.getVremeDo())) 
		{
			message = messageTerminJeZauzet;
			return;
		}
		message = "";

		ScheduleEvent newEvent = new DefaultScheduleEvent(
				novaObaveza.getNaziv(), getBegin(novaObaveza), getEnd(novaObaveza), novaObaveza);

		// this is private event and it have unique color
		((DefaultScheduleEvent) newEvent).setStyleClass(colorEventPrivate);
		eventModel.addEvent(newEvent);

		// add obaveza to database
		ObavezaManager.persistObavezaFT(fizioterapeut, novaObaveza);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('newEventDialog').hide();");
	}
	
	
	public void editEvent(ActionEvent aciotnEvent)
	{
		if (!ObavezaManager.checkInterval(izabranaObaveza.getVremeOd(), izabranaObaveza.getVremeDo())) 
		{
			message = messageNeispravnoVreme;
			izabranaObaveza.setVremeOd(oldVremeOd);
			izabranaObaveza.setVremeDo(oldVremeDo);
			return;
		}
		message = "";

		// is termin occupied
		if (FizioterapeutManager.isFizioterapeutOccupiedInTermin(
				izabranaObaveza.getId(), fizioterapeut, izabranaObaveza.getDatum(),
				izabranaObaveza.getVremeOd(), izabranaObaveza.getVremeDo())) {
			message = messageTerminJeZauzet;
			izabranaObaveza.setVremeOd(oldVremeOd);
			izabranaObaveza.setVremeDo(oldVremeDo);
			return;
		}
		message = "";

		eventModel.deleteEvent(editEvent);

		editEvent = new DefaultScheduleEvent(izabranaObaveza.getNaziv(),
				getBegin(izabranaObaveza), getEnd(izabranaObaveza),
				izabranaObaveza);

		// fizijatar can only have pregled or private obaveza
		((DefaultScheduleEvent) editEvent).setStyleClass(izabranaObaveza
				.getPregled() == null ? colorEventPrivate : colorEventPublic);
		eventModel.addEvent(editEvent);

		// update obaveza in database
		ObavezaManager.updateObaveza(izabranaObaveza);

		// update and hide dialog if everything is ok
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("PF('myschedule').update();PF('editEventDialog').hide();");
	}
	
	
	private Date getBegin(Obaveza ob)
	{
		return ScheduleViewOnlyFizijatar.getStartDate(ob);
	}
	
	private Date getEnd(Obaveza ob)
	{
		return ScheduleViewOnlyFizijatar.getEndDate(ob);
	}


	
	public Fizioterapeut getFizioterapeut() {
		return fizioterapeut;
	}

	public void setFizioterapeut(Fizioterapeut fizioterapeut) {
		this.fizioterapeut = fizioterapeut;
	}

	public Obaveza getIzabranaObaveza() {
		return izabranaObaveza;
	}

	public void setIzabranaObaveza(Obaveza izabranaObaveza) {
		this.izabranaObaveza = izabranaObaveza;
	}

	public Obaveza getNovaObaveza() {
		return novaObaveza;
	}

	public void setNovaObaveza(Obaveza novaObaveza) {
		this.novaObaveza = novaObaveza;
	}

	public ScheduleEvent getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(ScheduleEvent editEvent) {
		this.editEvent = editEvent;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public WelcomeFizioterapeutManagedBean getWelcomeFTManagedBean() {
		return welcomeFTManagedBean;
	}

	public void setWelcomeFTManagedBean(
			WelcomeFizioterapeutManagedBean welcomeFTManagedBean) {
		this.welcomeFTManagedBean = welcomeFTManagedBean;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getMessageNeispravnoVreme() {
		return messageNeispravnoVreme;
	}

	public void setMessageNeispravnoVreme(String messageNeispravnoVreme) {
		this.messageNeispravnoVreme = messageNeispravnoVreme;
	}

	public String getMessageTerminJeZauzet() {
		return messageTerminJeZauzet;
	}

	public void setMessageTerminJeZauzet(String messageTerminJeZauzet) {
		this.messageTerminJeZauzet = messageTerminJeZauzet;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isPublicEvent() {
		return publicEvent;
	}

	public void setPublicEvent(boolean publicEvent) {
		this.publicEvent = publicEvent;
	}

	public String getOldVremeOd() {
		return oldVremeOd;
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
}
