package Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class B3052 {
	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		int remind = 42;

		Set<Integer> set = new HashSet<>();

		for (int i = 0; i <10 ; i++) {
			int a = Integer.parseInt(bufferedReader.readLine());
			if(a>0) {
				set.add(a%remind);
			}
		}
		System.out.println(set.size());
	}
}
