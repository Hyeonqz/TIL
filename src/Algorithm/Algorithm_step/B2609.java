package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B2609 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		long A = Long.parseLong(st.nextToken());
		long B = Long.parseLong(st.nextToken());

		long gcd = 0; //최대공약수
		long lcm = 0; //최소공배수

		gcd = getGCD(A,B);
		lcm = A*B/gcd;

		System.out.println(gcd);
		System.out.println(lcm);
	}

	private static long getGCD(long a, long b) {
		while(a!=0 && b!=0) {
			long divide = a%b; //0
			a=b; //6
			b=divide; //
		}
		return a;
	}
}
