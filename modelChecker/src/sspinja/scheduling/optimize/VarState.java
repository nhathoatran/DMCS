package sspinja.scheduling.optimize;
import sspinja.search.HeuristicSearch;

public class VarState {
	public int x;

	public VarState(int x) {
		this.x = 0;
		this.x = x;
	}

	public int distance(VarState s) {
		return Math.abs(this.x - s.x) ;
	}
	public void print() {
		System.out.print("x = " + x) ;
	}

	//these for assertion 
	public int getErrorDistance_0(){
		//assert: (x != 2)
		//error: (x == 2)
		int errorDistance = 0 ;
		errorDistance = (Math.abs(x - 2));
		return Math.max(Math.abs(HeuristicSearch.processLoc[1] - 29), errorDistance) ;
	}

	public boolean checkError_0(){
		return (x == 2);
	}

	public String errorState_0(){
		return "(x == 2)";
	}

	public int getAssertionDistance(){
		int result = Integer.MAX_VALUE ;
		result = Math.min(result, getErrorDistance_0());
		return result ;
	}

	public boolean checkError(){
		boolean result = false ;
		result = result || checkError_0();
		return result ;
	}

}

