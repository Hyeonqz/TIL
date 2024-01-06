package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B11659 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		//데이터를 한줄로 쭉 받아올 때 사용
		StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());

		//int 값이므로 stringTokenizer를 파싱에서 받아와야함
		int suNo = Integer.parseInt(stringTokenizer.nextToken());
		int quizNo = Integer.parseInt(stringTokenizer.nextToken());

		//배열 0번째 인덱스 무시하고 1부터 시작하게 하려고 +1을 함
		long[] S = new long[suNo+1];

		stringTokenizer = new StringTokenizer(bufferedReader.readLine());
		//합배열 구하기
		for (int i = 1; i <=suNo; i++) {
			S[i] = S[i-1] + Integer.parseInt(stringTokenizer.nextToken());
		}

		//구간합 구하기
		for (int q = 0; q <quizNo ; q++) {
			stringTokenizer = new StringTokenizer(bufferedReader.readLine());
			int i = Integer.parseInt(stringTokenizer.nextToken());
			int j = Integer.parseInt(stringTokenizer.nextToken());
			System.out.println(S[j] - S[i-1]);
		}

	}
}
