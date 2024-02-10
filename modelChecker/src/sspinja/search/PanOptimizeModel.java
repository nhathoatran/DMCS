package sspinja.search;
import sspinja.Run;

import spinja.util.DataReader;
import spinja.util.DataWriter;
import spinja.promela.model.*;
import spinja.exceptions.*;

public class PanOptimizeModel extends PromelaModel {


	public static void main(String[] args) {
		//DFS
		Run run = new Run();
		run.parseArguments(args,"PanOptimize");
		run.search(PanOptimizeModel.class);
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

	int x;
	int y;

	public PanOptimizeModel() throws SpinJaException {
		super("PanOptimize", 9);

		// Initialize the default values
		x = 0;
		y = 0;
		// Initialize the starting processes
		addProcess(new P1_0());
		addProcess(new P2_0());
	}

	public boolean isAccepted() {
		return false ;
	}

	public VarState getVarState() {
		return new VarState(x, y) ;
	}

	public void encode(DataWriter _writer) {
		_writer.writeByte(_nrProcs);
		_writer.writeInt(x);
		_writer.writeInt(y);
		for(byte _i = 0; _i < _nrProcs; _i++) { 
			_procs[_i].encode(_writer);
		}

	}

	public boolean decode(DataReader _reader) {
		_nrProcs = _reader.readByte();
		x = _reader.readInt();
		y = _reader.readInt();

		int _start = _reader.getMark();
		for(int _i = 0; _i < _nrProcs; _i++) {
			_reader.storeMark();
			if(_procs[_i] == null || !_procs[_i].decode(_reader)) {
				_reader.resetMark();
				switch(_reader.peekByte()) {
					case 0: _procs[_i] = new P1_0(true, _i); break;
					case 1: _procs[_i] = new P2_0(true, _i); break;
					default: return false;
				}
				if(!_procs[_i].decode(_reader)) return false;
			}
		}
		_process_size = _reader.getMark() - _start;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("+ PanOptimizeModel: \n");
		sb.append("x = ").append(x).append('\t');
		sb.append("y = ").append(y).append('\t');
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
		protected int _pc;
		protected int _pid;

		public P1_0(boolean decoding, int pid) {
			super(PanOptimizeModel.this, pid, new State[3], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 13, 0, 1, "run P2_0()") {
					public final PromelaTransition newTransition() {
						//useful transition
						return new NonAtomicTransition(0) {
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
				new PromelaTransitionFactory(false, 26, 1, 2, "x--") {
					public final PromelaTransition newTransition() {
						//useless transition
						return new NonAtomicTransition(2) {
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
				};
			factory.append(
				new PromelaTransitionFactory(false, 27, 1, 2, "x++") {
					public final PromelaTransition newTransition() {
						//useless transition
						return new NonAtomicTransition(2) {
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
				});
			factory.append(
				new PromelaTransitionFactory(false, 28, 1, 2, "assert (y == 0)") {
					public final PromelaTransition newTransition() {
						//useful transition
						return new NonAtomicTransition(0) {
							public final void takeImpl() throws ValidationException {
								if(!(y == 0)) throw new AssertionException("(y == 0)");
							}//1

							public final void undoImpl() {
								//undo statement
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

			_pc = 0;
			_pid = 0;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x0);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x0) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("P1_0 (" +  _pid + "," + _sid + "): ");
			sb.append("_pc = ").append(_pc).append('\t');
			sb.append("_pid = ").append(_pid).append('\t');
			return sb.toString();
		}

		public int getChannelCount() {
			return 0;
		}
		@Override
		public int getId() {return _pid;}
		@Override
		public int get_pid() {return _pid;}
		@Override
		public void set_pid(int _pid) {this._pid = _pid;}
	}

	public class P2_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public P2_0(boolean decoding, int pid) {
			super(PanOptimizeModel.this, pid, new State[2], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 29, 0, 1, "y--") {
					public final PromelaTransition newTransition() {
						//useless transition
						return new NonAtomicTransition(2) {
							private int _backup_y;

							public final void takeImpl() throws ValidationException {
								_backup_y = y;
								y--;
							}//1

							public final void undoImpl() {
								//undo statement
								y = _backup_y;
							}
						};
					}
				};
			factory.append(
				new PromelaTransitionFactory(false, 30, 0, 1, "y++") {
					public final PromelaTransition newTransition() {
						//useless transition
						return new NonAtomicTransition(2) {
							private int _backup_y;

							public final void takeImpl() throws ValidationException {
								_backup_y = y;
								y++;
							}//1

							public final void undoImpl() {
								//undo statement
								y = _backup_y;
							}
						};
					}
				});
			_stateTable[0] = new State(P2_0.this, factory, false, false, false);

			factory = 
				new EndTransitionFactory(23);
			_stateTable[1] = new State(P2_0.this, factory, true, false, false);

		}

		public P2_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = 0;
			_pid = 1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x1);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x1) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("P2_0 (" +  _pid + "," + _sid + "): ");
			sb.append("_pc = ").append(_pc).append('\t');
			sb.append("_pid = ").append(_pid).append('\t');
			return sb.toString();
		}

		public int getChannelCount() {
			return 0;
		}
		@Override
		public int getId() {return _pid;}
		@Override
		public int get_pid() {return _pid;}
		@Override
		public void set_pid(int _pid) {this._pid = _pid;}
	}
	public String sysGet(String va) {
		String result = "";
		switch (va) {
			case "x" :
				result += x;
				break ;
			case "y" :
				result += y;
				break ;
		}
		return result ;
	}
}
