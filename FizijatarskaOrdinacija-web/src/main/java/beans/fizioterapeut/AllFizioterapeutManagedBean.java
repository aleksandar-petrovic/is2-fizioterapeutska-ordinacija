package beans.fizioterapeut;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import managers.FizioterapeutManager;
import model.Fizioterapeut;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@SessionScoped
public class AllFizioterapeutManagedBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4407367563412871009L;
	List<Fizioterapeut> fizioterapeuti;

	@PostConstruct
	private void init() {
		fizioterapeuti = FizioterapeutManager.getAllFizioterapeut();
	}

	public List<Fizioterapeut> getFizioterapeuti() {
		return fizioterapeuti;
	}

	public void setFizioterapeuti(List<Fizioterapeut> fizioterapeuti) {
		this.fizioterapeuti = fizioterapeuti;
	}

}
