package chp2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

	@Test
	@DisplayName("더하기 테스트")
	void plus() {
	    // given
		int result = Calculator.plus(1,2);
		System.out.println("Result : " + result);

		assertEquals(5, Calculator.plus(4,1));
		assertEquals(3,result);
	}
}
