package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B1929 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		for (int i = N; i <=M ; i++) {
			if(isPrime(i)) {
				System.out.println(i);
			}
		}
	}

	private static boolean isPrime(int num) {
		if(num<2) {
			return false;
		}
		for (int i = 2; i*i<=num ; i++) {
			if(num%i==0) {
				return false;
			}
		}
		return true;
	}
}
