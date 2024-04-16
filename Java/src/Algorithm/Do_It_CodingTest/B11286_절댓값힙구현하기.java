package Algorithm.Do_It_CodingTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

public class B11286_절댓값힙구현하기 {
	public static void main(String[] args) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		int N = Integer.parseInt(bf.readLine());
		PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>(
			(o1,o2) -> {
				//절댓값 작은 데이터 우선
				int first_abs =  getAbs(o1);
				int second_abs =  getAbs(o2);
				if(first_abs==second_abs) { //절댓값이 같은 경우? 음수 우선
					return o1>o2 ? 1:-1;
				}
				return first_abs-second_abs; //절댓값 작은 데이터 우선
		/*		절댓값이 큰 데이터 우선이면?
				 return second_abs-first_abs;*/
			} );

		for (int i = 0; i <N ; i++) {
			int request = Integer.parseInt(bf.readLine());
			if(request == 0) {
				if(priorityQueue.isEmpty()) {
					System.out.println("0");
				} else {
					System.out.println(priorityQueue.poll());
				}
			} else {
				priorityQueue.add(request);
			}
		}
	}

	//절댓값 가져오기
	private static int getAbs(int a) {
		int abs = Math.abs(a);
		return abs;
	}
}
