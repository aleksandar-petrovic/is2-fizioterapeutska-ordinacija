package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author <a href="petrovic.aleks@outlook.com">Aleksandar Petrovic</a>
 */
@FacesValidator("timeValidator")
public class TimeValidator implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object obj)
			throws ValidatorException {

		String str = (String) obj;

		int k = 0;
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) == ':')
				k++;

		if (k == 1) {
			String[] array = str.split(":");
			if (array.length != 2)
				throwException();

			String str1 = array[0];
			String str2 = array[1];

			try {
				int hours = Integer.parseInt(str1);
				int minutes = Integer.parseInt(str2);

				if (hours < 0 || hours > 23)
					throwException();
				if (minutes < 0 || minutes > 59)
					throwException();

			} catch (NumberFormatException e) {
				throwException();
			}

			return;

		}

		throwException();
	}

	private void throwException() {
		FacesMessage msg = new FacesMessage("Time validation failed",
				"Pogresan format vremena");
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);
	}

	public static String getTimeAddDuration(String time, int duration) {
		String[] strA = time.split(":");
		int hours = Integer.parseInt(strA[0]);
		int minutes = Integer.parseInt(strA[1]);

		if (duration > 60) {
			hours += duration / 60;
			duration = duration % 60;
		}

		if (minutes + duration >= 60)
			hours++;
		minutes = (minutes + duration) % 60;

		return hours + ":" + minutes;
	}

	public static int getDurationOfInterval(String vremeOd, String vremeDo) {
		String[] strA = vremeOd.split(":");
		int hoursOd = Integer.parseInt(strA[0]);
		int minutesOd = Integer.parseInt(strA[1]);

		String[] strB = vremeDo.split(":");
		int hoursDo = Integer.parseInt(strB[0]);
		int minutesDo = Integer.parseInt(strB[1]);

		return (hoursDo - hoursOd) * 60 + minutesDo - minutesOd;
	}
}
