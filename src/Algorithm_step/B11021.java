package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B11021 {
	public static void main(String[] args) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());

		int T = Integer.parseInt(stringTokenizer.nextToken());

		for (int i = 1; i <=T; i++) {
			stringTokenizer = new StringTokenizer(bufferedReader.readLine());
			int A = Integer.parseInt(stringTokenizer.nextToken());
			int B = Integer.parseInt(stringTokenizer.nextToken());
			System.out.println("Case #"+i+": " + (A+B));
		}

	}
}
