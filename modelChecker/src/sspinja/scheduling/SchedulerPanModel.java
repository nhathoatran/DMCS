package sspinja.scheduling;
import sspinja.Run;

import spinja.util.DataReader;
import spinja.util.DataWriter;
import sspinja.search.VarState;
import spinja.promela.model.*;
import spinja.exceptions.*;
import sspinja.scheduler.promela.model.*;

public class SchedulerPanModel extends SchedulerPromelaModel {


	public static void main(String[] args) {
		//scheduler DFS
		Run run = new Run(6);
		run.parseArguments(args,"SchedulerPan");
		if (SchedulerPanModel.scheduler.InitSchedulerObject(run.scheduleropt.getOption()))
		run.search(SchedulerPanModel.class);
	}

	public int getErrorDistance(){
		return 0 ;
	}

	public boolean checkError(){
		return false;
	}

	public String errorState(){
		return "";
	}

	public int x;

	public SchedulerPanModel() throws SpinJaException {
		super("SchedulerPan", 5);

		// Initialize the default values
		x = 0;
		// Initialize the starting processes
		addProcess(new P1_0());
		addProcess(new P2_0());
	}

	public VarState getVarState() {
		return new VarState(x) ;
	}

	public void encode(DataWriter _writer) {
		scheduler.encode(_writer);
		_writer.writeByte(_nrProcs);
		_writer.writeInt(x);
		int _i = 0 ;
		int _pcount = 0 ;
		while (_pcount < _nrProcs && _i < 255) {
			if (_procs[_i] != null && SchedulerObject.processInScheduler[_i]) {
				_procs[_i].encode(_writer);
				_pcount++;
			}
			_i++;
		}
	}

	public boolean decode(DataReader _reader) {
		scheduler.decode(_reader) ; 
		_nrProcs = _reader.readByte();
		x = _reader.readInt();

		int _start = _reader.getMark();
		for(int _i = 0; _i < _nrProcs; _i++) {
			_reader.storeMark();
			if(SchedulerObject.processInScheduler[_i]) {
				if(_procs[_i] == null || !_procs[_i].decode(_reader)) { // change local variables
					_reader.resetMark();
					switch(_reader.peekByte()) {
						case 0: _procs[_i] = new P1_0(true, _i); break;
						case 1: _procs[_i] = new P2_0(true, _i); break;
						default: return false;
					}
					if(!_procs[_i].decode(_reader)) return false;
				}
			}
		}
		_process_size = _reader.getMark() - _start;
		//updateProcessListInScheduler(_nrProcs) ;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("+ SchedulerPanModel: \n");
		sb.append("x = ").append(x).append('\t');
		for(int i = 0; i < _nrProcs; i++) {
		  sb.append('\n').append(_procs[i]);
		}
		for(int i = 0; i < _nrChannels; i++) {
		  sb.append('\n').append(_channels[i]);
		}
		return sb.toString();
	}

	public int getChannelCount() {
		return 0;
	}

	public class P1_0 extends PromelaProcess {

		public P1_0(boolean decoding, int pid) {
			super(SchedulerPanModel.this, pid, new State[3], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 13, 0, 1, "run P2_0()") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							public final void takeImpl() throws ValidationException {
								addProcess(new P2_0());
							}//1

							public final void undoImpl() {
								//undo statement
								endProcess();
							}
						};
					}
				};
			_stateTable[0] = new State(P1_0.this, factory, false, false, false);

			factory = 
				new PromelaTransitionFactory(false, 25, 1, 2, "x = 0") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_x;

							public final void takeImpl() throws ValidationException {
								_backup_x = x;
								x = 0;
							}//1

							public final void undoImpl() {
								//undo statement
								x = _backup_x;
							}
						};
					}
				};
			factory.append(
				new PromelaTransitionFactory(false, 26, 1, 2, "x = 1") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_x;

							public final void takeImpl() throws ValidationException {
								_backup_x = x;
								x = 1;
							}//1

							public final void undoImpl() {
								//undo statement
								x = _backup_x;
							}
						};
					}
				});
			_stateTable[1] = new State(P1_0.this, factory, false, false, false);

			factory = 
				new EndTransitionFactory(16);
			_stateTable[2] = new State(P1_0.this, factory, true, false, false);

		}

		public P1_0() throws ValidationException {
			this(false, _nrProcs);

		}

		public int getSize() {
		  return 2;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x0);
			_writer.writeByte(_sid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x0) return false;
			_sid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("P1_0 (" +  _pid + "," + _sid + "): ");
			return sb.toString();
		}

		public String getName() {
			return "P1" ;
		}

		public int getChannelCount() {
			return 0;
		}
	}

	public class P2_0 extends PromelaProcess {

		public P2_0(boolean decoding, int pid) {
			super(SchedulerPanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 27, 0, 0, "x++") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_x;

							public final void takeImpl() throws ValidationException {
								_backup_x = x;
								x++;
							}//1

							public final void undoImpl() {
								//undo statement
								x = _backup_x;
							}
						};
					}
				};
			factory.append(
				new PromelaTransitionFactory(false, 28, 0, 0, "x--") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_x;

							public final void takeImpl() throws ValidationException {
								_backup_x = x;
								x--;
							}//1

							public final void undoImpl() {
								//undo statement
								x = _backup_x;
							}
						};
					}
				});
			factory.append(
				new PromelaTransitionFactory(false, 29, 0, 0, "assert (x != 2)") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							public final void takeImpl() throws ValidationException {
								if(!(x != 2)) throw new AssertionException("(x != 2)");
							}//1

							public final void undoImpl() {
								//undo statement
							}
						};
					}
				});
			_stateTable[0] = new State(P2_0.this, factory, false, false, false);

		}

		public P2_0() throws ValidationException {
			this(false, _nrProcs);

		}

		public int getSize() {
		  return 2;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x1);
			_writer.writeByte(_sid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x1) return false;
			_sid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("P2_0 (" +  _pid + "," + _sid + "): ");
			return sb.toString();
		}

		public String getName() {
			return "P2" ;
		}

		public int getChannelCount() {
			return 0;
		}
	}
	public String sysGet(String va) {
		String result = "";
		switch (va) {
			case "x" :
				result += x;
				break ;
		}
		return result ;
	}
}
