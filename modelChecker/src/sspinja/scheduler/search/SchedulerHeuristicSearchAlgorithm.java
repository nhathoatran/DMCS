// Copyright 2010, University of Twente, Formal Methods and Tools group
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package sspinja.scheduler.search;

import static spinja.search.Message.DEADLOCK;
import static spinja.search.Message.DUPLICATE_STATE;
import static spinja.search.Message.EXCEED_DEPTH_ERROR;
import static spinja.search.Message.EXCEED_DEPTH_WARNING;
import static spinja.search.Message.LIVELOCK;
import static spinja.search.Message.TRANS_ERROR;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

import spinja.exceptions.SpinJaException;
import spinja.exceptions.ValidationException;
import spinja.model.Condition;
import spinja.model.Model;
import spinja.model.SchedulerTransition;
import spinja.model.Transition;
import spinja.options.NumberOption;
import spinja.promela.model.PromelaModel;
import spinja.promela.model.PromelaProcess;
import spinja.promela.model.PromelaTransition;
import spinja.search.SearchAlgorithm;
import spinja.search.SearchableStack;
import spinja.search.TransitionCalculator;
import spinja.store.ProbingHashTable;
import spinja.store.StateStore;
import spinja.store.hash.HashAlgorithm;
import spinja.util.ByteArrayStorage;
import spinja.util.DummyStdOut;
import spinja.util.Util;
import sspinja.Config;
import sspinja.Utility;
import sspinja.scheduler.promela.model.SchedulerNeverClaimPromelaModel;
import sspinja.scheduler.promela.model.SchedulerNeverClaimSporadicModel;
import sspinja.scheduler.promela.model.SchedulerPromelaModel;
import sspinja.scheduler.promela.model.SchedulerSporadicModel;

import sspinja.scheduling.optimize.SchedulerPanOptimizeModel;
import sspinja.search.HeuristicSearch;
import sspinja.search.PanOptimizeModel;
import sspinja.scheduling.optimize.VarState;
import sspinja.scheduling.SchedulerPanModel;
import sspinja.scheduling.optimize.Node;
import sspinja.verifier.Verifier;

/**
 * Scheduler Search is a search algorithm based on scheduler guide 
 */

public abstract class SchedulerHeuristicSearchAlgorithm<M extends Model<T>, T extends Transition> extends SearchAlgorithm<M, T> {
	private static final long serialVersionUID = -9191078596196568047L;
		
	protected final ByteArrayStorage storage = new ByteArrayStorage();
	protected final ByteArrayStorage runningSetStorage = new ByteArrayStorage();
	
	protected final SchedulerStack stack;
//    public static int identifier ;
    protected boolean printedDepthWarning;
    protected int running_processID ;
    public byte modelType = 0 ;
	
	byte[] runningSetState ;			
	final StateStore runningSetStore;
	int runningSetIdentifier;	

	protected HashAlgorithm hash;

	int stateCount = 0 ;
	int stateMatch = 0 ;

    private final NumberOption hashEntries = new NumberOption('w',
    		"sets the number of entries in the hash table to 2^N", 21, 3, 31);
   
	@SuppressWarnings("unchecked")
	public SchedulerHeuristicSearchAlgorithm(final M model, final StateStore store, final int stackSize,
		final boolean errorExceedDepth, final boolean checkForDeadlocks, final int maxErrors,
		final TransitionCalculator<M, T> nextTransition) {
		
		super(model, store, checkForDeadlocks, maxErrors, errorExceedDepth, nextTransition);		
		this.stack = new SchedulerStack(stackSize);
		runningSetStore = new ProbingHashTable(hashEntries.getValue());
				
		if (model instanceof SchedulerNeverClaimSporadicModel) {
			modelType = 3 ;
		} else {
			if (model instanceof SchedulerNeverClaimPromelaModel) {
				modelType = 2 ;
			} else {
				if (model instanceof SchedulerSporadicModel) {
					modelType = 1 ;
				}
			}
		}		
		hash = HashAlgorithm.getDefaultAlgorithm();
	}
		
	
	protected void restoreRunningSet() {
		byte [] runningSetState = stack.getRunningSetTop();
		if (runningSetState != null) {
			ByteArrayStorage reader = new ByteArrayStorage();
			reader.setBuffer(runningSetState);
			SchedulerPromelaModel.scheduler.decodeRunningSet(reader) ;
		}
	}
	
