int x = 0;
int y = 0;

active proctype A(){		
	y++;
	do
	:: x--;
	:: y++;
	od
}

active proctype B(){
	x++;
	do
	:: x--;
	:: y++;	
	:: x++;
	od
}

error {
	x == 2;
}
	