package Algorithm.DP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B9095 {
	public static void main(String[] args) throws IOException {

		//3시52분 시작
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int T = Integer.parseInt(br.readLine());
		int[] arr = new int[T];
		int[] sumArr = new int[T];

		for (int i = 0; i <T ; i++) {
			arr[i] = Integer.parseInt(br.readLine());
			sumArr[i] = Count(arr[i]);
		}

		for (int i = 0; i <T; i++) {
			System.out.println(sumArr[i]);
		}
	}
	private static int Count(int n) {
		int[] dp = new int[n + 1];

		if(n>=1) {
			dp[1] = 1;
		}
		if (n >= 2) {
			dp[2] = 2; //각 배열마다 그 인덱스에 맞는, 경우의 수를 넣는다.
		}
		if (n >= 3) {
			dp[3] = 4; //각 배열마다 그 인덱스에 맞는, 경우의 수를 넣는다.
		}

		//우리는 숫자를 1,2,3만 사용해야 하니 조건은 3까지 경우의 수로 제한한다.
		//그렇게 해서 쭉 각 수마다 1,2,3,으로 만들 수 있는 합의 경우의 수를 저장하고 마지막에 최종적으로 더해서 n에 맞는 총 경우의 수를 구하는 로직이다.

		//i가 4인 이유는 dp배열은 이미0,1,2,3 인덱스가 꽉차 있다. 그러므로 4부터 시작한다. 이것을 사용하기 위해서 처음 배열에 n+1을 해준 것 이다.
		//만약 문제에 숫자,1,2,3,4 4가지를 이용하라고 했다면 위에 if조건에 4까지 들어가고, for문은 5부터 시작했을 것이다.
		for (int i = 4; i <= n; i++) {
			dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
			//문제가 1,2,3의 활용하여 문제를 찾는 것 이기 때문에 1,2,3을 빼고 위 수들을 배열의 합을 구하는 것이다.
			//배열로 하기 싫다면 재귀함수를 이용해도 된다.
		}

		return dp[n];
	}

}
