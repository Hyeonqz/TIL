package EnhancedJava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Class1 {
	// https://medium.com/@chandantechie/10-common-java-programming-tricks-with-examples-to-enhance-your-code-cc61a04e4bfa 참고함

	// 1. Enhanced for loop
	public static void EnhancedForLoop () {
		int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};

		for (int number : numbers) {
			System.out.print(number + " ");
		}
		System.out.println();
	}

	// String manipulation with StringBuilder
	// 새로운 String 객체를 생성하지 않고, StringBuilder 를 이용해서 붙인다.
	public static void StringBuilderTest () {
		StringBuilder sb = new StringBuilder("Hello?");
		sb.append("my name ").append("is ").append("hyeonkyu");
		System.out.println(sb);
	}

	// Null checks -> Optional 사용
	public static void OptionalMethod () {
		String name = "HyeonKyu";
		Optional<String> maybeName = Optional.ofNullable(name);
		System.out.println(maybeName.orElse("Null"));
	}

	// UnderScores in numeric literals
	public static void UnderScores () {
		long largeNumber = 123_456_789_312_625L;
		int smallNumber = 123_456_789;
		System.out.println(largeNumber);
		System.out.println(smallNumber);
	}

	// Double brace initialization
	public static void initialization () {
		Map<String, Integer> fruits = Map.of("apple", 1, "orange", 2);
		List<String> students = Arrays.asList("123", "123", "3123");
		List<String> english = List.of("a", "b", "c", "c");
		System.out.println(fruits);
		System.out.println(students);
		System.out.println(english);
	}

	// Labeled statements (break & continue)
	public static void Labeled () {
		loop:
		for (int i = 0; i < 10; i++) {
			if (i == 5) {
				break loop;
			}
			System.out.println(i);
		}
	}

	// Annoymous inner classes
	public static void Annoymous () {
		Runnable runnable = new Runnable() {
			@Override
			public void run () {
				Thread thread = new Thread();
				thread.start();
				thread.notifyAll();
			}
		};
	}

	// Varargs -> variable arguments
	public static void Varargs (String... parameter) {
		for (String s : parameter) {
			System.out.println(s);
		}
	}

	// instanceof operator
	public static void instanceOf() {
		Object obj = "hi?";
		if(obj instanceof String) {
			String message = (String) obj;
			System.out.println(message.toUpperCase());
		}
	}

	// Try-with-resources -> 자동 리소스 종료를 보장하는 코드.
	public static void TryWithResources() {
		try(BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
			String line;
			while((line=reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// IO Handleing
		}
	}

}
