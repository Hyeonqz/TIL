package Java.Lambda;

public class LambdaEx2 {
	public static void main(String[] args) {
		Person person = new Person();

		//실행문이 2개이상
		person.action1((name,job) -> {
			System.out.println(name + " 님이");
			System.out.println(job + " 합니다");
		});

		//실행문 1개
		person.action2((content) -> {
			System.out.println(content + " 라고 말합니다.");
		});
	}
}
