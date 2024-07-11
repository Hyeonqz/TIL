package Java.Lambda2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@FunctionalInterface
interface a {
	//public abstract int max(int a, int b);
	void myMethod();
}
//위 메서드는 1:1로 만 연결되어야함.
//함수형 인터페이스는 오직 하나의 추상 메서드만 정의되어 있어야 한다는 제약이 있음
//static이나 default 메서드의 개수는 제약이 없다.


public class Ex1 {
	public static void main(String[] args) {

		//함수형 프로그래밍 전
		List<String> list = Arrays.asList("abc","bcd","cde");
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});

		//함수형 프로그래밍 후
		List<String> list2 = Arrays.asList("abc","bcd","cde");
		Collections.sort(list, (s1,s2) -> s2.compareTo(s1));

	}
}
