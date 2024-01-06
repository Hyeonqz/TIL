package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B10813 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		int[] arr = new int[N+1]; //시작을0이 아닌 1로 시작하기 위함.


		for (int i = 1; i <=N ; i++) {
			arr[i] = i;
		}

		for (int a = 0; a <M ; a++) {
			st = new StringTokenizer(bf.readLine());
			int i = Integer.parseInt(st.nextToken());
			int j = Integer.parseInt(st.nextToken());

			//i번 바구니 j번 바구니 교환
			int tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;

		}
		for (int i = 1; i <=N ; i++) {
			System.out.print(arr[i] + " ");
		}
	}
}
