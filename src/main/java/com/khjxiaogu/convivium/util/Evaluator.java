package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.world.level.lighting.BlockLightEngine;

public class Evaluator{
		public interface Node{
			double eval(Map<String,Double> env);
			boolean isPrimary();
			Node simplify();
		}
		static abstract class BiNode implements Node{
			Node left;
			Node right;
			public BiNode(Node left, Node right) {
				super();
				this.left = left;
				this.right = right;
			}
			
		}
		static class BiCalcNode extends BiNode{
			BiFunction<Double,Double,Double> calc;
			public static BiFunction<Double,Double,Double> add=(v1,v2)->v1+v2;
			public static BiFunction<Double,Double,Double> min=(v1,v2)->v1-v2;
			public static BiFunction<Double,Double,Double> mul=(v1,v2)->v1*v2;
			public static BiFunction<Double,Double,Double> div=(v1,v2)->v1/v2;
			public static BiFunction<Double,Double,Double> pow=(v1,v2)->Math.pow(v1,v2);
			public static BiFunction<Double,Double,Double> mod=(v1,v2)->v1%v2;
			public BiCalcNode(Node left, Node right,BiFunction<Double,Double,Double> calc) {
				super(left, right);
				this.calc=calc;
			}

			@Override
			public double eval(Map<String, Double> env) {
				return calc.apply(left.eval(env),right.eval(env));
			}

			@Override
			public boolean isPrimary() {
				return false;
			}

			@Override
			public String toString() {
				String cn=calc==add?"+":(calc==min?"-":(calc==mul?"*":(calc==div?"/":(calc==pow?"^":(calc==mod?"%":calc+"")))));
				return left+cn+right;
			}

			@Override
			public Node simplify() {
				left=left.simplify();
				right=right.simplify();
				if(left.isPrimary()&&right.isPrimary())
					return new ConstNode(eval(null));
				else if(calc==add||calc==min) {
					return new ExprNode(calc==add,left,right).simplify();
				}else if(calc==mul||calc==div) {
					return new TermNode(calc==mul,left,right).simplify();
				}
				return this;
			}
			
		}
		static class ExprNode implements Node{
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
		static class TermNode implements Node{
			List<Node> positive=new ArrayList<>();
			List<Node> negative=new ArrayList<>();
			
			public TermNode(boolean type,Node pos,Node pos2) {
				if(type) {
					positive.add(pos);
					positive.add(pos2);
				}else {
					positive.add(pos);
					negative.add(pos2);
				}
			}

			public TermNode() {
				super();
			}

			@Override
			public double eval(Map<String, Double> env) {
				double rslt=1;
				for(Node n:positive)
					rslt*=n.eval(env);
				for(Node n:negative)
					rslt/=n.eval(env);
				return rslt;
			}

			@Override
			public boolean isPrimary() {
				return false;
			}

