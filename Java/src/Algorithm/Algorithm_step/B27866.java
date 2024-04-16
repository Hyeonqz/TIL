package Algorithm.Algorithm_step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class B27866 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		String s = bf.readLine();
		int i  = Integer.parseInt(bf.readLine());

		char output = s.charAt(i-1);

		System.out.println(output);
	}
}
