package Algorithm_Normal.Queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class B10845 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		int inputNum = Integer.parseInt(bf.readLine());
		String[] inputArr = new String[inputNum];
		Queue<String> queue = new LinkedList<>();
		String lastElement = null;

		for (int i = 0; i <inputNum ; i++) {
			inputArr[i] = bf.readLine();
		}

		for (int i = 0; i <inputNum; i++) {
			String[] outPutArr = inputArr[i].split(" ");

			if(outPutArr[0].equals("push")) {
				queue.add(outPutArr[1]);
				lastElement = outPutArr[1];
			}

			if(outPutArr[0].equals("pop")) {
				if(queue.isEmpty()) {
					System.out.println("-1");
				} else {
					String poll1 = queue.poll();
					System.out.println(poll1);
				}
			}

			if(outPutArr[0].equals("size")) {
				int size1 = queue.size();
				System.out.println(size1);
			}

			if(outPutArr[0].equals("empty")) {
				if(queue.isEmpty()) {
					System.out.println("1");
				} else {
					System.out.println("0");
				}
			}

			if(outPutArr[0].equals("front")) {
				if(queue.isEmpty()) {
					System.out.println("-1");
				} else {
					String front1 = queue.peek();
					System.out.println(front1);
				}
			}

			if(outPutArr[0].equals("back")) {
				if(queue.isEmpty()) {
					System.out.println("-1");
				}  else {
					System.out.println(lastElement);
				}
			}

		}
	}
}
