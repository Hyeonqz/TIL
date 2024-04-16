package DataStructure.Tree;

import java.util.Comparator;
import java.util.TreeSet;

class Fruit {
	public String name;
	public int price;

	public Fruit(String name, int price) {
		this.name = name;
		this.price = price;
	}
}

class FruitComparator implements Comparator<Fruit> {
	@Override
	public int compare(Fruit o1, Fruit o2) {

		if(o1.price < o2.price) {
			return -1;
		}
		if(o1.price == o2.price) {
			return 0;
		} else {
			return 1;
		}
	}
}

public class ComparatorEx {
	public static void main(String[] args) {

		TreeSet<Fruit> treeSet = new TreeSet<Fruit>();
		treeSet.add(new Fruit("사과",10000));
		treeSet.add(new Fruit("포도",5000));
		treeSet.add(new Fruit("샤머캣",9700));

		for(Fruit f : treeSet) { //treeset에 내장된 객체의 갯수만큼 for문을 돌려서 f변수에 저장한다.
			System.out.println(f.name +  ":" + f.price);
		}
	}
}
