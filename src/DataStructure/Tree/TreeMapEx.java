package DataStructure.Tree;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class TreeMapEx {
	public static void main(String[] args) {

		//TreeMap 컬렉션 생성
		TreeMap<String,Integer> treemap = new TreeMap<String,Integer>();

		//Entry 저장
		treemap.put("apple",10);
		treemap.put("forever",60);
		treemap.put("description",40);
		treemap.put("ever",50);
		treemap.put("zoo",80);
		treemap.put("base",20);
		treemap.put("guess",70);
		treemap.put("cherry",30);

		//정렬된 Entry 하나씩 가져오기
		Set<Entry<String,Integer>> entrySet = treemap.entrySet();
		for (Entry<String,Integer> entry : entrySet) {
			System.out.println(entry.getKey() + "-" + entry.getValue()); //key를 가져오고 value를 가져온다
		}

		//특정 키에 대한 값 가져오기
		Entry<String,Integer> entry = null;
		entry = treemap.firstEntry();
		System.out.println("제일 앞 단어 : " + entry.getKey() + "-" + entry.getValue());

		entry = treemap.lastEntry();
		System.out.println("제일 마지막 단어 : " + entry.getKey() + "-" + entry.getValue());

		entry = treemap.lowerEntry("ever"); //특정 단어 앞 단어 찾기
		System.out.println("ever 앞 단어 : " + entry.getKey() + "-" + entry.getValue());

		//내림차순으로 정렬하기
		NavigableMap<String,Integer> descendingMap = treemap.descendingMap();
		Set<Entry<String,Integer>> descendingSet = descendingMap.entrySet();

		for(Entry<String,Integer> e : descendingSet) {
			System.out.println(e.getKey() + "-" + e.getValue());
		}
		System.out.println();

		//범위 검색
		System.out.println("[c ~ h] 사이의 단어 검색");
		NavigableMap<String,Integer> rangeMap = treemap.subMap("c",true,"h",false);

		for(Entry<String,Integer> e : rangeMap.entrySet()) {
			System.out.println(e.getKey() + "-" + e.getValue());
		}
	}
}
