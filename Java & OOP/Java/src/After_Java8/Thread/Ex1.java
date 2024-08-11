package Java.Thread;

public class Ex1 {
	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//스레드가 실행할 코드
			}
		});
	}
}
