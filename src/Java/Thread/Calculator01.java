package Java.Thread;
class User1Thread extends Thread {
	private Calculator01 calculator01;

	public User1Thread() {
		setName("User1Thread");
	}
	public void setCalculator01(Calculator01 calculator01) {
		this.calculator01 = calculator01;
	}
	@Override
	public void run() {
		calculator01.setMemory1(100);
	}
}

class User2Thread extends Thread {
	private Calculator01 calculator01;

	public User2Thread() {
		setName("User2Thread");
	}
	public void setCalculator01(Calculator01 calculator01) {
		this.calculator01 = calculator01;
	}
	@Override
	public void run() {
		calculator01.setMemory2(50);
	}
}

public class Calculator01 {
	private int memory;
	public int getMemory() {
		return memory;
	}
	public synchronized void setMemory1(int memory) {
		this.memory = memory; //메모리값 저장
		try {
			Thread.sleep(2000); //2초간 일시정지
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + " : " + this.memory);
		}
	}
	public synchronized void setMemory2(int memory) {
		this.memory = memory;
		try {
			Thread.sleep(2000); //2초간 일시정지
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + " : " + this.memory);
		}
	}

	public static void main(String[] args) {
		Calculator01 calculator01 = new Calculator01();
		User1Thread user1Thread = new User1Thread();
		user1Thread.setCalculator01(calculator01);
		user1Thread.start();

		User2Thread user2Thread = new User2Thread();
		user2Thread.setCalculator01(calculator01);
		user2Thread.start();
	}






















}
