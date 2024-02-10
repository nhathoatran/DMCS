package sspinja.search ;

import static spinja.search.Message.TRANS_ERROR;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import spinja.exceptions.SpinJaException;
import spinja.promela.model.PromelaTransition;
import spinja.search.Message;
import spinja.util.ByteArrayStorage;
import spinja.util.DummyStdOut;
import sspinja.Run;



public class HeuristicSearch {	
	ArrayList<Node> open = new ArrayList<Node> () ;
	ArrayList<Node> openUp = new ArrayList<Node> () ;
	ArrayList<Node> closed= new ArrayList<Node> () ;
	private final ByteArrayStorage storage = new ByteArrayStorage();
	
	private PanOptimizeModel model ;
	PromelaTransition next, last ;
	
	int assertionLocation, assertionProcessId ;
	public static int [] processLoc = new int[255] ; //process location for assertion
	private int checkOption;

	
	public HeuristicSearch(final PanOptimizeModel model) {
		//checkOption = Run.checkOption ;
		checkOption = 0;
		this.model = model ;
	}
	
	private int getErrorDistance() {
		switch (checkOption) {
			case 0:  //distance to error state, need to know error state (A* and greedy)
				System.out.println("model.getErrorDistance");
				return model.getErrorDistance() ;
				
			case 1: //dead lock : number transitions
				System.out.println("model.countNumTrans");
				return model.countNumTrans() ;
				
			case 2: //assertion 
				System.out.println("model.getAssertionDistance");
				return model.getVarState().getAssertionDistance() ;
				
			case 3: //useless transition
				System.out.println("getUselessDistance");
				VarState vstate = model.getVarState() ;
				int ulDis = getUselessDistance() ;
				if (vstate != null)
					return ulDis + model.getVarState().getAssertionDistance() ;
				else
					return ulDis ;
		}			
		return 0 ;
	}
	
	public int getUselessDistance() {
		int bonusWeight = 0 ;
		if (next != null) {
			bonusWeight = next.uselessWeight ;
		}
		return bonusWeight ;
	}
	
	public void execute(){
		switch (checkOption) {
			case 0:  
				System.out.println("0 : distance to error state, need to know error state (A* and greedy)");
				break ;
			case 1:
				System.out.println("1 : dead lock : number transitions");
				break ;
			case 2:  
				System.out.println("2 : assertion distance");
				break ;
			case 3: 
				System.out.println("3 : useless transition");
				break ;
		}
		boolean bfs = false ;
		if (!bfs) {
			System.out.println("Heuristic with BFS");
			executeWithBFS() ;
		} else {
			System.out.println("Heuristic without BFS");
			executeWOBFS() ;
		}	
	}
	
	private void executeWithBFS() {		
		last = null ;
		while ((next = model.nextTransition(last)) != null) {
			HeuristicSearch.processLoc[next.getProcess().getId()] = next.getId() ;
			last = next ;
		}
		
		VarState s = model.getVarState(); 
		int h = getErrorDistance() ;		
		byte [] sysstate = storeModel() ;		
		Node node = new Node(0, s, sysstate, null, null, h);
		
		open.add(node) ;
		
		int max = 5000 ;
		boolean errorfound = false ;
		int step = 0 ;	
				
		while (! open.isEmpty() && !errorfound) {
			Node curr ;
			curr = open.remove(open.size()-1) ;
			int fVal = curr.f;
			openUp.add(curr);
			
			while (!open.isEmpty()){
				curr = open.get(open.size() - 1) ;
				if (curr.f == fVal) {
					openUp.add(0, curr);
					open.remove(curr) ;
				} else 
					break ;
			}
			
			while (!openUp.isEmpty() && !errorfound) {
				curr = openUp.remove(openUp.size() -1 );
			
				restoreModel(curr) ;
//				System.out.println("Current model: " + model);
//				
//				System.out.println("--- Step : " + step ++);
//				if (step == 14)
//					System.out.println("stop");
				closed.add(curr) ;
				
				if (curr.trans != null)
					HeuristicSearch.processLoc[curr.trans.getProcess().getId()] = curr.trans.getId() ;
				
				if (curr.f > max ) {				
					errorfound = true ;
					break ;
				}
				
//				print_queue() ;
				last = null ;
				next = model.nextTransition(last) ;
				if (next == null) {
					if ((open.size() == 0) && (openUp.size() == 0)) {
						System.out.println("Search complete ");
						break ;										
					} else {
						if (model.getNrProcesses() != 0) {
							System.out.println("Dead lock");					
							print_trail(curr) ;				
							break ;
						} else
							continue ;
					}			
				}
				
				while (next != null) {
					try {													
//						System.out.println(model);
//						System.out.println("Take Next = " + next);
						next.take();
					} 
					catch (SpinJaException e) {	
						DummyStdOut.enableStdOut();
						print_trail(curr);
						report(TRANS_ERROR, e.getMessage());
						return ;
					}
					
					h = getErrorDistance();
					if (h + curr.g + 1 > fVal) {
						add_node_to_list(open,curr,next,h);							
					}else {
						add_node_to_list(openUp,curr,next,h);
					}
					
					next.undo();
					last = next ;
					next = model.nextTransition(last) ;				
				}
			}
		}		
		System.out.println("Search complete");
		System.out.println("Number of transtion: " + (open.size() + closed.size() + openUp.size()));
	}	

