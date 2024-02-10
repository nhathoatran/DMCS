int x = 0;
int y = 0;

active proctype P1(){	
	run P2();
	if	
	:: x--; 
	:: x++;
	:: assert(y==0);
	fi
}

active proctype P2(){
	if
	:: y--;	
	:: y++;
	fi
}
	