package naon;

// 계산기 인터페이스
interface Calculator {
	long calc(long[] arr);
}

// 전략 클래스
class Strategy {
	static final int MEAN = 0;
	static final int DISTRIBUTION = 1;
}

// 평균 계산기 클래스
class MeanCalculator implements Calculator {
	@Override
	public long calc(long[] arr) {
		long sum = 0;
		for (long num : arr) {
			sum += num;
		}
		return sum / arr.length;
	}
}

// 분포 계산기 클래스
class DistributeCalculator implements Calculator {
	@Override
	public long calc(long[] arr) {
		Calculator meanCalculator = new MeanCalculator();
		long mean = meanCalculator.calc(arr);

		long sumOfSquaredDifferences = 0;
		for (long num : arr) {
			long difference = num - mean;
			sumOfSquaredDifferences += difference * difference;
		}
		return sumOfSquaredDifferences;
	}
}

// 팩토리 클래스
class CalcFactory {
	public static Calculator createCalculator(int strategy) {
		return switch (strategy) {
			case Strategy.MEAN -> new MeanCalculator();
			case Strategy.DISTRIBUTION -> new DistributeCalculator();
			default -> throw new IllegalArgumentException("Unknown strategy");
		};
	}
}

public class Main {
	public static void main(String[] args) {
		long[] arr = {10, 20, 30, 55, 60, 75, 80, 95, 83, 50};

		Calculator meanCalculator = CalcFactory.createCalculator(Strategy.MEAN);
		long meanResult = meanCalculator.calc(arr);
		System.out.println("Mean Result: " + meanResult);

		Calculator distributeCalculator = CalcFactory.createCalculator(Strategy.DISTRIBUTION);
		long distributeResult = distributeCalculator.calc(arr);
		System.out.println("Distribution Result: " + distributeResult);
	}
}
