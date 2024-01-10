package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B2609 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int A = Integer.parseInt(st.nextToken());
		int B = Integer.parseInt(st.nextToken());

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
		//24,18
		//18,6
		//6,0 -> 0이 므로 while문 종료
		return a;
	}
}
