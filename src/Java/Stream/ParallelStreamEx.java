package Java.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParallelStreamEx {
	public static void main(String[] args) {

		//List
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");

		//병렬 처리
		Stream<String> parellelStream = list.parallelStream(); //병렬 스트림 얻기
		parellelStream.forEach( name -> {
			System.out.println(name + " : " + Thread.currentThread().getName()); //람다식 사용해서 요소 처리
		});
	}
}
