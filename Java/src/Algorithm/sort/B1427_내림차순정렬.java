package Algorithm.sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B1427_내림차순정렬 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String str = br.readLine();
		int[] A = new int[str.length()];

		for (int i = 0; i <str.length() ; i++) {
			A[i] = Integer.parseInt(str.substring(i, i+1));
		}

		//선택정렬
		for (int i = 0; i <str.length() ; i++) {
			int Max = i;
			for (int j = i+1; j <str.length() ; j++) {
				if(A[j]>A[Max]) {
					Max = j;
				}
			}
			//swap 로직
			if (A[i] < A[Max]) {
				int tmp = A[i];
				A[i] = A[Max];
				A[Max] = tmp;
			}
		}

		for (int i = 0; i <str.length() ; i++) {
			System.out.print(A[i]);
		}
	}
}
