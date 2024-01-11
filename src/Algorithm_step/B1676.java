package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B1676 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		int N = Integer.parseInt(bf.readLine());
		long fact = factoiral(N);
		int count = CountZero(fact);

		System.out.println(count);
	}

	//0세기
	private static int CountZero(long number) { //3688800
		int countZero = 0;

		while (number % 10 == 0) {
			countZero++;
			number /= 10;
		}

		return countZero;
	}

	//팩토리얼
	private static long factoiral(int n) {
		if(n<=1) {
			return 1;
		} else {
			return n * factoiral(n-1);
		}
	}
}
