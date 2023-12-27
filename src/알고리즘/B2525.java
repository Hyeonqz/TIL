package 알고리즘;

import java.util.Scanner;

public class B2525 {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int hour = scanner.nextInt();
		int min = scanner.nextInt();
		int cookTime = scanner.nextInt();

		int outputValue = 60 * hour + min; //분 단위로 만든다
		outputValue = outputValue + cookTime; // 분 단위 + 추가 시간

		int outHour = (outputValue/60)%24; //24를 나눈 이유는 나중에 나눴을때 24일 경우 0시를 리턴해줘야하기 때문입니다.
		int outMin = outputValue%60; //나눈 나머지가 분이고, 60으로 나눈 이유도 60으로 이나왔을시 0으로 리턴해줘야 하기 떄문.

		System.out.println(outHour + " " + outMin);

	}
}
