package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B12891 {
	static int[] checkArr;
	static int[] myArr;
	static int checkSecret;
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(bf.readLine());

		int S = Integer.parseInt(st.nextToken()); //문자열 전체 크기 입력 받기
		int P = Integer.parseInt(st.nextToken()); //부분 문자열 크기 입력 받기
		int result = 0; //최종 정답 출력 값

		checkArr = new int[4]; //4로 한 이유는 DNA비밀번호가 4자리 이기 때문
		myArr = new int[4];//checkArr이 문제을 조건 현재 상태 배열 -> 내 부분 문자배열이 뭔지
		char[] A = new char[S]; //처음에 들어오는 전체 문자열 받는 배열 생성

		checkSecret = 0; //현재 몇개가 비밀번호 요건에 만족하는지 나타내는 변수
		//부분 문자를 탐색 했을 때, 몇개 문자가 만족하는지, 전체 부분 문자가 만족하는 4가 되면 ++

		A = bf.readLine().toCharArray();
		st = new StringTokenizer(bf.readLine());

		//for문 4번 반복을 통해 DNA비밀번호 숫자 채우기. -> 조건 주기 위함 1 0 0 1 이런식으로
		for (int i = 0; i <4 ; i++) {
			checkArr[i] = Integer.parseInt(st.nextToken());
			if(checkArr[i]==0) { //0이 들어와 있다는 것은 없어도 된다는 문자열이다.
				checkSecret++; //4개가 만족해야하는데 0이라는건 이미 만족했다는 뜻이기 때문이다.
			}
		}

		//P 부분 문자열 처리 , 처음 받을때 세팅
		for (int i = 0; i <P ; i++) {
			//메소드 만들어서 사용
			//입력된 첫번째 값을, 현재 상태 배열에 넣어준다.
			Add(A[i]);
		}
		if(checkSecret==4) {
			result++;
		}

		//슬라이딩 윈도우
		//한칸 옮겨가서 index=0 은 처리한 다음 상태를 처리하는 것
		for (int i = P; i <S ; i++) {
			int j = i-P;
			//맨 왼쪽 뜻 j
			//맨 오른쪽 i==P
			// ++은 P가 증가함에 따라 한칸씩 옮겨가는 느낌
			Add(A[i]);
			remove(A[j]);
			if(checkSecret==4) {
				result++;
			}
		}
		System.out.println(result);
		bf.close();

	}

	private static void remove(char c) {
		switch(c) {
			case 'A':
				if(myArr[0]==checkArr[0]) {
					checkSecret--;
					myArr[0]--;
				}
				break;
			case 'C':
				if(myArr[1]==checkArr[1]) {
					checkSecret--;
					myArr[1]--;
				}
				break;
			case 'G':
				if(myArr[2]==checkArr[2]) {
					checkSecret--;
					myArr[2]--;
				}
				break;
			case 'T':
				if(myArr[3]==checkArr[3]) {
					checkSecret--;
					myArr[3]--;
				}
				break;
		}
	}

	private static void Add(char c) {
		switch (c) {
			case 'A':
				myArr[0]++;
				if(myArr[0]==checkArr[0]) {
					checkSecret++;
				}
				break;
			case 'C':
				myArr[1]++;
				if(myArr[1]==checkArr[1]) {
					checkSecret++;
				}
				break;
			case 'G':
				myArr[2]++;
				if(myArr[2]==checkArr[2]) {
					checkSecret++;
				}
				break;
			case 'T':
				myArr[3]++;
				if(myArr[3]==checkArr[3]) {
					checkSecret++;
				}
				break;
		}
	}
}
