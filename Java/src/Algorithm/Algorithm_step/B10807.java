package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class B10807 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));

		int N = Integer.parseInt(bufferedReader.readLine());

		StringTokenizer stringTokenizers = new StringTokenizer(bufferedReader.readLine());
		int[] arrays = new int[N];
		for (int i = 0; i < N; i++) {
			arrays[i] = Integer.parseInt(stringTokenizers.nextToken());
		}

		int V = Integer.parseInt(bufferedReader.readLine()); //한줄을 문자열로 읽어온다.

		int vSum = 0;
		for (int i = 0; i < N; i++) {
			if (arrays[i] == V) {
				vSum++;
			}
		}

		bufferedWriter.write(String.valueOf(vSum));
		bufferedWriter.newLine(); //줄 바꿈

		bufferedWriter.flush();
	}
}
