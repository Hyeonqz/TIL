package DataStructure.Tree;

import java.util.NavigableSet;
import java.util.TreeSet;

public class TreeSetEx {
	public static void main(String[] args) {

		TreeSet<Integer> scores = new TreeSet<Integer>();

		scores.add(100);
		scores.add(99);
		scores.add(98);
		scores.add(97);
		scores.add(643);
		scores.add(513);
		scores.add(52);

		for(Integer s : scores) {
			System.out.println(s + " ");
		}
		System.out.println("\n");

		System.out.println("가장 낮은 점수 : " + scores.first());
		System.out.println("가장 높은 점수 : " + scores.last());
		System.out.println("500점 아래 점수 : " + scores.lower(500));
		System.out.println("500점 이상 점수 : " + scores.higher(500));

		System.out.println("97점이거나 바로 아래 점수 : " + scores.floor(97));
		System.out.println("100점이거나 바로 위 점수 : " + scores.ceiling(100));

		//내림차순 정렬하기
		NavigableSet<Integer> descending = scores.descendingSet();
		for(Integer s : descending) {
			System.out.println(s + " ");
		}
		System.out.println("\n");

		//범위 검색 (80<=)
		NavigableSet<Integer> rangeSet = scores.tailSet(80,true);
		for(Integer s : rangeSet) {
			System.out.println(s+" ");
		}
		System.out.println("\n");

		//범위 검색 (80<=score<90)
		rangeSet = scores.subSet(80,true,90,false);
		for(Integer s : rangeSet) {
			System.out.println(s+"");
		}

	}
}
