package Algorithm.Do_It_CodingTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class B2164_카드게임 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		Queue<Integer> myqueue = new LinkedList<>();
		int N = Integer.parseInt(bufferedReader.readLine());

		for (int i = 1; i <=N ; i++) {
			myqueue.add(i);
		}
		while (myqueue.size() >1) {
			myqueue.poll(); //맨 앞 버림
			int tmp = myqueue.poll(); // 그 다음 버리는 수 변수에 저장
			myqueue.add(tmp); //버려진 수를 맨 뒤에 저장
		}
		System.out.println(myqueue.poll());
	}
}
