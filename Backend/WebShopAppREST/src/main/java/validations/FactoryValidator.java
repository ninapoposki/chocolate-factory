package validations;

import java.net.MalformedURLException;
import java.net.URL;

import beans.Chocolate;
import beans.Factory;
import beans.Location;

//informacije o nazivu, lokaciji, radnom vremenu i da izabere logo
public class FactoryValidator{
	//lokaciju ce birati iz padajuceg menija-isto kao sto smo uradili 
	//za fabrike i cokolade
	 public static boolean isValidFactory(Factory factory) {
	        return isValidName(factory.getFactoryName()) &&
	               isValidWorkingTime(factory.getWorkingTime()) &&
	               isValidLogo(factory.getLogoUri()) &&
	               isValidLocation(factory.getLocation());
	               
	    }

	    private static boolean isValidName(String name) {
	        return name != null && !name.trim().isEmpty();
	    }

	    private static boolean isValidWorkingTime(int time) {
	        return time > 0;
	    }

	    private static boolean isValidLogo(String logoUri) {
	        return logoUri != null && !logoUri.trim().isEmpty() && isValidURL(logoUri);
	    }

	    //ne znam hoce li moci ovo
	    private static boolean isValidURL(String urlString) {
	        try {
	            URL url = new URL(urlString);
	            return url.getProtocol().equals("http") || url.getProtocol().equals("https");
	        } catch (MalformedURLException e) {
	            return false;
	        }
	    }

	    private static boolean isValidLocation(Location location) {
	        return location !=null;
	    }

	   
	
	
}