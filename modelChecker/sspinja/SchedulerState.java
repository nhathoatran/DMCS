package sspinja.scheduling;

import java.util.ArrayList;
import spinja.model.Transition;
import sspinja.GenerateCode;

public class SchedulerState { ;
	public boolean duplicate = false;
	public int identifier;
	public boolean results[];
	public boolean checked[];
	public ArrayList<SchedulerState> next = new ArrayList<SchedulerState>();
	public ArrayList<Transition> trans = new ArrayList<Transition>();

	public void update(int depth, boolean isCycle, boolean isCurrentState) {}

	public void print(ArrayList<SchedulerState> schStateList, String pref, boolean fulltrans) {
		if (!schStateList.contains(this)) schStateList.add(this) ;
		System.out.print(pref + "[" + identifier + "," + results[0]+ "," + results[1]+ "]") ;
		for (SchedulerState child : next) System.out.print("," + child.identifier);
		System.out.println("");
		int index = 0 ;
		for (SchedulerState s : next) {
			if (!schStateList.contains(s)) {
				if (fulltrans) {
					s.print(schStateList, pref + trans.get(index++) + "-->", fulltrans);
				} else {
					s.print(schStateList, pref + "-->", fulltrans);
				}
			}
		}
	}
	public void print() {
		System.out.println("[" + identifier + "," + results[0]+ "," + results[1]+ "]") ;
	}
}