	protected void restoreModel() {
		byte [] state = stack.getTop();			
		ByteArrayStorage reader = new ByteArrayStorage();
		reader.setBuffer(state);
		model.decode(reader);
	}
	
	protected byte[] storeRunningSet() {		
		runningSetStorage.init(SchedulerPromelaModel.scheduler.getRunningSetSize());
		//SchedulerPromelaModel.scheduler._runningSet.encode(runningSetStorage);
		SchedulerPromelaModel.scheduler.encodeRunningSet(runningSetStorage);
		return runningSetStorage.getBuffer();
	}
	
		
	protected void addRunningSetState(){
		runningSetState = storeRunningSet() ;
		runningSetIdentifier = runningSetStore.addState(runningSetState);
		stack.pushRunningSet(runningSetState, runningSetIdentifier) ;		
	}
	
	
	@Override
	protected boolean checkModelState() {
		final byte[] buffer = storeModel();
		return Arrays.equals(buffer, stack.getTop());
	}

	/**
	 * @see spinja.search.Algoritm#freeMemory()
	 */
	@Override
	public void freeMemory() {
		stack.clearStack();		
	}
		

	@Override
	protected boolean addState(final byte[] state, int identifier) {
		return stack.push(state, identifier);
	}
			
	protected void replaceState(final byte[] state, int identifier) {
		stack.replaceLastState(state, identifier);		
	}
	
	public void printStack(){
		Util.print("+ System stack");
		stack.print();		
	}

	protected void printBytes(byte[] state) {
		for (int i=0 ;i < state.length;i++)
			System.out.print(Byte.toString(state[i]) + ' ');
		Util.print("");
	}
	
	public int getStateID() {
		byte[] state  = storeModel();				
		int identifier ;
		identifier = hash.hash(state, 0);
		return identifier ;
	}
	
	private void saveSchedulerTransitionToStack(){
		//save the trace to stack
		SchedulerTransition schTrans = new SchedulerTransition(1); //select process
		stack.pushSchTrans(schTrans);
	}
	
	private void storeModelandStack() {
		byte[] state  = storeModel();	//encode of the model	
		int identifier;
		if (model.conditionHolds(Condition.SHOULD_STORE_STATE)) {
			identifier = store.addState(state);
		} else {
			identifier = hash.hash(state, 0);			
			atomicSteps++;
		}
				
		if (!addState(state, identifier)) { //add to stack
			throw new RuntimeException("Could not even add the state.");
		}
		addRunningSetState();	//add to stack	
	}
	
