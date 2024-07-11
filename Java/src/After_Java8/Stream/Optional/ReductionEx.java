package Java.Stream.Optional;

import java.util.Arrays;
import java.util.List;

public class ReductionEx {
    public static void main(String[] args) {

        List<Student1> list = Arrays.asList(
                new Student1("진현규",10),
                new Student1("이성신",20),
                new Student1("최성현",30)
        );

        //Ex1
        int sum1 = list.stream()
                .mapToInt(Student1 :: getScore)
                .sum();

        //EX2
        int sum2 = list.stream()
                .mapToInt(Student1 :: getScore)
                .reduce(0, (a,b) -> a+b);

        System.out.println(sum1);
        System.out.println(sum2);
    }
}
