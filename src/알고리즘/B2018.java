package 알고리즘;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B2018 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in));

		int N = Integer.parseInt(bufferedReader.readLine());
		int sum=1;
		int count=1;
		int start_index=1;
		int end_index=1;

		while(end_index!=N) { //이미 N개의 숫자를 count에 1개를 더해놨기 때문이다.
			if(sum==N) {
				count++;
				end_index++;
				sum += end_index;
			} else if(sum>N) {
				sum -= start_index;
				start_index++;
			} else {
				end_index++;
				sum+=end_index;
			}
		}
		System.out.println(count);

	}
}
