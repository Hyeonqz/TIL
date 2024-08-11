package DataStructure.Set;

import java.util.HashSet;
import java.util.Set;

public class HashSetMemberService {
	public static void main(String[] args) {
		Set<HashSetMember> set = new HashSet<>();
		set.add(new HashSetMember("진현규",25));
		set.add(new HashSetMember("진현규",25));
		set.add(new HashSetMember("이성신",28));
		System.out.println("총 객체 수 " + set.size());


	}
}
