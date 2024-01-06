package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B10811 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		int[] baskets = new int[N+1];

		for (int i = 1; i <=N ; i++) {
			baskets[i] = i;
		}

		for (int a = 0; a <M ; a++) {
			st = new StringTokenizer(bf.readLine());
			int i = Integer.parseInt(st.nextToken());
			int j = Integer.parseInt(st.nextToken());
			
			reverseBaskets(baskets,i,j);
		}

		for (int i = 1; i <=N ; i++) {
			System.out.print(baskets[i] + " ");
		}
	}
	private static void reverseBaskets(int[] arr, int start, int end) {
		while(start<end) {
			int tmp = arr[start];
			arr[start] = arr[end];
			arr[end] = tmp;

			start++;
			end--;
		}
	}
}
