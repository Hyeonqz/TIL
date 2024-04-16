package DataStructure.Set;

public class HashSetMember {
	public String name;
	public int age;

	public HashSetMember(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public int hashCode() {
		return name.hashCode() + age; //name과age값이 같으면 동일한 hashcode가 리턴됨
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HashSetMember target) {
			return target.name.equals(name) && (target.age==age);
		} else {
			return false;
		}
	}
}
