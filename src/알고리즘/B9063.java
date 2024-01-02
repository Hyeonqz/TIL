package 알고리즘;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class B9063 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		int N = Integer.parseInt(bufferedReader.readLine());

		int[] arr1 = new int[N];
		int[] arr2 = new int[N];

		for (int i = 0; i <N; i++) {
			st = new StringTokenizer(bufferedReader.readLine());
			arr1[i] = Integer.parseInt(st.nextToken());
			arr2[i] = Integer.parseInt(st.nextToken());
		}

		//정렬
		Arrays.sort(arr1);
		Arrays.sort(arr2);

		int sum = (arr1[N-1]-arr1[0])*(arr2[N-1] - arr2[0]);

		System.out.println(sum);

	}
}
