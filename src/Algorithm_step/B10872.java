package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B10872 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		int N = Integer.parseInt(bf.readLine());
		int result = factorial(N);
		System.out.println(result);

	}
	private static int factorial(int n) {
		if(n<=1) {
			return 1;
		} else {
			return n * factorial(n-1);
		}
	}

}
