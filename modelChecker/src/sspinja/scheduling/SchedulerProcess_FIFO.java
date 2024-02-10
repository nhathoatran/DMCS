package sspinja.scheduling ;
import spinja.util.*;
import spinja.exceptions.ValidationException;
import java.util.ArrayList;

//Automatic generation
public class SchedulerProcess_FIFO  extends SchedulerProcessBase {	
	public int processID ;
	public int refID ;
	
	public int get_processID() {
		return processID ;
	}				
	public void print(){
		Util.println(this.toString()) ;
	}
	
					
	public void inc_time() {}
	public void dec_time() {}
	public void add_time(int time) {}
	public void sub_time(int time) {}
	public int getSize() {
		int size = 0;				
		return 4 + 4 + size ; //processID, refID, process properties				
	}
	
	public SchedulerProcess_FIFO(int processID) {
		//this.processID = (byte) processID ;
		this.processID = processID ;
	}			
	public SchedulerProcess_FIFO() {}
	
	public String getName() {
		return SchedulerObject.getStaticPropertyObject(this.refID).pName + "_" + this.processID; 
	}
					
	public String toString() {
		String res = "    + Process ID=" + this.processID ; 
		res += ", Name: " + SchedulerObject.getStaticPropertyObject(this.refID).pName + ", Ref ID=" + this.refID ;
		return res ;
	}
	
	public void encode(DataWriter _writer) {
		//_writer.writeByte(processID);
		//_writer.writeByte(refID);
		_writer.writeInt(processID);
		_writer.writeInt(refID);
	}		
	public boolean decode(DataReader _reader) {		
		//processID = (byte) _reader.readByte();
		//refID = (byte) _reader.readByte();
		processID = _reader.readInt();
		SchedulerObject.processInScheduler[processID] = true ;
		refID = _reader.readInt();
		return true;
	}
	
	//default value
	public void P1() {
	}					
	//init process with string parameter
	public void P1(String paraList) throws ValidationException {
		String[] parameters = paraList.split(",");							
		ArrayList<String> para = new ArrayList<String>() ;
		for (int i = 0; i < parameters.length; i++)
			if (! parameters[i].trim().isEmpty())
				para.add(parameters[i]) ;
		if (para.size() == 0) {
			P1() ;							
		} else {
			//check number of parameters
			if (para.size() == 0) 								
				P1() ;
			else //raise exception
				throw new ValidationException("Error init the process for scheduling: P1(" + paraList + ")") ;
		}
	}
	//default value
	public void P2() {
	}					
	//init process with string parameter
	public void P2(String paraList) throws ValidationException {
		String[] parameters = paraList.split(",");							
		ArrayList<String> para = new ArrayList<String>() ;
		for (int i = 0; i < parameters.length; i++)
			if (! parameters[i].trim().isEmpty())
				para.add(parameters[i]) ;
		if (para.size() == 0) {
			P2() ;							
		} else {
			//check number of parameters
			if (para.size() == 0) 								
				P2() ;
			else //raise exception
				throw new ValidationException("Error init the process for scheduling: P2(" + paraList + ")") ;
		}
	}
									
	public void initProcess(String pName, ArrayList<String> para){
		//call only with execute function				
		switch (pName) {
			case "P1" : 
				getRefID(pName) ;
				if (para == null) {
					P1() ;								
				} else {
					if (para.isEmpty()) {
						P1() ;
					} else {	
						//check number of parameters
						if (para.size() == 0) 								
							P1() ;
						else
							System.out.println("Error init the process for scheduling") ;
					}
				}
				break ;
			case "P2" : 
				getRefID(pName) ;
				if (para == null) {
					P2() ;								
				} else {
					if (para.isEmpty()) {
						P2() ;
					} else {	
						//check number of parameters
						if (para.size() == 0) 								
							P2() ;
						else
							System.out.println("Error init the process for scheduling") ;
					}
				}
				break ;
			default : break ;
		}
	}
	public void getRefID(String pName){
		if (processList.size() == 0) initProcessList() ;
		this.refID = processList.indexOf(pName);
	}			
	protected void initProcessList() {
		if (!processList.contains("P1"))
			processList.add("P1") ;
		if (!processList.contains("P2"))
			processList.add("P2") ;
	}		
}		
