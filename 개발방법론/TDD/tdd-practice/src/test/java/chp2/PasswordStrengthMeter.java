package chp2;

public class PasswordStrengthMeter {

	public PasswordStrength meter(String s) {
		if(s.length() < 8) {
			return PasswordStrength.NORMAL;
		}
		return PasswordStrength.STRONG;
	}

}
