package sspinja;

//Model compiler
public class CTLFormula {
	//AG<=20 (remaining >= 0)  
	public int length = 2;
	public String opcode[] = new String[2];
	public byte sf[][] = new byte[2][2]; //formula parameters
	public byte sn[] = new byte[2]; //formula superscripts

	public CTLFormula () {
		//AG<=20 (remaining >= 0)  

		opcode[0]="AG"; //AG<=20 (remaining >= 0)  
		sn[0]= 20; //superscript
		sf[0][0]=1;

		opcode[1]="atomic"; //(remaining >= 0) 
		sn[1]= -1; //no superscript
		sf[1][0]=1;
	}

	public String toString () {
		return "AG<=20 (remaining >= 0)  ";
	}

}

