package Java.Stream;

import java.util.stream.IntStream;

public class IntegerStream {
	public static int sum;
	public static void main(String[] args) {

		IntStream intStream = IntStream.rangeClosed(1,100);
		intStream.forEach(a -> sum += a);
		System.out.println("총합 : " + sum);
	}
}
