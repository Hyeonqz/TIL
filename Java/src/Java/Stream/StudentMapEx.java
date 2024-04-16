package Java.Stream;

import java.util.ArrayList;
import java.util.List;

public class StudentMapEx {
	public static void main(String[] args) {

		List<Student> studentList = new ArrayList<Student>();
		studentList.add(new Student("진현규",100));
		studentList.add(new Student("이성신",99));
		studentList.add(new Student("최성현",98));

		//StudentEx1 클래스를 score 스트림으로 변환
		studentList.stream()
			.mapToInt(s -> s.getScore())
			.forEach(score -> System.out.println("점수 : " + score));

	}
}
