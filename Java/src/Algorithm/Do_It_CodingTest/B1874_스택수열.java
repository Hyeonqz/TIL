package Algorithm.Do_It_CodingTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class B1874_스택수열 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		int N  = Integer.parseInt(bf.readLine());
		int[] A = new int[N];

		for (int i = 0; i <N ; i++) {
			A[i] = Integer.parseInt(bf.readLine());
		}

		Stack<Integer> stack = new Stack<>();
		int num = 1;
		boolean result = true;

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i <A.length ; i++) {
			int su = A[i];
			if(su>=num) {
				while(su>=num) {
					stack.push(num++);
					stringBuffer.append("+\n");
				}
				stack.pop();
				stringBuffer.append("-\n");
			} else {
				int n = stack.pop();
				if(n>su) {
					System.out.println("No");
					result = false;
					break;
				} else {
					stringBuffer.append("-\n");
				}
			}
		}
		if(result) {
			System.out.println(stringBuffer.toString());
		}

	}
}
