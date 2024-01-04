package 알고리즘;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class B10810 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf =new 	BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		int[] baskets = new int[N+1]; //바구니 시작이 1번부터라서 +1 해줌

		for (int a = 0; a <M ; a++) {
			st = new StringTokenizer(bf.readLine());
			int i = Integer.parseInt(st.nextToken());
			int j = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());

			for (int l = i; l <=j ; l++) { //시작 은 i, 마지막은j 를 해야함
				baskets[l] = k;
			}
		}
		for (int i = 1; i <=N ; i++) {
			System.out.print(baskets[i] + " ");
		}

	}
}
