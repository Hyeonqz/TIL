package DataStructure.자료구조;

import java.util.HashMap;
import java.util.Map;


public class ASyncEx {
	public static void main(String[] args) {

		Map<Integer,String> map = new HashMap<Integer,String>();

		//작업 스레드 생성
		Thread thread = new Thread() {
			@Override
			public void run() {
				for (int i = 1; i <=1000 ; i++) {
					map.put(i,"내용+i");
				}
			}
		};

		//작업스레드 2 생성
		Thread thread2 = new Thread() {
			@Override
			public void run() {
				for (int i = 1001; i <=2000 ; i++) {
					map.put(i,"내용+i");
				}
			}
		};

		//thread 실행 메소드
		thread.start();
		thread2.start();

		try {
			thread.join(); //스레드들이 모두 종료될 때 까지 메인 스레드 기다리게하는 메소드 -> join
			thread2.join();
		} catch (Exception e) {

		}

		//저장된 총 객체 수 얻기
		int size = map.size();
		System.out.println("총 객체 수 : " + size);
		System.out.println();

	}
}
