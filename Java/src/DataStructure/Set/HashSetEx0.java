package DataStructure.Set;

import java.util.HashSet;
import java.util.Set;

public class HashSetEx0 {
	public static void main(String[] args) {
		Set<String> set = new HashSet<String>();
		set.add("java");
		set.add("javax");
		set.add("jdbc");
		set.add("mysql");
		set.add("java"); //중복 객체이므로 저장하지 않음 이건.
		set.add("spring");

		int size = set.size();
		System.out.println("총 객체의 수 : " + size);
	}
}
