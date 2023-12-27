package 알고리즘;

import java.util.Scanner;

public class B2480 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		int diceNum0 = sc.nextInt();
		int diceNum1 = sc.nextInt();
		int diceNum2 = sc.nextInt();

		if(diceNum0 != diceNum1 && diceNum1!=diceNum2 && diceNum0!=diceNum2) {
			int max;
			if(diceNum0>diceNum1) {
				if(diceNum2>diceNum0) {
					max = diceNum2;
				} else {
					max = diceNum0;
				}
			} else {
				if(diceNum2>diceNum1) {
					max = diceNum2;
				} else {
					max = diceNum1;
				}
			}
			System.out.println(max*100);
		} else {
			if(diceNum0==diceNum1 && diceNum0==diceNum2) { //3개다 같은 경우
				System.out.println(10000+(diceNum0*1000));
			} else {
				if(diceNum0==diceNum1 || diceNum0==diceNum2) {//1이 2,3,둘중 하나랑 같은 경우
					System.out.println(1000 + diceNum0*100);
				} else { //2,3이 같은 경우
					System.out.println(1000 + diceNum1*100);
				}
			}
		}


	}
}
