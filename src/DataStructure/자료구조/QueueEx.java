package DataStructure.자료구조;

import java.util.LinkedList;
import java.util.Queue;

class Message {
	public String command;
	public String to;

	public Message(String command, String to) {
		this.command = command;
		this.to = to;
	}
}

public class QueueEx {
	public static void main(String[] args) {

		Queue<Message> queue = new LinkedList<>();

		queue.offer(new Message("sendMail","이성신"));
		queue.offer(new Message("sendSMS","진현규"));
		queue.offer(new Message("카톡","최성현"));

		while(!queue.isEmpty()) {
			Message message = queue.poll();

			switch (message.command) {
				case "sendMail" :
					System.out.println(message.to + "님에게 이메일을 보냅니다.");
					break;

				case "sendSMS" :
					System.out.println(message.to + "님에게 문자를 보냅니다");
					break;

				case "카톡" :
					System.out.println(message.to + "님에게 카톡을 보냅니다.");
					break;
			}
		}
	}
}
