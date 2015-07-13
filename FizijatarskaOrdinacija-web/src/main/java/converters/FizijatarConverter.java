package converters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import managers.FizijatarManager;
import model.Fizijatar;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */

@FacesConverter("fizijatarConverter")
public class FizijatarConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String str) {
		try {
			int id = Integer.parseInt(str);

			for (Fizijatar fiz : FizijatarManager.getAllFizijatar()) {
				if (fiz.getId() == id)
					return fiz;
			}

		} catch (Exception e) {

		}
		throw new ConverterException(new FacesMessage(
				String.format("Nepostojeci fizijatar sa id-om " + str)));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) {
		if (obj == null)
			return null;
		else {
			Fizijatar fizijatar = (Fizijatar) obj;
			return String.valueOf(fizijatar.getId());
		}
	}

}
