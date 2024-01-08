package Algorithm_Normal.Queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class B10866 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		Deque<String> deque = new ArrayDeque<>();

		int inputNum = Integer.parseInt(bf.readLine());
		String[] inputArr = new String[inputNum];

		for (int i = 0; i <inputNum ; i++) {
			inputArr[i] = bf.readLine();
		}

		for (int i = 0; i <inputNum ; i++) {
			String[] outPutArr = inputArr[i].split(" ");

			if(outPutArr[0].equals("push_front")) {
				deque.addFirst(outPutArr[1]);
			}

			if(outPutArr[0].equals("push_back")) {
				deque.addLast(outPutArr[1]);
			}

			if(outPutArr[0].equals("pop_front")) {
				if(deque.isEmpty()) {
					System.out.println("-1");
				} else {
					String push_front = deque.pollFirst();
					System.out.println(push_front);
				}
			}

			if(outPutArr[0].equals("pop_back")) {
				if(deque.isEmpty()) {
					System.out.println("-1");
				} else {
					String push_back = deque.pollLast();
					System.out.println(push_back);
				}
			}

			if(outPutArr[0].equals("size")) {
				int size= deque.size();
				System.out.println(size);
			}

			if(outPutArr[0].equals("empty")) {
				if(deque.isEmpty()) {
					System.out.println("1");
				} else {
					System.out.println("0");
				}
			}

			if(outPutArr[0].equals("front")) {
				if(deque.isEmpty()) {
					System.out.println("-1");
				} else {
					String front = deque.peekFirst();
					System.out.println(front);
				}
			}

			if(outPutArr[0].equals("back")) {
				if(deque.isEmpty()) {
					System.out.println("-1");
				} else {
					String back = deque.peekLast();
					System.out.println(back);
				}
			}

		}

	}
}
