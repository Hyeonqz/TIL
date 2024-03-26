package Algorithm.Algorithm_step;

import java.util.Scanner;

public class B10818 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		int N = sc.nextInt();
		int[] arr = new int[N];

		for (int i = 0; i <N ; i++) {
			arr[i] = sc.nextInt();
		}

		int max = arr[0];
		int min = arr[0];

		//최대값, 최소값 비교하는 로직.
		for (int i = 0; i <N ; i++) {
			if(arr[i]>max) {
				max = arr[i];
			}
			if(arr[i]<min) {
				min = arr[i];
			}
		}
		System.out.println(min + " " +max);

	}
}
