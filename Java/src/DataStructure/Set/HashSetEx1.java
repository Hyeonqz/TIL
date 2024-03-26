package DataStructure.Set;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HashSetEx1 {
	public static void main(String[] args) {
		Set<String> set = new HashSet<>();
		//객체 추가
		set.add("java");
		set.add("mysql");
		set.add("oracle");
		set.add("spring");
		set.add("jsp");

		//객체를 하나씩 가져와서 처리
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String element =iterator.next();
			System.out.println(element);
			if (element.equals("jsp")) {
				iterator.remove();
			}
		}
		System.out.println();
		set.remove("oracle");

		for(String element : set) {
			System.out.println(element);
		}

	}
}
