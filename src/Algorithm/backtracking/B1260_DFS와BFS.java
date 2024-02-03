package Algorithm.backtracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class B1260_DFS와BFS {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());
		int V = Integer.parseInt(st.nextToken());

		int[][] arr = new int[N][N];
		boolean[] check = new boolean[N];

		for (int i = 0; i <M; i++) {
			st=  new StringTokenizer(br.readLine());
			arr[i][i] = Integer.parseInt(st.nextToken());
		}
	}

	private static void dfs(int start) {

	}

	private static void bfs(int start) {

	}
}
