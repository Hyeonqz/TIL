package chp3;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExpirationDateTest {
	
	@Test
	@DisplayName("월 납입료는 만원이고, 1달 연장 가능")
	void 만원납부하면_한달뒤가_만료일임() {
	    // given
		assertExpiryDate(LocalDate.of(2019,3,1),10_000, LocalDate.of(2019,4,1));
		assertExpiryDate(LocalDate.of(2019,7,1),10_000, LocalDate.of(2019,8,1));

	}

	private static void assertExpiryDate (PayData payData, LocalDate expectedDate) {
		ExpiryDateCalculator calculator = new ExpiryDateCalculator();
		LocalDate realExpireDate = calculator.calculateExpirationDate(payData);
		Assertions.assertEquals(expectedDate,realExpireDate);
	}

	@Test
	@DisplayName("")
	void 납부일과_한달뒤_날짜가_같지않음() {
	}

}
