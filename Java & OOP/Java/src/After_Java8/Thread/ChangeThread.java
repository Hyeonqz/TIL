package Java.Thread;

public class ChangeThread {
	public static void main(String[] args) {
		Thread mainThread = Thread.currentThread(); //이 코드를 실해하는 스레드 객체 참조 얻기.
		System.out.println(mainThread.getName() + "실행");

		for (int i = 0; i <3 ; i++) {
			Thread threadA = new Thread() {
				@Override
				public void run() {
					System.out.println(getName() + "실행1"); //쓰레드의 인스턴스 메소드로 스레드의 이름을 리턴한다.
				}
			};
			threadA.start();
		}
		Thread threadB = new Thread() {
			@Override
			public void run() {
				System.out.println(getName() + "실행2");
			}
		};
		threadB.setName("threadB"); //작업스레드 이름변경
		threadB.start();
	}
}
