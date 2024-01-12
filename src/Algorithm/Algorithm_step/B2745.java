package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B2745 {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		/*
		* 예제는 ZZZZZ를 36진수로 출력해달라고 함
		→ 35*36^5 + 35*36^4 ~~ 35*36^1 ⇒ `60466175` 이 나온다
		(10진법숫자 * 진법 ^ 자리번호) 를 쭉 더하게 되면 합계가 나온다.
		*/

		String N = st.nextToken(); //변환하고 싶은 것
		int B = Integer.parseInt(st.nextToken()); //몇 진수로 변환할 것인가.
		br.close();

		int tmp = 1; //0이 아닌 1인 이뉴는 0*36은 0이기 때문
		int sum = 0; //진법 변환 후 합계

		for(int i = N.length()-1 ; i >= 0; i--){ // 여기서, 맨오른쪽 부터 계산!e
			char C = N.charAt(i);

			if ('A' <= C && C<= 'Z') { //위 조건 만족하지 않을시 10이하 인 숫자임.
				sum += (C - 'A' + 10) * tmp; //'A'를 해두 되고 65를 해도 됨.
			} else { //10 이하 10진법 숫자 일 떄
				sum += (C - '0') * tmp; // '0' 대신 48을 해두됨
			}
			tmp *= B; //최종 자릿수 계산. 2^1*36 -> 2^2*36 -> 2^3*36
		}

		//최종 sum
		//(35 * 1) + (35 * 36) + (35 * 36^2) + (35 * 36^3) + (35 * 36^4)
		System.out.println(sum);
	}
}
