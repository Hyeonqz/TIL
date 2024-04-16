package Java.Stream;

import java.util.ArrayList;
import java.util.List;

public class FilteringEx {
	public static void main(String[] args) {
		//List 컬렉션 생성
		List<String> list = new ArrayList<String>();
		list.add("진현규");
		list.add("진라면");
		list.add("이성신");
		list.add("최성현");
		list.add("최성현");
		list.add("임형준");
		list.add("임형준");
		list.add("장순영");

		//중복 요소 제거
		list.stream()
			.distinct()
			.forEach(a -> System.out.println(a));
		System.out.println();

		//'진' 으로 시작하는 요소만 필터링
		list.stream()
			.filter(n -> n.startsWith("진"))
			.forEach(a -> System.out.println(a));
		System.out.println();

		//중복 요소 제거하고, 신으로 시작하는 요소만 필터링
		list.stream()
			.distinct()
			.filter(n -> n.startsWith("진"))
			.forEach(a -> System.out.println(a));

	}
}
