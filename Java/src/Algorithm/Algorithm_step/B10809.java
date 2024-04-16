package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B10809 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = br.readLine();
		char[] charArr = new char[26];
		char[] arr = input.toCharArray();

		// a~z 담기 배열에
		for (int i = 0; i < 26; i++) {
			charArr[i] = (char) ('a' + i);
		}

		// 비교 로직
		for (int i = 0; i < 26; i++) {
			boolean found = false;
			for (int j = 0; j < arr.length; j++) {
				if (charArr[i] == arr[j]) {
					System.out.print(j + " ");
					found = true;
					break; //처음 for문으로 돌아가기
				}
			}
			if (!found) {
				System.out.print("-1 ");
			}
		}
	}
}
