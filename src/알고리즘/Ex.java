package 알고리즘;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Ex {
	public static void main(String[] args) {

		Scanner sc= new Scanner(System.in);
		int test = sc.nextInt();
		int answer = 0;
		int A[] = new int[100001];
		int B[] = new int[100001];

		for (int i = 0; i <100001 ; i++) {
			A[i] = (int)(Math.random() * Integer.MAX_VALUE);
			B[i] = B[i-1] + A[i];

			List<String> a = new LinkedList<>();
		}
	}
}
