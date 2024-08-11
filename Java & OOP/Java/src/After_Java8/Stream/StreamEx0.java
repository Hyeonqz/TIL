package Java.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class StreamEx0 {
	public static void main(String[] args) {

		Set<String> set = new HashSet<String>();
		set.add("이성신");
		set.add("진현규");
		set.add("최성현");

		//스트림 얻기
		Stream<String> stream = set.stream();
		//람다식을 사용하여 스트림 처리
		stream.forEach(name -> System.out.println(name));

	}
}
