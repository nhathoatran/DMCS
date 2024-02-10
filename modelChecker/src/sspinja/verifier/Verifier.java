package sspinja.verifier;
import sspinja.scheduling.SchedulerPanModel;

public class Verifier {
	public static boolean isVerified = false;
	public static void main(String[] args) {
		isVerified = true;
		SchedulerPanModel.main(args);
	}
}	
