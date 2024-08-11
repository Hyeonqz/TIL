package DataStructure.자료구조;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinkedListTest {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		List<String> list2 = new LinkedList<String>();

		long startTime; //시작시간
		long endTime; //끝난 시간

		startTime = System.nanoTime();
		for (int i = 0; i <10000 ; i++) {
			list.add(0, String.valueOf(i)); //0번 인덱스부터, i값의 value값 다 저장.
		}
		endTime = System.nanoTime();
		System.out.println("ArrayList 걸린시간? ");
		System.out.println("시작시간 : " + startTime);
		System.out.println("끝난시간 : " + (endTime-startTime));

		startTime = System.nanoTime();
		for (int i = 0; i <10000 ; i++) {
			list2.add(0, String.valueOf(i));
		}
		endTime = System.nanoTime();
		System.out.println("LinkedList 걸린시간? ");
		System.out.println("시작시간 : " + startTime);
		System.out.println("끝난시간 : " + (endTime-startTime));

	}
}
