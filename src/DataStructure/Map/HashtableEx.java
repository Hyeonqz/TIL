package DataStructure.Map;

import java.util.Hashtable;
import java.util.Map;

public class HashtableEx {
	public static void main(String[] args) {

		Map<String,Integer> map = new Hashtable<>();

		//A스레드 객체 생성
		Thread threadA = new Thread() {
			@Override
			public void run() {
				for(int i=1; i<=1000; i++) {
					map.put(String.valueOf(i),i);
				}
			}
		};

		//B스레드 객체 생성
		Thread threadB = new Thread() {
			@Override
			public void run() {
				for(int i=1001; i<=2000; i++) {
					map.put(String.valueOf(i),i);
				}
			}
		};

		threadA.start();
		threadB.start();

		try {
			threadA.join();
			threadB.join();
		} catch (Exception e) {
		}

		int size = map.size();
		System.out.println("총 엔트리 수 : " + size);
		System.out.println();

	}
}
