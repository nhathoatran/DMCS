package sspinja.search;
import sspinja.search.HeuristicSearch;

public class VarState {
	int x;
	int y;

	public VarState(int x , int y) {
		this.x = 0;
		this.y = 0;
		this.x = x;
		this.y = y;
	}

	public int distance(VarState s) {
		return Math.abs(this.x - s.x) + Math.abs(this.y - s.y) ;
	}
	public void print() {
		System.out.print("x = " + x + ", " + "y = " + y) ;
	}

	//these for assertion 
	public int getErrorDistance_0(){
		//assert: (y == 0)
		//error: (y != 0)
		int errorDistance = 0 ;
		errorDistance = (Math.abs(y - 0) == 0 ? 1 : 0 );
		return Math.max(Math.abs(HeuristicSearch.processLoc[0] - 28), errorDistance) ;
	}

	public boolean checkError_0(){
		return (y != 0);
	}

	public String errorState_0(){
		return "(y != 0)";
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

