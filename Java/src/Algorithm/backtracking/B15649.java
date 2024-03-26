package Algorithm.backtracking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class B15649 {
	private static int[] arr;
	private static boolean[] visit;
	public static StringBuilder sb = new StringBuilder();
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());

		arr = new int[M]; //순열 개수
		visit = new boolean[N]; //범위
		dfs(N, M, 0);
		System.out.println(sb);
	}
	public static void dfs(int N, int M, int depth) {
		//재귀 길이가 M과 같아지면 탐색과정에서 담았던 배열을 출력한다.
		if (depth == M) {
			for (int val : arr) {
				sb.append(val).append(' ');
			}
			sb.append('\n');
			return;
		}

		for (int i = 0; i < N; i++) {
			if (!visit[i]) { //만약 노드 값을 방문하지 않았다면?
				visit[i] = true; // 해당 노드를 방문상태로 바꾼다
				arr[depth] = i + 1; //해당 깊이를 index로 하여 i+1값을 배열에 저장한다.
				dfs(N, M, depth + 1); //다음 자식 노드 방문을 위해 depth 1증가시키면서 재귀를 호출한다.
				visit[i] = false; //자식노드 방문이 끝나고 돌아오면 방문노드를 방문하지 않은 상태로 변경한다.
			}
		}
	}

}