			@Override
			public String toString() {
				String x="";
				/*System.out.println("st");
				for(Node n:positive) {
					System.out.println(n.getClass().getSimpleName()+":"+n);
				}
				System.out.println("te");*/
				if(!positive.isEmpty()) {
					x=String.join("*",new Iterable<String>() {
						@Override
						public Iterator<String> iterator() {
							return positive.stream().map(n->"("+n+")").iterator();
						}});
				}else if(!negative.isEmpty())
					x="1";
				if(!negative.isEmpty()) {
					x+="/";
					x+=String.join("/",new Iterable<String>() {
						@Override
						public Iterator<String> iterator() {
							return negative.stream().map(n->"("+n+")").iterator();
						}});
				}
				return x;
			}
			double primaries=1;
			@Override
			public Node simplify() {
				primaries=1;
				positive.replaceAll(n->n.simplify());
				List<Node> pcopy=new ArrayList<>(positive);
				for(Node n:pcopy) {//combine 
					if(n instanceof TermNode) {
						positive.remove(n);
						positive.addAll(((TermNode) n).positive);
						negative.addAll(((TermNode) n).negative);
					}
				}
				negative.replaceAll(n->n.simplify());
				List<Node> ncopy=new ArrayList<>(negative);
				for(Node n:ncopy) {
					if(n instanceof TermNode) {
						negative.remove(n);
						positive.addAll(((TermNode) n).negative);
						negative.addAll(((TermNode) n).positive);
					}
				}
				List<Node> primaryExprp=new ArrayList<>();
				positive.removeIf(s->{//calc all primaries
					if(s.isPrimary()) {
						primaries*=s.eval(null);
						return true;
					}else if(!(s instanceof ExprNode)) {
						primaryExprp.add(s);
						return true;
					}
					return false;
				});

				negative.removeIf(s->{
					if(s.isPrimary()) {
						primaries/=s.eval(null);
						return true;
					}
					return false;
				});
				if(positive.isEmpty()) {
					if(primaryExprp.isEmpty())
						return new ConstNode(primaries);
					positive.addAll(primaryExprp);
					if(primaries!=1)
						positive.add(new ConstNode(primaries));
					if(positive.isEmpty())
						positive.add(new ConstNode(1));
					if(positive.size()==1&&negative.isEmpty())
						return positive.get(0);
					//System.out.println(this.toString());
					return this;
				}
				positive.addAll(primaryExprp);
				if(primaries!=1)
					positive.add(new ConstNode(primaries));
				/*ExprNode en=(ExprNode)positive.remove(0);
				en.positive.replaceAll(nxx->{
					TermNode tn=new TermNode();
					if(primaries!=1)
						tn.positive.add(new ConstNode(primaries));
					tn.positive.addAll(positive);
					if(positive.isEmpty())
						positive.add(new ConstNode(1));
					tn.negative.addAll(negative);
					tn.positive.add(nxx);
					return tn.simplify();
				});
				en.negative.replaceAll(nxx->{
					TermNode tn=new TermNode();
					if(primaries!=1)
						tn.positive.add(new ConstNode(primaries));
					tn.positive.addAll(positive);
					if(positive.isEmpty())
						positive.add(new ConstNode(1));
					tn.negative.addAll(negative);
					tn.positive.add(nxx);
					return tn.simplify();
				});
				positive.clear();
				positive.add(en);*/
				//positive.replaceAll(n->n.simplify());
				if(positive.size()==1&&negative.isEmpty())
					return positive.get(0);
				/*for(Node t:positive) {
					System.out.println(t.getClass().getSimpleName()+":"+t);
				}
				System.out.println("e:"+this.toString());*/
				return this;
			}
			
		}
		static class CalcNode implements Node{
			Function<Double,Double> calc;
			Node nested;
			String name;
			public CalcNode(Node nested,String name,Function<Double, Double> calc) {
				this.calc = calc;
				this.nested = nested;
				this.name=name;
			}

			@Override
			public double eval(Map<String, Double> env) {
				return calc.apply(nested.eval(env));
			}
			
			@Override
			public boolean isPrimary() {
				return false;
			}
			
			@Override
			public Node simplify() {
				nested=nested.simplify();
				if(nested.isPrimary())
					return new ConstNode(eval(null));
				return this;
			}

			@Override
			public String toString() {
				return name + "(" + nested + ")";
			}
		}
		static class ConstNode implements Node{
			double val;

			@Override
			public String toString() {
				return ""+val;
			}

			public ConstNode(double val) {
				this.val = val;
			}

			@Override
			public double eval(Map<String, Double> env) {
				return val;
			}
			@Override
			public boolean isPrimary() {
				return true;
			}

			@Override
			public Node simplify() {
				return this;
			}
		}
		static class VarNode implements Node{
			String token;

			public VarNode(String token) {
				this.token = token;
			}

			@Override
			public double eval(Map<String, Double> env) {
				return env.getOrDefault(token,0.0);
			}
			@Override
			public String toString() {
				return  token ;
			}

