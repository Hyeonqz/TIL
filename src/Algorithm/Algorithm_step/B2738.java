package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B2738 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		int[][] A = new int[N][M];
		int[][] B = new int[N][M];

		for (int i = 0; i <N ; i++) {
			st = new StringTokenizer(bf.readLine());
			for (int j = 0; j <M ; j++) {
				A[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		for (int i = 0; i <N ; i++) {
			st = new StringTokenizer(bf.readLine());
			for (int j = 0; j <M ; j++) {
				B[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		//출력
		for (int i = 0; i <N ; i++) {
			for (int j = 0; j <M ; j++) {
				System.out.print(A[i][j] + B[i][j] + " ");
			}
			System.out.println();
		}

	}
}
