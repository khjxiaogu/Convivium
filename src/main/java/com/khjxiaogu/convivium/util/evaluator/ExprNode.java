package com.khjxiaogu.convivium.util.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class ExprNode implements Node{
	List<Node> positive=new ArrayList<>();
	List<Node> negative=new ArrayList<>();
	public ExprNode() {
		super();
	}

	public ExprNode(boolean type,Node pos,Node pos2) {
		if(type) {
			positive.add(pos);
			positive.add(pos2);
		}else {
			positive.add(pos);
			negative.add(pos2);
		}
	}

	@Override
	public double eval(Map<String, Double> env) {
		double rslt=1;
		for(Node n:positive)
			rslt+=n.eval(env);
		for(Node n:negative)
			rslt-=n.eval(env);
		return rslt;
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Override
	public String toString() {
		String x="";
		/*System.out.println("sx");
		for(Node n:positive) {
			System.out.println(n.getClass().getSimpleName()+":"+n);
		}
		System.out.println("xe");*/
		if(!positive.isEmpty()) {
			x=String.join("+",new Iterable<String>() {
				@Override
				public Iterator<String> iterator() {
					return positive.stream().map(n->n.toString()).iterator();
				}});
		}else if(!negative.isEmpty())
			x="0";
		if(!negative.isEmpty()) {
			x+="-";
			x+=String.join("-",new Iterable<String>() {
				@Override
				public Iterator<String> iterator() {
					return negative.stream().map(n->n.toString()).iterator();
				}});
		}
		return x;
	}
	double primaries=0;
	@Override
	public Node simplify() {
		
		primaries=0;
		positive.replaceAll(n->n.simplify());
		negative.replaceAll(n->n.simplify());
		//System.out.println("f:"+this.toString());
		List<Node> pcopy=new ArrayList<>(positive);
		for(Node n:pcopy) {//combine 
			if(n instanceof ExprNode) {
				positive.remove(n);
				positive.addAll(((ExprNode) n).positive);
				negative.addAll(((ExprNode) n).negative);
			}
		}
		
		
		List<Node> ncopy=new ArrayList<>(negative);
		for(Node n:ncopy) {
			if(n instanceof ExprNode) {
				negative.remove(n);
				positive.addAll(((ExprNode) n).negative);
				negative.addAll(((ExprNode) n).positive);
			}
		}
		positive.removeIf(s->{//calc all primaries
			if(s.isPrimary()) {
				primaries+=s.eval(null);
				return true;
			}
			return false;
		});

		negative.removeIf(s->{
			if(s.isPrimary()) {
				primaries-=s.eval(null);
				return true;
			}
			return false;
		});
		/*for(Node t:positive) {
			System.out.println(t.getClass().getSimpleName()+":"+t);
		}
		System.out.println("c:"+this.toString());*/
		if(positive.isEmpty()&&negative.isEmpty())
			return new ConstNode(primaries);
		if(primaries!=0)
			positive.add(new ConstNode(primaries));
		/*for(Node t:positive) {
			System.out.println(t.getClass().getSimpleName()+":"+t);
		}
		System.out.println("t:"+this.toString());*/
		return this;
	}
	
}