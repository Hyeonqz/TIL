package Algorithm.DP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B11726 {

	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		int N = Integer.parseInt(bf.readLine());
		int result = count(N);
		System.out.println(result);

	}

	private static int count(int a) {
		int[] dp = new int[1001];
		int divideNum = 10007;

		dp[1] = 1;
		dp[2] = 2;

		for (int i = 3; i <=a ; i++) {
			dp[i] = (dp[i-1] + dp[i-2])%divideNum;
		}
		return dp[a];
	}
}