	private void executeWOBFS() {		
		last = null ;
		while ((next = model.nextTransition(last)) != null) {
			HeuristicSearch.processLoc[next.getProcess().getId()] = next.getId() ;
			last = next ;
		}
		VarState s = model.getVarState(); 
		int h = getErrorDistance() ;		
		byte [] sysstate = storeModel() ;		
		Node node = new Node(0, s, sysstate,null, null, h) ;
		open.add(node) ;
		
		int max = 5000 ;
		boolean errorfound = false ;
		int step = 0 ;	
		
		
		while (! open.isEmpty() && !errorfound) {			
			Node curr = open.remove(open.size()-1) ;
			restoreModel(curr) ; 			
			closed.add(curr) ;		 
			if (curr.trans != null)
				HeuristicSearch.processLoc[curr.trans.getProcess().getId()] = curr.trans.getId() ; 

			if (curr.f > max ) {
				System.out.println("Max reached");
				break ;
			}
			
			print_queue() ;
			last = null ;
			next = model.nextTransition(last) ;
			if (next == null) {
				if (open.size() == 0) {
					System.out.println("Search complete ");
					break ;										
				} else {
					if (model.getNrProcesses() != 0) {
						System.out.println("Dead lock");					
						print_trail(curr) ;				
						break ;
					} else
						continue ;
				}			
			}
			
			
			//add all states from current node
			while (next != null) {
				try {						
					next.take();
				} 
				catch (SpinJaException e) {
					print_trail(curr);
					report(TRANS_ERROR, e.getMessage());	
					System.out.println("Number of transtion: " + (open.size() + closed.size()));
					return ;
				}
				add_node_to_list(open,curr,next,getErrorDistance());					
				next.undo();
				last = next ;
				next = model.nextTransition(last) ;				
			}
		}
		System.out.println("Search complete");
		System.out.println("Number of transtion: " + (open.size() + closed.size()));
	}	

	protected byte[] storeModel() {
		storage.init(model.getSize());
		model.encode(storage);
		return storage.getBuffer();
	}
	
	protected void restoreModel(Node node) { 
		byte [] state = node.nodeState;			
		ByteArrayStorage reader = new ByteArrayStorage();
		reader.setBuffer(state);
		model.decode(reader);	
	}
	
	private void print_trail(Node end) {
		System.out.println("Error trail");
		Node run = end ;
		while (run != null) {
			run.print();
			run = run.parent ;
		}
	}
	private void print_queue() {
		System.out.println("------------------");
		System.out.println("-- Queue open:");
		for (Node n : open)
			n.print();
		System.out.println("-- Queue openNode:");
		for (Node n : openUp)
			n.print();
		System.out.println("------------------");
	}
	
	private int add_node_to_list(ArrayList<Node> listNode, Node curr, PromelaTransition next, int errDistance) {
		byte [] sysstate = storeModel() ;
		VarState s = model.getVarState() ;
				
		boolean existclosed = false ;
		Node n ;
		
		int id = 0 ; //index in closed
		while (!existclosed && id< closed.size()) {
			n = closed.get(id) ;
			if (Arrays.equals(n.nodeState, sysstate))
				existclosed = true ;
			else
				id ++ ;
		}
		
		if (!existclosed) { //not exist in closed			
			if (listNode == open) {
				//remove node from openUp
				id = 0 ;
				while (id < openUp.size()) {
					n = openUp.get(id) ;
					if (Arrays.equals(n.nodeState, sysstate)) {
						openUp.remove(id) ;
						break ;
					} else
						id ++ ;
				}
			}
			
			int numChild = 0 ;
			if (curr.children != null) 
				numChild = curr.children.size() ;
			
			Node node = new Node(numChild + curr.id, s, sysstate, curr, next, errDistance) ;		
			int inspos = 0 ; //insert position	
			int existpos = -1 ; //check exist in openUp
			int i = 0 ;
			while (i < listNode.size()) {
				n = listNode.get(i) ; //check from first node -> end node
				
				if (n.f > node.f) { //order by f
					System.out.println("Old f: " + n.f + " ; New f: " + node.f);
					inspos ++ ;
				} else {
					if (n.f == node.f) { 
						if (n.g > node.g) { //then oder by g
							System.out.println("Old g: " + n.g + " ; New g: " + node.g);
							inspos ++ ;
						}
					}
				}
					
				
				if (Arrays.equals(n.nodeState,sysstate)) { 
					existpos = i ;					
				}
				i++ ;
			}
			
			
			if (existpos < 0) {				
				listNode.add(inspos, node);
			} else {				
				if (existpos < inspos) {
					if (inspos + 1< listNode.size())
						listNode.add(inspos, node);
					else
						listNode.add(node) ;
					listNode.remove(existpos) ;
				}
			}
		} else {
			//System.out.println("Already visisted -> not add"); //-> reopen ?
		}
		return errDistance ;
	}	
	
	protected final void report(final Message msg, final String text) {
		if (msg.isError()) {		
			System.out.println("sspinja error: " + text );			
		} else if (msg.isWarning()) {
			System.out.println("sspinja warning: " + text );
		}
	}
	
	public void printSummary() {		
	}

	public void printSummary(PrintWriter out) {
		// TODO Auto-generated method stub
		
	}

	public double getBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getNrStates() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
}