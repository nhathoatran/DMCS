// Copyright 2010, University of Twente, Formal Methods and Tools group
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package sspinja.scheduler.search;

import spinja.exceptions.SpinJaException;
import spinja.model.Model;
import spinja.model.Transition;
import spinja.search.TransitionCalculator;
import spinja.store.StateStore;
import spinja.util.Log;
import sspinja.scheduling.optimize.SchedulerPanOptimizeModel;

public class SchedulerOptimizeSearch<M extends Model<T>, T extends Transition> extends SchedulerHeuristicSearchAlgorithm<M, T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SchedulerOptimizeSearch(M model, StateStore store, int stackSize, boolean errorExceedDepth,
			boolean checkForDeadlocks, int maxErrors, TransitionCalculator<M, T> nextTransition) {
		super(model, store, stackSize, errorExceedDepth, checkForDeadlocks, maxErrors, nextTransition);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected Transition nextTransition() {		
		return model.nextTransition(null) ;
	}

	@Override
	protected Transition nextTransition(Transition last) {	
		return model.nextTransition((T) last) ;
	}	
	
	public void takeTransition(final Transition transition) throws SpinJaException {
		if (transition != null) {			
			transition.take();
		}
	}
//	@Override
//	protected void takeTransition(final Transition next) throws SpinJaException {
//		stack.takeTransition(next);
//	}
}