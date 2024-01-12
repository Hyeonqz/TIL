package DataStructure.Map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ex1 {
	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("진현규",100);
		map.put("이성신",99);
		map.put("최성현",98);
		map.put("임형준",97);

		System.out.println("총 Entry 수 : " + map.size());
		System.out.println("--------------------------------------------------------");

		//Key로 Value 얻기
		String key = "진현규";
		int value = map.get(key);
		System.out.println(key + ": " + value);
		System.out.println("--------------------------------------------------------");

		//Key Set컬렉션 얻고, 반복해서 키와 값을 얻기
		Set<String> keySet = map.keySet();
		Iterator<String> keyIterator = keySet.iterator();

		while(keyIterator.hasNext()) {
			String k = keyIterator.next();
			Integer v = map.get(k);
			System.out.println(k + ": " + v);
		}
		System.out.println();

		//Entry DataStructure.Set 컬렉션을 얻고, 반복해서 키와 값을 얻기
		Set<Entry<String,Integer>> entrySet = map.entrySet();
		Iterator<Entry<String,Integer>> entryIterator = entrySet.iterator();

		while(entryIterator.hasNext()) {
			Entry<String,Integer> entry = entryIterator.next();
			String k = entry.getKey();
			Integer v=  entry.getValue();
			System.out.println(k + ":" + v);
		}
		System.out.println();

		map.remove("최성현");
		System.out.println("총 Entry 수 : " + map.size());
		System.out.println();
	}
}
