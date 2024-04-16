package DataStructure.자료구조;

import java.util.ArrayList;
import java.util.List;

public class BoardEx {
	public static void main(String[] args) {
		List<Board> list = new ArrayList<>();
		list.add(new Board("제목","내용1","글쓴이1"));
		list.add(new Board("제목1","내용2","글쓴이2"));
		list.add(new Board("제목2","내용3","글쓴이3"));
		list.add(new Board("제목3","내용4","글쓴이4"));
		list.add(new Board("제목4","내용5","글쓴이5"));
		list.add(new Board("제목5","내용6","글쓴이6"));

		int size = list.size();
		System.out.println("총 객체수 : " + size);
		System.out.println();

		//특정인덱스 객체 가져오기
		Board board = list.get(2);
		System.out.println(board.getSubject() + "/" +board.getContent() + "/" +board.getWriter());
		System.out.println();

		//모든 객체를 하나씩 가져오기
		for (int i = 0; i <list.size() ; i++) {
			Board b = list.get(i);
			System.out.println(b.getSubject() + "-" +  b.getWriter() + "-" + b.getContent());
		}
		System.out.println();

	 	list.remove(2); //2번 인덱스 삭제했어도
	 	list.remove(2); //2번이 삭제되면 3번이 다시 내려와서 2번이 되기 때문에 또 삭제가 가능하다.

		for (Board b : list) {
			System.out.println(b.getSubject() + "-" +  b.getWriter() + "-" + b.getContent());
		}

	}
}
