package Java.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectGroupEx {

  public static void main(String[] args) {
    List<Student> list = new ArrayList<Student>();
    list.add(new Student("진",100));
    list.add(new Student("진1",99));
    list.add(new Student("진2",980));
    list.add(new Student("진3",6570));
    list.add(new Student("진4",15746));

    Map<String, List<Student>> map = list.stream()
        .collect(
            Collectors.groupingBy(s -> s.getName())
        );

    List<Student> maleList = map.get("진");
    maleList.stream().forEach(s -> System.out.println(s.getName()));
    System.out.println();

    List<Student> femaleList = map.get("진");
    femaleList.stream().forEach(s -> System.out.println(s.getScore()));

  }

}
