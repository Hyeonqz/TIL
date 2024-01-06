package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B9086  {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		int T = Integer.parseInt(bf.readLine());

		String[] testCase = new String[T];

		for (int i = 0; i <T ; i++) {
			testCase[i] = bf.readLine();
		}

		for (int i = 0; i <T ; i++) {

			char first = testCase[i].charAt(0);
			char last = testCase[i].charAt(testCase[i].length()-1);

			System.out.println(first+""+last);
		}

	}
}
