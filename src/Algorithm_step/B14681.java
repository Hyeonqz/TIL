package Algorithm_step;

import java.util.Scanner;

//사분면 고르기
public class B14681 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int x = sc.nextInt();
		int y = sc.nextInt();

		if(x>0 && y>0) {
			System.out.println("1");
		}
		if(x<0 && y>0) {
			System.out.println("2");
		}
		if(x<0 && y<0) {
			System.out.println("3");
		}
		if(x>0 && y<0) {
			System.out.println("4");
		}

	}
}
