package converters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import managers.FizioterapeutManager;
import model.Fizioterapeut;


@FacesConverter("fizioterapeutConverter")
public class FizioterapeutConvertor implements Converter 
{
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String str) {
		try {
			int id = Integer.parseInt(str);

			for (Fizioterapeut fiz : FizioterapeutManager.getAllFizioterapeut()) {
				if (fiz.getId() == id)
					return fiz;
			}

		} catch (Exception e) {

		}
		throw new ConverterException(new FacesMessage(
				String.format("Nepostojeci fizioterapeut sa id-om " + str)));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) 
	{
		if (obj == null)
			return null;
		else {
			Fizioterapeut fizioterapeut = (Fizioterapeut) obj;
			return String.valueOf(fizioterapeut.getId());
		}
	}
}
