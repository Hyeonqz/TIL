package Algorithm.TreeEx;

import java.util.Scanner;

public class B1991_트리순회 {
	static Node root = new Node();
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		root.index = "A";
		int N = sc.nextInt();

		for(int i=0;i<N;i++) {
			String parent = sc.next();
			String left = sc.next();
			String right = sc.next();

			addNode(root, parent, left, right);

		}
		preorder(root);
		System.out.println();
		midorder(root);
		System.out.println();
		postorder(root);
	}

	static void addNode(Node temp, String parent, String leftchild, String rightchild) {
		if(temp.index.equals(parent)) {
			if(!leftchild.equals(".")) {
				temp.left = new Node(leftchild);
			}
			if(!rightchild.equals(".")) {
				temp.right = new Node(rightchild);
			}
		}else {
			if(temp.left != null) addNode(temp.left, parent, leftchild, rightchild);
			if(temp.right != null) addNode(temp.right, parent, leftchild, rightchild);
		}

		return ;
	}

	static void preorder(Node start) {
		if(start == null) return ;
		System.out.print(start.index);
		preorder(start.left);
		preorder(start.right);

	}
	static void midorder(Node start) {
		if(start == null) return ;

		midorder(start.left);
		System.out.print(start.index);
		midorder(start.right);

	}
	static void postorder(Node start) {
		if(start == null) return ;
		postorder(start.left);
		postorder(start.right);
		System.out.print(start.index);
	}

	static class Node{
		String index;
		Node left;
		Node right;
		Node(){

		}
		Node(String index){
			this.index = index;
		}

	}
}
