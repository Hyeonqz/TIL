package Algorithm.DP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B9655 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		long N = Long.parseLong(br.readLine());
		getName(N);
	}
	private static void getName(long a) {
		if(a%2==1) {
			System.out.println("SK");
		}
		if(a%2==0) {
			System.out.println("CY");
		}
	}
}
