package Algorithm.Algorithm_step;

import java.util.Scanner;

public class B25304 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		int X = sc.nextInt();
		int N = sc.nextInt();
		int sum =0;

		for (int i = 0; i <N ; i++) { //N개의 수만큼 for문을 돌린다.
			int a = sc.nextInt();
			int b = sc.nextInt();
			sum += a*b;
		}
		if(sum==X) {
			System.out.println("Yes");
		}
		if(sum!=X) {
			System.out.println("No");
		}
	}
}
