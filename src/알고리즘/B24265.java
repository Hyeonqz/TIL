package 알고리즘;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class B24265 {
	private void solution() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(br.readLine());
		System.out.println(1l*n*(n-1)/2);
		System.out.println(2);
	}
	public static void main(String[] args) throws Exception {
		new B24265().solution();
	}
}