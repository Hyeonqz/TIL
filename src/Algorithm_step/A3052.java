package Algorithm_step;

import java.util.Arrays;
import java.util.Scanner;

public class A3052 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		int[] arr = new int[10];   //42로 나눌 숫자
		int[] arr2 = new int[10];   //42로 나눈 나머지

		for(int i=0;i<arr.length;i++) {
			arr[i] = sc.nextInt();
			arr2[i] = arr[i]%42;
		}

		int[] distinctNum = Arrays.stream(arr2)
			.distinct()
			.toArray();

		System.out.println(distinctNum.length);

	}
}
