package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class B15552 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));

		StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());

		int T = Integer.parseInt(stringTokenizer.nextToken());

		for (int i = 0; i <T ; i++) {
			stringTokenizer = new StringTokenizer(bufferedReader.readLine());
			bufferedWriter.write((Integer.parseInt(stringTokenizer.nextToken()) + Integer.parseInt(stringTokenizer.nextToken()))+"\n");
		}
		bufferedWriter.close();
	}
}