	@Override
	public void execute1() {
		Util.println("Begin execute");
		Util.println(SchedulerPromelaModel.scheduler.getSchedulerName());
		Config.processInit = false ;	
		
		
		
		try {
			SchedulerPromelaModel.scheduler.init_order();
		} catch (final SpinJaException ex) {
			report(TRANS_ERROR, ex.getMessage());
		}
		
		if (Verifier.isVerified) { //save first state to stack for the simulation (no running process)
			storeModelandStack();
		}
		
		if (SchedulerPromelaModel.scheduler.running_process == null) {			
			try {
				running_processID = SchedulerPromelaModel.scheduler.select_process(-1) ;
			} catch (final SpinJaException ex) {
				report(TRANS_ERROR, ex.getMessage());
			}
		}
		
		if (Verifier.isVerified) {
			saveSchedulerTransitionToStack(); //save scheduling transition
		}
		
		byte[] state;			
		int identifier ;
		state =  storeModel();	
		if (model.conditionHolds(Condition.SHOULD_STORE_STATE)) {
			identifier = store.addState(state);
		} else {
			identifier = hash.hash(state, 0);			
			atomicSteps++;
		}
				
		if (!addState(state, identifier)) {
			throw new RuntimeException("Could not even add the first state.");
		}
		
				
		addRunningSetState();		
		

		
		Transition last = null ;
		Transition next = null;
		
		int livelockCount = 0 ;		
		boolean startCheckLiveLock = false ;
		
		boolean isTimer ;
			
		while ((nrErrors < maxErrors) && !Thread.currentThread().isInterrupted() && restoreState()) {	
			isTimer = SchedulerPromelaModel.scheduler.isTimer();			
			if (outOfMemory) {			
				throw new OutOfMemoryError();
			}
			
//			stack.print();
			
			assert checkModelState();
			if (state.length > maxSize) {
				maxSize = state.length;
			}
					
			if (getDepth() - 1 > maxDepth) {
				maxDepth = getDepth() - 1;
			}
						
			last = stack.getLastTransition() ;
			if (last instanceof SchedulerTransition){
				last = null; //
			} 

			next = nextTransition(last) ; //find other action
			
			if (next == null) {		
				if (isTimer && last == null) { 
					try {	
						SchedulerPromelaModel.scheduler.clock();						
					} catch (ValidationException e) {								
						continue; //break ;
					}
					if (Config.isCheckLiveLock)	if (!startCheckLiveLock) startCheckLiveLock = true ;
					if (model.conditionHolds(Condition.SHOULD_STORE_STATE)) {							
						state = storeModel() ;
						identifier = store.addState(state);							
						if (identifier < 0) {
							report(DUPLICATE_STATE.withState(state));
							nextTransition.duplicateState((M) model, getLastTransition(), state, -(identifier + 1), getSearchableStack());
							statesMatched++;											
							if (startCheckLiveLock) { 
								if (SchedulerPromelaModel.scheduler.running_process != null) {
									report(LIVELOCK) ;
									livelockCount ++ ;
									if (!(nrErrors < maxErrors)) {
										break ;
									}							
								}
							} 
							stateDone() ;
						} else {					
							if (!addState(state, identifier) ){			
								if (errorExceedDepth) {
									report(EXCEED_DEPTH_ERROR);
								} else { 
									if (!printedDepthWarning) {
										report(EXCEED_DEPTH_WARNING);
										printedDepthWarning = true;
									}
								}								
								undoTransition();							
							}
						}
						continue ;
					}										
				} 				
				
				if (last == null && !isTimer && SchedulerPromelaModel.scheduler.running_process != null) {
					report(DEADLOCK) ; 
				} 
				
				if (getOtherProcessfromRunningSet()) { //select other process to run
					if (model.conditionHolds(Condition.SHOULD_STORE_STATE)) {
						state = storeModel() ;
						identifier = store.addState(state);
						if (identifier < 0) {	
							report(DUPLICATE_STATE.withState(state));
							nextTransition.duplicateState((M) model, getLastTransition(), state, -(identifier + 1), getSearchableStack());
							statesMatched++;
							stateDone() ;
						} else {
							replaceState(state, identifier) ;
							continue;
						}
					}
				} else {
					
					stateDone() ;
					continue ;
				}	
			} else  {
				try {
					takeTransition(next);
					SchedulerPromelaModel.scheduler._runningSet.dataSet.clear();
					startCheckLiveLock = false ;
				} catch (SpinJaException e) {					
					e.printStackTrace();
//					print_trace();
					outputErrorTrace(SchedulerPromelaModel.scheduler.getSchedulerName()); //to trail file
					report(TRANS_ERROR, e.getMessage());	
					continue ;
				}	
				
				if (isTimer) {
					try {
						SchedulerPromelaModel.scheduler.clock();
					} catch (ValidationException e) {
						e.printStackTrace();
						report(TRANS_ERROR, e.getMessage());	
						continue ;//break ;
					}
				} else {				
					if (SchedulerPromelaModel.scheduler.running_process == null) {
						try {
							if (Verifier.isVerified) { //save first state to stack for the simulation (no running process)
								storeModelandStack();
							}
							running_processID = SchedulerPromelaModel.scheduler.select_process(-1) ;
							if (Verifier.isVerified) {
								saveSchedulerTransitionToStack(); //save scheduling transition
							}
						} catch (ValidationException e) {
							e.printStackTrace();
						}
					}
				}
				
				if (model.conditionHolds(Condition.SHOULD_STORE_STATE)) {
					state = storeModel() ;
					identifier = store.addState(state);
					if (identifier < 0) {				
						nextTransition.duplicateState((M) model, getLastTransition(), state, -(identifier + 1), getSearchableStack());
						statesMatched++;
						if (Config.isCheckAcceptionCycle) { 				
							continue;
						}						
						undoTransition();
						continue;
					} else {
						if ((store.getStored() & 0xfffff) == 0) {
						}
					}						
				} else {
					atomicSteps++;
					identifier = hash.hash(state, 0);
				}
									
				if (!addState(state, identifier) ){
					if (errorExceedDepth) {
						report(EXCEED_DEPTH_ERROR);
					} else if (!printedDepthWarning) {
						report(EXCEED_DEPTH_WARNING);
						printedDepthWarning = true;
					}
					undoTransition();
					continue;
				}
				addRunningSetState();
			} //else next == null
		}//end while
		
		freeMemory();		
	} 
	

	
	protected boolean getOtherProcessfromRunningSet() {
		if (SchedulerPromelaModel.scheduler._runningSet == null)
			return false ;
		if (SchedulerPromelaModel.scheduler._runningSet.size() <= 1) //true
			return false ;
		else {						
			try {
				if (SchedulerPromelaModel.scheduler.running_process != null) {
					running_processID = SchedulerPromelaModel.scheduler.running_process.processID ;
					running_processID = SchedulerPromelaModel.scheduler.select_process(running_processID) ;
				} else return false ;
			} catch (final SpinJaException e) {			
				e.printStackTrace();
				report(TRANS_ERROR, e.getMessage());
			}			
			//no other process
			if (running_processID < 0)
				return false ;
		    else 
		    	return true ;
		}
	}
	
