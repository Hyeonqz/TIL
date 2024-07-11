package Java.Thread;

import java.awt.*;

public class BeenEx {
	public static void main(String[] args) {
		Toolkit toolkit = Toolkit.getDefaultToolkit(); //toolkit객체 얻기
		for (int i = 0; i < 5; i++) {
			toolkit.beep(); //비프음 발생
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			for (int j = 0; i < 5; j++) {
				System.out.println("띵");
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		}
	}
}
