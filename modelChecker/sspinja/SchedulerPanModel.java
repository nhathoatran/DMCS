package sspinja;
import sspinja.scheduler.promela.model.*;
import spinja.exceptions.*;
import sspinja.scheduling.SchedulerObject;
import sspinja.scheduling.SchedulerProcess;

public class SchedulerPanModel extends SchedulerPromelaModel { 
	public SchedulerPanModel() throws SpinJaException {
		super("SchedulerPan", 0);
	}
}
