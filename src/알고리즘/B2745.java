package 알고리즘;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B2745 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		String N = st.nextToken(); //변환하고 싶은 것
		int B = Integer.parseInt(st.nextToken()); //몇 진수로 변환할 것인가.
		br.close();

		int tmp = 1; //자리번호 오른쪽 기준으로 1번부터 시작
		int sum = 0; //진법 변환 후 합계

		for(int i = N.length()-1 ; i >= 0; i--){ // 여기서, 맨오른쪽 부터 계산!
			char C = N.charAt(i);

			if ('A' <= C && C<= 'Z') {
				sum += (C - 'A' + 10) * tmp;
			} else {
				sum += (C - '0') * tmp;
			}
			tmp *= B;
		}

		System.out.println(sum);
	}
}
