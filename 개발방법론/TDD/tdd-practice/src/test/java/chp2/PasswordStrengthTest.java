package chp2;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PasswordStrengthTest {
	
	@Test
	@DisplayName("")
	void meetsAllCriteria_Then_Strong() {
	    // given
		PasswordStrengthMeter meter = new PasswordStrengthMeter();
		PasswordStrength result = meter.meter("ab12!@AB");
		assertEquals(PasswordStrength.STRONG, result);

		PasswordStrength result2 = meter.meter("abc1!Add");
		assertEquals(PasswordStrength.STRONG, result2);
		// when

		// then
	}


	@Test
	@DisplayName("길이가 8글자 미만이고 나머지 조건은 충족하는 경우")
	void meetsOtherCriteria_except_for_Length_Then_Normal() {
	    // given
		PasswordStrengthMeter meter = new PasswordStrengthMeter();
		PasswordStrength passwordStrength = meter.meter("ab12!@A");
		assertEquals(PasswordStrength.NORMAL, passwordStrength);

		PasswordStrength passwordStrength1 = meter.meter("abc1!Add");
		assertEquals(PasswordStrength.STRONG, passwordStrength1);

	    // when

	    // then
	}
}