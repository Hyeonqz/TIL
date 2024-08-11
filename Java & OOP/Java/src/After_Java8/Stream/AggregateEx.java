package Java.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggregateEx {
    public static void main(String[] args) {

        //정수 배열
        int[] arr = {1,2,3,4,5};

        //리스트
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(6);

        //카운팅
        long count = Arrays.stream(arr)
                .filter(n -> n%2==0)
                .count();
        System.out.println("2의 배수 개수 : " + count);

        //총합
        long sum = Arrays.stream(arr)
                .filter(n -> n%2==0)
                .sum();
        System.out.println("2의 배수 합 : " + sum);

        //평균
        double avg = Arrays.stream(arr)
                .filter(n -> n%2==0)
                .average()
                .getAsDouble();
        System.out.println("평균 : " + avg);

        //최대값
        int max = Arrays.stream(arr)
                .filter(n -> n%2==0)
                .max()
                .getAsInt();
        System.out.println("최대값 : " + max);

        //최소값
        int min = Arrays.stream(arr)
                .filter(n -> n%2==0)
                .min()
                .getAsInt();
        System.out.println("최소값 : " + min);

        //첫번째 요소
        int first = Arrays.stream(arr)
                .filter(n -> n%3==0)
                .findFirst()
                .getAsInt();
        System.out.println("첫 번째 3의 배수 : " + first);


    }
}
