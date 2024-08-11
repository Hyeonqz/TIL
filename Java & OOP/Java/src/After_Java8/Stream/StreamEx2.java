package Java.Stream;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamEx2 {
	public static void main(String[] args) throws Exception{
		Path path = Paths.get(StreamEx2.class.getResource("data.txt").toURI()); //data.txt 파일 경로 객체 얻기
		Stream<String> stream = Files.lines(path, Charset.defaultCharset()); //path로부터 파일을 열고, 한 행씩 읽으면서 문자열 Stream생성
		stream.forEach(line -> System.out.println(line));
		stream.close();
	}
}