			@Override
			public boolean isPrimary() {
				return false;
			}

			@Override
			public Node simplify() {
				return this;
			}
		}
	    int pos = -1, ch;
	    String str;
	    public Evaluator(String str) {
	    	this.str=str;
	    }
	    void nextChar() {
	        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	    }

	    boolean eat(int charToEat) {
	        while (ch == ' ') nextChar();
	        if (ch == charToEat) {
	            nextChar();
	            return true;
	        }
	        return false;
	    }

	    public Node parse() {
	        nextChar();
	        Node x = parseExpression();
	        System.out.println(pos+str.substring(pos));
	        if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	        return x;
	    }
	    public static Node eval(String exp) {
	    	return new Evaluator(exp).parse();
	    }
	    // Grammar:
	    // expression = term | expression `+` term | expression `-` term
	    // term = factor | term `*` factor | term `/` factor
	    // factor = `+` factor | `-` factor | `(` expression `)`
	    //        | number | functionName factor | factor `^` factor

	    Node parseExpression() {
	    	Node x = parseTerm();
	        for (;;) {
	            if      (eat('+')) x=new BiCalcNode(x,parseTerm(),BiCalcNode.add); // addition
	            else if (eat('-')) x=new BiCalcNode(x,parseTerm(),BiCalcNode.min); // subtraction
	            else return x;
	        }
	    }

	    Node parseTerm() {
	    	Node x = parseFactor();
	        for (;;) {
	            if      (eat('*')) x=new BiCalcNode(x,parseFactor(),BiCalcNode.mul); // multiplication
	            else if (eat('/')) x=new BiCalcNode(x,parseFactor(),BiCalcNode.div); // division
	            else return x;
	        }
	    }

	    Node parseFactor() {
	        if (eat('+')) return parseFactor(); // unary plus
	        if (eat('-')) return new CalcNode(parseFactor(),"-",v->-v); // unary minus
	        Node x = null;
	        int startPos = this.pos;
	        if (eat('(')) { // parentheses
	            x = parseExpression();
	            eat(')');
	        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	            x = new ConstNode(Double.parseDouble(str.substring(startPos, this.pos)));
	        } else if (ch >= 'a' && ch <= 'z') { // functions
	            while (ch >= 'a' && ch <= 'z') nextChar();
	            String func = str.substring(startPos, this.pos);
	            //System.out.println(String.valueOf(Character.toString(str.charAt(pos))));
	            //this.pos--;
	            if(eat('(')) {
	            	//this.pos--;
	            	//this.pos--;
	            	x = parseExpression();
	            	eat(')');
	            	//System.out.println(str.substring(pos));
	            	//System.out.println(eat(')'));
	            	//eat(')');
	            }
	            if(x!=null) {
		            if (func.equals("sqrt")) x = new CalcNode(x,"sqrt",v->Math.sqrt(v));
		            else if (func.equals("sin")) x = new CalcNode(x,"sin",v->Math.sin(Math.toRadians(v)));
		            else if (func.equals("cos")) x = new CalcNode(x,"cos",v->Math.cos(Math.toRadians(v)));
		            else if (func.equals("tan")) x = new CalcNode(x,"tan",v->Math.tan(Math.toRadians(v)));
		            else throw new RuntimeException("Unknown function: " + func);
	            }else {
	            	x=new VarNode(func);
	            }
	        } else {
	            throw new RuntimeException("Unexpected: " + (char)ch);
	        }

	        if (eat('^')) x = new BiCalcNode(x, parseFactor(),BiCalcNode.pow); // exponentiation
	        if (eat('%')) x = new BiCalcNode(x, parseFactor(),BiCalcNode.mod); // modular
	        return x;
	    }
	    public static void main(String[] args) {
	    	System.out.println(eval("(v+1+2+3*5*n)*(v*2*5*8*1)").simplify());
	    }
	}