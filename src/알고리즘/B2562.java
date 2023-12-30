package 알고리즘;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B2562 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		int[] arr = new int[9];
		for (int i = 0; i <arr.length ; i++) {
			arr[i] = Integer.parseInt(bufferedReader.readLine());
		}
		int max = arr[0]; //최댓값 찾기
		int maxIndex = 0; //최댓값 위치 찾기

		for (int i = 0; i <arr.length ; i++) {
			if(arr[i]>max) {
				max=arr[i];
				maxIndex=i;
			}
		}
		System.out.println(max);
		System.out.println(maxIndex+1); //(배열은 0부터 시작 하니 +1)

	}
}
