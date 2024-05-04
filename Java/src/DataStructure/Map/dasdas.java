package DataStructure.Map;

import java.util.ArrayList;
import java.util.HashMap;

public class dasdas extends A{

	public dasdas () {
		System.out.println("b");
	}

	public dasdas (String a) {
		System.out.println("파라미터 1개 자식");
	}

	public static void main (String[] args) {
		A a = new dasdas();
		dasdas d = new dasdas();
		dasdas d1 = new dasdas("파라미터1개 임?");
	}
}

class A {
	A() {
		System.out.println("a");
	}

	A(String a) {
		System.out.println("파라미터 1개 부모");
	}
}
