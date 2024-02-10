int x = 0;
int y = 0;

active proctype P1(){	
	do
	:: x--;
	:: x++;
	:: assert(y!=1);
	od
}

active proctype P2(){
	do
	:: y--;	
	:: y++;
	:: assert(x !=1);
	od
}
	