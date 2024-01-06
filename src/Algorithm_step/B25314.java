package Algorithm_step;

import java.util.Scanner;

public class B25314 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();
		String A = "long int";
		String B = "long";

			if(N==4) {
				System.out.println(A);
			}

		if(N>5) {
			int S = (N/4)-1;
			for (int j = 1; j <=S ; j++) {
				System.out.print(B+" ");
			}
			System.out.print(A);
		}
	}
}
