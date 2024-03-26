package Algorithm.Algorithm_step;

import java.util.Scanner;

public class B11720 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();
		String sNum = sc.next();
		char[] cNum = sNum.toCharArray(); //toCharArrya -> String을 char 배열로 담기.
		int sum = 0;

		for (int i = 0; i<cNum.length; i++) {
			sum += cNum[i] - '0'; // 48을 해줘도 됨.
		}
		System.out.println(sum);

	}
}
/*
* */