	public void printInfo() {		
		final double memory = getBytes() / (1024.0 * 1024.0);
		Util.println("----------------------------------------------------");				
		Util.println("System state: " +  store.getStored());
		Util.println("System state match: " + statesMatched);
		Util.println("System state memory: " + (double) store.getBytes()/(1024 * 1024));
		Util.println("----------------------------------------------------");		
	}

	/**
	 * @see spinja.search.SearchAlgorithm#getBytes()
	 */
	@Override
	public long getBytes() {
		return 26 + store.getBytes() ; 
	}
	
	

	@Override
	public int getDepth() {
		return stack.getSize();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T getLastTransition() {
		return (T)stack.getLastTransition();
	}

	
	@Override
	public SearchableStack getSearchableStack() {
		return stack;
	}

	/**
	 * This function prints the trace to a .trail file so that it can be rerun.
	 * 
	 * @param name
	 *            The name of the file (without extension)
	 */
	@Override
	protected void outputTrace(final String name) {
//		try {
//			final PrintWriter out = new PrintWriter(
//				new BufferedWriter(new FileWriter(name + ".trail")));
//			for (int i = 0; i < stack.getSize(); i++) {
//				if (stack.getTransition(i) != null) {
//					out.println(stack.getTransition(i).getId());
//				} else {
//					break;
//				}
//			}
//			out.flush();
//			out.close();
//			Util.println("sspinja: wrote " + name + "_Error.trail\n");
//		} catch (final IOException ex) {
//			Util.println("sspinja: error while writing " + name + "_Error.trail: " + ex.getMessage());
//		}
		
	}
	
	public void outputErrorTrace(final String name) {
		try {
			final PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(name + "_Error.trail")));
			for (int i = 0; i < stack.getSize(); i++) {
				if (stack.getTransition(i) != null) {
					out.println(stack.identifiers[i] + "->" + stack.lastTransition[i].getId() + "->" + stack.lastTransition[i].toString());
				} else {
					break;
				}
			}
			out.flush();
			out.close();
			Util.println("sspinja: wrote " + name + "_Error.trail\n");
		} catch (final IOException ex) {
			Util.println("sspinja: error while writing " + name + "_Error.trail: " + ex.getMessage());
		}
		
	}
	
	

	@Override
	protected boolean restoreState() {
		return stack.getSize() > 0;
	}

	@Override
	protected void stateDone() {	
		stack.pop() ;
		if (stack.getSize() > 0) {
			restoreModel();
			restoreRunningSet();
		}
	}


	@Override
	protected void undoTransition() {
		Transition lastTrans = stack.getLastTransition() ;
		if (lastTrans != null) {
			String trans = lastTrans.toString() ;
			if (lastTrans instanceof PromelaTransition) {
				PromelaProcess proc = ((PromelaTransition) lastTrans).getProcess() ;
				if (proc == null && trans.contains("-end-")) { //claim
					restoreModel();
					restoreRunningSet();
				}else {
					String procName = proc.getName() ;
					if (procName != null) {
						if (procName.contains("sporadic") ) {					
							if (trans.contains("skip")) {
								stateDone() ;
							} else {
								restoreModel();
								restoreRunningSet();
							}
						} else {
							restoreModel();
							restoreRunningSet();
						}
					} else {
						if (trans.contains("-end-")) {
							stateDone();
						} else {							
							restoreModel();
							restoreRunningSet();
						}
					}
				}
			} else { //instance of SchedulerTranstion
				restoreModel();
				restoreRunningSet();				
			}
		} else {
			restoreModel();
			restoreRunningSet();
			stateDone();
		}		
	}

		
	@Override
	protected void print_trace() {
		stack.print();		
	}


	protected abstract Transition nextTransition(Transition last) ;
	
	
	/*
	 * new code
	 */
	ArrayList<Node> open = new ArrayList<Node> () ;
	ArrayList<Node> openUp = new ArrayList<Node> () ;
	ArrayList<Node> closed= new ArrayList<Node> () ;
