package Java.Thread;
class YieldEx {
	public static void main(String[] args) {
		WorkThread workThreadA = new WorkThread("workA");
		WorkThread workThreadB = new WorkThread("workB");
		workThreadA.start();
		workThreadB.start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			workThreadA.work  = false;
		}
		try {
			Thread.sleep(10000) ;
		} catch (InterruptedException e) {
			workThreadB.work = true;
		}

	}
}

public class WorkThread extends Thread{
	public boolean work = true;
	public WorkThread(String name) {
		setName(name);
	}

	@Override
	public void run()  {
		while(true) {
			if(work) {
				System.out.println(getName() + " : 작업처리");
			} else {
				Thread.yield();
			}
		}
	}
}
