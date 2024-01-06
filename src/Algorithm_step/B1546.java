package Algorithm_step;

import java.util.Scanner;

public class B1546 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();
		long sum = 0;
		long max = 0;

		for (int i = 0; i <N ; i++) {
			int tmp = sc.nextInt();
			if(tmp>max) {
				max = tmp;
			}
				sum += tmp;
		}
		System.out.println(sum*100/max/N);
	}
}
