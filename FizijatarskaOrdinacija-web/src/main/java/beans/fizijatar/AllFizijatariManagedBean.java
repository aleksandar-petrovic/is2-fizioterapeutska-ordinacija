package beans.fizijatar;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import managers.FizijatarManager;
import model.Fizijatar;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@ManagedBean
@SessionScoped
public class AllFizijatariManagedBean implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	List<Fizijatar> fizijatri;

	@PostConstruct
	private void init() {
		fizijatri = FizijatarManager.getAllFizijatar();
	}

	public List<Fizijatar> getFizijatri() {
		return fizijatri;
	}

	public void setFizijatri(List<Fizijatar> fizijatri) {
		this.fizijatri = fizijatri;
	}

}