//	private SchedulerPanOptimizeModel model ;
	PromelaTransition next, last ;
	
	int assertionLocation, assertionProcessId ;
	public static int [] processLoc = new int[255] ; //process location for assertion
	private  int checkOption = 0;

	
//	public void initSchedulerOptimizeSearchAlgorithm(M model){
//		checkOption = 0;
//		model = (SchedulerPanOptimizeModel) model ;
//	}
	
	private int getErrorDistance() {
		checkOption = 0;
		switch (checkOption) {
			case 0:  //distance to error state, need to know error state (A* and greedy)
				System.out.println("model.getErrorDistance");
				return  ((SchedulerPanOptimizeModel) model).getErrorDistance() ;
				
			case 1: //dead lock : number transitions
				System.out.println("model.countNumTrans");
				return ((SchedulerPanOptimizeModel) model).countNumTrans() ;
				
			case 2: //assertion 
				System.out.println("model.getAssertionDistance");
				return ((SchedulerPanOptimizeModel) model).getVarState().getAssertionDistance() ;
				
			case 3: //useless transition
				System.out.println("getUselessDistance");
				VarState vstate = ((SchedulerPanOptimizeModel) model).getVarState() ;
				int ulDis = getUselessDistance() ;
				if (vstate != null)
					return ulDis + ((SchedulerPanOptimizeModel)model).getVarState().getAssertionDistance() ;
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
		Util.println("Begin execute");
		Util.println(SchedulerPromelaModel.scheduler.getSchedulerName());
		Config.processInit = false ;
		stateCount++;
		try {
			SchedulerPromelaModel.scheduler.init_order();
			
		} catch (final SpinJaException ex) {
			report(TRANS_ERROR, ex.getMessage());
		}
		
//		if (Verifier.isVerified) { //save first state to stack for the simulation (no running process)
//			storeModelandStack();
//		}
		
		if (SchedulerPromelaModel.scheduler.running_process == null) {			
			try {
				running_processID = SchedulerPromelaModel.scheduler.select_process(-1) ;
			} catch (final SpinJaException ex) {
				report(TRANS_ERROR, ex.getMessage());
			}
//			SchedulerPromelaModel.scheduler.print_all();
		}
		if (running_processID != -1){ //select process to run
			runningSetState = storeRunningSet() ; //save RunningSet
		}
		
		last = null ;
		while ((next = ((SchedulerPanOptimizeModel) model).nextTransition(last)) != null) {
			HeuristicSearch.processLoc[next.getProcess().getId()] = next.getId() ;
			last = next ;
		}
		Util.println("Begin execute");
//		Util.println(SchedulerPromelaModel.scheduler.getSchedulerName());
		Config.processInit = false ;	
				

		Transition last1 = null ;
		Transition next1 = null;
//		System.out.println("------- First model: " + model);
//		VarState s = ((SchedulerPanModel) model).getVarState(); 
		VarState s = ((SchedulerPanOptimizeModel) model).getVarState(); 
		int h = getErrorDistance() ;		
		byte [] sysstate = storeModel() ;		
		Node node = new Node(0, s, sysstate, null, next1, h);
		open.add(node) ;
		stateCount++;
		
		int max = 5000 ;
		boolean errorfound = false ;
		int step = 0 ;	
		Node curr ;
		int fVal;
		
		
		boolean isTimer = SchedulerPromelaModel.scheduler.isTimer();;
		
		
		while (! open.isEmpty() && !errorfound) {			
			curr = open.remove(open.size()-1) ;
			fVal = curr.f;
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
//				System.out.println("--- Step : " + step ++);
//				SchedulerPromelaModel.scheduler.print_all();
//				if (step == 14)
//					System.out.println("stop");
				closed.add(curr) ;
				
				if (curr.trans != null)
					HeuristicSearch.processLoc[((PromelaTransition) curr.trans).getProcess().getId()] = curr.trans.getId() ;
				
								
				if (curr.f > max ) {				
					errorfound = true ;
					break ;
				}
				
//				print_queue() ;
				last1 = null ;
				//next = ((SchedulerPanOptimizeModel) model).nextTransition(last) ; loi nhan model cu
				next1 = (PromelaTransition) nextTransition(last1) ;
				if (next1 == null) {
					if (((SchedulerPanOptimizeModel) model).getNrProcesses() != 0) {
						if (SchedulerPromelaModel.scheduler.running_process != null) {
							System.out.println("Dead lock");	
							errorfound = true;
							print_trail(curr) ;				
							break ;
						} else {
							try {
								running_processID = SchedulerPromelaModel.scheduler.select_process(-1);
							} catch (ValidationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (running_processID != -1){ //select process to run
								runningSetState = storeRunningSet() ; //save RunningSet 
//								s = ((SchedulerPanModel) model).getVarState(); 
//								h = getErrorDistance() ;		
//								sysstate = storeModel() ;		
//								node = new Node(0, s, sysstate,null, null, h) ;
//								openUp.add(node) ;
								
								h = getErrorDistance();
								System.out.println("Error distance: " + h);
								if (h + curr.g + 1 > fVal) {
									add_node_to_list(open,curr,(PromelaTransition) next1,h);
									stateCount++;
								}else {
									add_node_to_list(openUp,curr,(PromelaTransition) next1,h);
									stateCount++;
								}
							}
//							System.out.println("Select process:");
//							SchedulerPromelaModel.scheduler.print_all();
//							System.out.println("------- model: " + model);
							continue;
						}
					} 
					if ((open.size() == 0) && (openUp.size() == 0)) {
						System.out.println("Search complete ");
						break ;										
					} 			
				}
				
				//take all actions add to list
				while (next1 != null && !errorfound) {
					try {													
//						System.out.println("");
//						System.out.println("Check action >>>>>>>>> Take = " + next1);						
						takeTransition(next1);												
					} 
					catch (SpinJaException e) {	
						DummyStdOut.enableStdOut();
						print_trail(curr);
						report(TRANS_ERROR, e.getMessage());
						return ;
					}
					SchedulerPromelaModel.scheduler._runningSet.dataSet.clear();
					if (((SchedulerPanOptimizeModel)model).checkError()) {
						errorfound = true;
						System.out.println("*************************************");
						System.out.println("Error " + ((SchedulerPanModel) model).errorState());
						print_trail(curr, next1);
						System.out.println("*************************************");
					} else {
						h = getErrorDistance();
						if (h + curr.g + 1 > fVal) {
							add_node_to_list(open,curr,(PromelaTransition) next1,h);
							stateCount++;
						}else {
							add_node_to_list(openUp,curr,(PromelaTransition) next1,h);
							stateCount++;
						}
						
						if (next1 != null) {
							next1.undo();
//							System.out.println("Undo action >>>>>>>>> " + next1);
						}
						
						last1 = next1 ;
						next1 = nextTransition(last1) ;		
					}
				}
				
				//restore RunningSet
				if (runningSetState != null) {
					ByteArrayStorage reader = new ByteArrayStorage();
					reader.setBuffer(runningSetState);
					SchedulerPromelaModel.scheduler.decodeRunningSet(reader) ;				
					if(getOtherProcessfromRunningSet()){
						System.out.println("getOtherProcessfromRunningSet");
						while ((next1 = ((SchedulerPanModel) model).nextTransition((PromelaTransition) last1)) != null) {
							HeuristicSearch.processLoc[next.getProcess().getId()] = next1.getId() ;
							last1 = next1 ;
						}
						
						s = ((SchedulerPanOptimizeModel) model).getVarState(); 
						h = getErrorDistance() ;		
						sysstate = storeModel() ;		
						node = new Node(0, s, sysstate, null, next1, h);
						open.add(node) ;	
						stateCount++;
					}
				}
			}
		}		
		System.out.println("Search complete");
		System.out.println("Number of transtion: " + (open.size() + closed.size() + openUp.size()));
		System.out.println("State count: " + stateCount);
	}
	
	private void executeWOBFS() {		
		last = null ;
		while (next = nextTransition(last) != null) {
			HeuristicSearch.processLoc[next.getProcess().getId()] = next.getId() ;
			last = next ;
		}
		VarState s = ((SchedulerPanOptimizeModel) model).getVarState(); 
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
			next = ((SchedulerPanOptimizeModel) model).nextTransition(last) ;
			if (next == null) {
				if (open.size() == 0) {
					System.out.println("Search complete ");
					break ;										
				} else {
					if (((SchedulerPanOptimizeModel) model).getNrProcesses() != 0) {
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
				next = ((SchedulerPanOptimizeModel) model).nextTransition(last) ;				
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
		Node run = end ;
		while (run != null) {
			run.print();
			run = run.parent ;
		}
	}
	private void print_trail(Node end, Transition trans) {
		System.out.println("Error trail");
		System.out.println("Take " + trans);
		print_trail(end);
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
	
	private int add_node_to_list(ArrayList<Node> listNode, Node curr, PromelaTransition next2, int errDistance) {
		byte [] sysstate = storeModel() ;
		VarState s = ((SchedulerPanOptimizeModel) model).getVarState() ;
				
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
			
			Node node = new Node(numChild + curr.id, s, sysstate, curr, next2, errDistance) ;		
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
	
}


