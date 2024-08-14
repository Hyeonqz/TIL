package chp3;

import java.time.LocalDate;

public class PayData {
	private LocalDate billingDate;
	private int payAmount;

	public PayData () {
	}

	public PayData (LocalDate billingDate, int payAmount) {
		this.billingDate = billingDate;
		this.payAmount = payAmount;
	}

	public LocalDate getBillingDate () {
		return billingDate;
	}

	public void setBillingDate (LocalDate billingDate) {
		this.billingDate = billingDate;
	}

	public int getPayAmount () {
		return payAmount;
	}

	public void setPayAmount (int payAmount) {
		this.payAmount = payAmount;
	}

	public static class Builder {
		private PayData data = new PayData();

		public Builder billingDate (LocalDate billingDate) {
			data.billingDate = billingDate;
			return this;
		}

		public Builder payAmount (int payAmount) {
			data.payAmount = payAmount;
			return this;
		}

		public PayData build() {
			return data;
		}
	}

}
