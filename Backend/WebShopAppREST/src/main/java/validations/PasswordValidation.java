package validations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PasswordValidation {
	
	/*Minimum eight characters, at least one letter and one number*/
	private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
	private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
	
	public static boolean isPasswordRegistrationValid(final String password, final String passwordConfirmation, final String useraname) 
	{
		if(isPasswordNull(password) || isPasswordNull(passwordConfirmation)) {
			return false;
		}
		
		if(!isPasswordsRegexMatches(password, passwordConfirmation)) {
			return false;
		}
		
		if(!isPasswordsMatches(password, passwordConfirmation)) {
			return false;
		}
		
		if(isPasswordContainsUsername(useraname, password)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isPasswordChangeValid(final String oldPassword, final String newPassword, final String newPasswordConfirmation, final String username) 
	{
		if(isPasswordNull(oldPassword) || isPasswordNull(newPassword) || isPasswordNull(newPasswordConfirmation)) 
		{
			return false;
		}
		if(!isPasswordsRegexMatches(newPassword, newPasswordConfirmation)) {
			return false;
		}
		if(!isPasswordsRegexMatches(newPassword, newPasswordConfirmation)) {
			return false;
		}
		if(isPasswordContainsUsername(username, newPassword)) {
			return false;
		}
		
		return true;
	}
	
	private static boolean isPasswordNull(final String password) {
		boolean result = password == null;
		if(result) {
			System.out.println("Password is null.");
		}
		
		return result;
	}
	
	private static boolean isPasswordsRegexMatches(final String password, final String passwordConfirmation) {
		return isRegexMatching(password) && isRegexMatching(passwordConfirmation);
	}
	
	private static boolean isRegexMatching(final String password) {
        Matcher matcher = passwordPattern.matcher(password);
        boolean result = matcher.matches();
        
        if(!result) {
        	System.out.println("Password is not valid.");
        }
        
        return result;
    }
	
	//other validations
	private static boolean isPasswordsMatches(final String passowrd, final String passwordConfirmation) {
		boolean result = passowrd.equals(passwordConfirmation);
		
		if(!result) {
			System.out.println("Passwords don't match.");
		}
		
		return result;
	}
		
	private static boolean isPasswordContainsUsername(final String username, final String password) {
		boolean result = password.contains(username);
		
		if(result) {
			System.out.println("Password contains usrename.");
		}
		
		return result;
	}
}
