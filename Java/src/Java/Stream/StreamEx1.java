package Java.Stream;

import java.util.Arrays;
import java.util.List;

public class StreamEx1 {
	public static void main(String[] args) {

		List<Student> list = Arrays.asList(
			new Student("진현규",10),
			new Student("이성신",20),
			new Student("최성현",30)
		);

		double avg = list.stream()
			.mapToInt(Student -> Student.getScore())
			.average()
			.getAsDouble();

		System.out.println("평균 점수 : " + avg);

	}
}
