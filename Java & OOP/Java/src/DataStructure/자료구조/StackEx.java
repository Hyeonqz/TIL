package DataStructure.자료구조;

import java.util.Stack;

class Coin {
	private int value;
	public Coin(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}

public class StackEx {
	public static void main(String[] args) {

		Stack<Coin> coinBox = new Stack<Coin>(); //coin을 스택에 담기

		coinBox.push(new Coin(100)); //처음 넣은거
		coinBox.push(new Coin(500)); //두번째 넣은거
		coinBox.push(new Coin(1000)); //세번째 넣은거
		coinBox.push(new Coin(10000)); //네번째 넣은거

		while(!coinBox.isEmpty()) {
			Coin coin = coinBox.pop();
			System.out.println("꺼낸 동전 : " + coin.getValue() + "원");
		}


	}
}
