package Java.Stream;

import java.util.Arrays;
import java.util.stream.IntStream;

public class TypeChnageStream {
	public static void main(String[] args) {
		int[] intArryay = {1,2,3,4,5,6,7,8,9,10,11};
		IntStream intStream = Arrays.stream(intArryay);
		intStream
			.asDoubleStream()
			.forEach(d -> System.out.print(d + " - "));

		System.out.println();

			intStream = Arrays.stream(intArryay);
			intStream
				.boxed()
				.forEach(obj -> System.out.print(obj.intValue() + " "));
	}
}
