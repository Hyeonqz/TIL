package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class B1940 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int N = Integer.parseInt(bufferedReader.readLine());
		int M = Integer.parseInt(bufferedReader.readLine());

		int[] A = new int[N];
		StringTokenizer st = new StringTokenizer(bufferedReader.readLine());
		for (int i = 0; i <N ; i++) {
			A[i] = Integer.parseInt(st.nextToken());
		}
		Arrays.sort(A); //오름차순 정렬

		int count=0; //최종 출력 개수
		int startNum=0; //A[0] min
		int endNum = N-1; //A[N-1] max

		while(startNum<endNum) {
			if(A[startNum] + A[endNum] <M) {
				startNum++;
			} else if(A[startNum] + A[endNum]>M) {
				endNum--;
			} else {
				count ++;
				startNum++;
				endNum--;
			}
		}
		System.out.println(count);
	}
}
