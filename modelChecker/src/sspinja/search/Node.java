package sspinja.search;

import java.util.ArrayList;

//import spinja.VarState;
import spinja.promela.model.PromelaTransition;
import sspinja.search.VarState;

public class Node {
	public VarState state ;
	public int id ;
	public int parentid ;
	public byte [] nodeState ;
	public int g, h, f ;
	public PromelaTransition trans ;
	public Node parent ;
	public ArrayList<Node> children ;
	
//	public Node(VarState s, Node parent) {
//		state = s ;
//		g = h = f = 0 ;
//		trans = null ;
//		this.parent = parent ;
//		
//		if (parent != null) {
//			parent.addChild(this);
//			f = parent.distance(s) ;
//		}
//		children = null ;
//	}
//	public Node(VarState s, Node parent, PromelaTransition trans) {
//		state = s ;
//		//g = h = f = 0 ;
//		
//		if (parent != null) {
//			this.parent = parent ;
//			parent.addChild(this);
//			//g = parent.g ; //+ 1 ;
//			f = parent.distance(s) ;
//		} else {
//			this.parent = null ;
//			g = 0 ;
//		}
//		this.trans = trans ;
//		children = null ;		
//	}
	public Node(int index, VarState s, byte[] bstate , Node parent, PromelaTransition trans, int distance) {
		state = s ;
		nodeState = bstate ;
		//g = h = f = 0 ;
		
		if (parent != null) {
			this.parent = parent ;
			parentid = parent.id ; 
			this.id = index; 
			parent.addChild(this);
			g = parent.g  + 1 ;			
		} else {
			this.parent = null ;
			this.id = 0 ;
			g = 0 ;
		}
		this.trans = trans ;
		h = distance ; // parent.distance(s) ;
		children = null ;	
		f = g + h ;
//		setDistanceToErrorState(errs) ;
	}
	
	public void reCountDistance() {
		if (children != null)
			for (Node n : children) {			
				n.f = this.distance(n.state) ;
				n.reCountDistance();
			}
	}
	
	
	
	public int distance(VarState n) {
		return this.state.distance(n);
	}
	
	public void setDistanceToErrorState(VarState errs) {
		h = this.state.distance(errs) ;
		f = g + h ;		
	}
	
	public int getDistanceToErrorState() {
		return f ;
	}
	
	
	public void addChild(Node n){
		if (children == null) {
			children = new ArrayList<Node>() ;			
		}
		children.add(n) ;
	}
	public void printChildren(){
		if (children == null) {
//			System.out.println("No children");
		} else {
//			System.out.println("Children:");
			for (Node n : children) {
				n.print();
			}
		}
	}
	public void print() {
		this.state.print();
		if (trans != null)
			System.out.println(", ID: " + id + ", parent ID: " + parentid + ", g = " + this.g + ", h = " + this.h + ", f = " + this.f + ", trans = " + this.trans);		
		else
			System.out.println(", ID: " + id + ", parent ID: " + parentid + ", g = " + this.g + ", h = " + this.h + ", f = " + this.f + ", trans = null");
	}
	public void printAll() {
		this.state.print();		
		printChildren() ;
	}
}
