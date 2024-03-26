package DataStructure.Tree;

import java.util.TreeSet;

class Person implements Comparable<Person> {

	public String name;
	public int age;

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public int compareTo(Person o) {
		if(age<o.age) {
			return -1;
		}
		if(age==o.age) {
			return 0;
		} else {
			return 1;
		}
	}
}
public class ComparableEx {
	public static void main(String[] args) {
		TreeSet<Person> treeSet = new TreeSet<Person>();

		treeSet.add(new Person("임형준",100));
		treeSet.add(new Person("진현규",99));
		treeSet.add(new Person("이성신",98));
		treeSet.add(new Person("최성현",97));

		for(Person person : treeSet) {
			System.out.println(person.name + ":" + person.age);
		}
	}
}
