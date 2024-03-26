package Algorithm.DataStructure.Stack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class B10828 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		//명령어를 몇번 입력할 지, 스택의 배열과는 상관없음.
		int inputNum = Integer.parseInt(bf.readLine());

		Stack<String> stack = new Stack<String>();
		String[] arr = new String[inputNum];

		for (int i = 0; i <inputNum ; i++) {
			arr[i] = bf.readLine();
		}

		for (int i = 0; i <inputNum ; i++) {
			String[] outPutArr = arr[i].split(" ");

			if(outPutArr[0].contains("push")) {
				stack.push(outPutArr[1]); //split을 나눴기 때문에 1을 넣어준 것
			}

			if(outPutArr[0].contains("top")) {
				if(stack.isEmpty()) {
					System.out.println("-1");
				} else {
					String peek1 = stack.peek();
					System.out.println(peek1);
				}
			}

			if(outPutArr[0].contains("size")) {
				int size = stack.size();
				System.out.println(size);
			}

			if(outPutArr[0].contains("pop")) {
				if(stack.isEmpty()) {
					System.out.println("-1");
				} else {
					String pop1 = stack.pop();
					System.out.println(pop1);
				}
			}

			if(outPutArr[0].contains("empty")) {
				if(stack.isEmpty()) {
					System.out.println("1");
				} else {
					System.out.println("0");
				}
			}

		}

	}
}
