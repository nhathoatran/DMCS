
int x = 0;

active proctype P1(){	
	sch_exec(P2());
	if	
	:: x=0; 
	:: x=1;	
	fi
}

active proctype P2(){
	do
	:: x++;	
	:: x--;
	:: assert(x!=2);
	od
}

