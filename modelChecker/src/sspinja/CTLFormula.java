package sspinja;

//Model compiler
public class CTLFormula {
	//EF<=5 (alarm == 1)  
	public int length = 2;
	public String opcode[] = new String[2];
	public byte sf[][] = new byte[2][2]; //formula parameters
	public byte sn[] = new byte[2]; //formula superscripts

	public CTLFormula () {
		//EF<=5 (alarm == 1)  

		opcode[0]="EF"; //EF<=5 (alarm == 1)  
		sn[0]= 5; //superscript
		sf[0][0]=1;

		opcode[1]="atomic"; //(alarm == 1) 
		sn[1]= -1; //no superscript
		sf[1][0]=1;
	}

	public String toString () {
		return "EF<=5 (alarm == 1)  ";
	}

}

