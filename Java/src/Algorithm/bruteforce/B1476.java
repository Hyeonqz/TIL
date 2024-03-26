package Algorithm.bruteforce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class B1476 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		StringTokenizer st = new StringTokenizer(br.readLine());

		//1차 시간초과
		//

		int E = Integer.parseInt(st.nextToken());
		int S = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		int e = 0, s=0, m=0, year=0;

		while (true) {
			e++;
			s++;
			m++;
			year++;
			if (e > 15) {
				e = 1;
			}
			if (s > 28){
				s=1;
			}
			if (m>19) {
				m=1;
			}
			if (e == E && m == M && S == s) {
				break;
			}

		}
		bw.write(year + "\n");
		bw.flush();
		bw.close();
		br.close();
	}
}
