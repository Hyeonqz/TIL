package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class B5597 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		int[] arr1 = new int[31];
		int[] arr2 = new int[29];
		int[] twoArr = new int[2];

		for (int i = 1; i <30 ; i++) {
			arr1[i] = i;
		}

		for (int i = 1; i <29 ; i++) {
			arr2[i] = Integer.parseInt(bufferedReader.readLine());
		}

		Arrays.sort(arr1);
		Arrays.sort(arr2);

		int idx=0;

		for (int i = 1; i <=30 ; i++) {
			boolean found = false;
			for (int j = 1; j <=28 ; j++) {
				if(arr2[j] == i) {
					found = true;
					break;
				}
			}
			if(!found) {
				twoArr[idx++] = i;
				if(idx==2) {
					break;
				}
			}
		}
		System.out.println(twoArr[0]);
		System.out.println(twoArr[1]);
	}
}
