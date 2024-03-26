package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B10952 {
	public static void main(String[] args) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer stringTokenizer;

		while(true) {
			stringTokenizer = new StringTokenizer(bufferedReader.readLine());

			int A = Integer.parseInt(stringTokenizer.nextToken());
			int B = Integer.parseInt(stringTokenizer.nextToken());

			if(A==0 && B==0) {
				break;
			}
			System.out.println(A+B);
		}
	}
}
