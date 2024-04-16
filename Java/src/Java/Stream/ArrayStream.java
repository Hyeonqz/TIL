package Java.Stream;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArrayStream {
	public static void main(String[] args) {
		//문자열 배열 출력
		String[] strArray = {"진현규", "이성신", "최성현" };
		Stream<String> strStream = Arrays.stream(strArray);
		strStream.forEach(item -> System.out.println(item + ","));
		System.out.println();

		//숫자 배열 출력하기
		int[] intArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		IntStream intStream = Arrays.stream(intArray);
		intStream.forEach(item1 -> System.out.print(item1 + ","));
		System.out.println();
	}
}
