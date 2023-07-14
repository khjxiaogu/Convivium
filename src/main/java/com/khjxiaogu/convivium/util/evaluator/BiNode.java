package com.khjxiaogu.convivium.util.evaluator;

abstract class BiNode implements Node{
	Node left;
	Node right;
	public BiNode(Node left, Node right) {
		super();
		this.left = left;
		this.right = right;
	}
	
}