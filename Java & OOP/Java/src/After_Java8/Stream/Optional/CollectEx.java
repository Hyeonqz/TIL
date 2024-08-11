package Java.Stream.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectEx {
    public static void main(String[] args) {

        List<StudentEx2> list = new ArrayList<StudentEx2>();
        list.add(new StudentEx2("진현규8","남",92));
        list.add(new StudentEx2("진현규7","남",93));
        list.add(new StudentEx2("진현규6","남3",94));
        list.add(new StudentEx2("진현규5","남",95));

        List<StudentEx2> maleList = list.stream()
                .filter(s->s.getSex().equals("남"))
                .toList();

        maleList.stream()
                .forEach(s -> System.out.println(s.getName()));

        System.out.println();

        Map<String,Integer> map = list.stream()
                .collect(
                        Collectors.toMap(
                                s -> s.getName(),
                                s -> s.getScore()
                        )
                );
        System.out.println(map);
    }
}
