package chp3;

import java.time.LocalDate;

public class ExpiryDateCalculator {

	public LocalDate calculateExpirationDate (PayData payData) {
		return payData.getBillingDate().plusMonths(1);
	}

}
