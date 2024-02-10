package espinja;
import espinja.Run;

import spinja.util.DataReader;
import spinja.util.DataWriter;
import spinja.promela.model.*;
import spinja.exceptions.*;

public class PanModel extends PromelaModel {


	public static void main(String[] args) {
		//CTL/RTCTL
		Run run = new Run(5);
		run.parseArguments(args,"Pan");
		run.setDepth(32);
		run.search(PanModel.class);
	}

	int water;
	int cake;
	int coffee;
	int input;
	int remaining;

	public PanModel() throws SpinJaException {
		super("Pan", 21);

		// Initialize the default values
		water = 0;
		cake = 0;
		coffee = 0;
		input = 0;
		remaining = 0;
		// Initialize the starting processes
		addProcess(new test_0());
	}

	public void encode(DataWriter _writer) {
		_writer.writeByte(_nrProcs);
		_writer.writeInt(water);
		_writer.writeInt(cake);
		_writer.writeInt(coffee);
		_writer.writeInt(input);
		_writer.writeInt(remaining);
		for(byte _i = 0; _i < _nrProcs; _i++) { 
			_procs[_i].encode(_writer);
		}

	}

	public boolean decode(DataReader _reader) {
		_nrProcs = _reader.readByte();
		water = _reader.readInt();
		cake = _reader.readInt();
		coffee = _reader.readInt();
		input = _reader.readInt();
		remaining = _reader.readInt();

		int _start = _reader.getMark();
		for(int _i = 0; _i < _nrProcs; _i++) {
			_reader.storeMark();
			if(_procs[_i] == null || !_procs[_i].decode(_reader)) {
				_reader.resetMark();
				switch(_reader.peekByte()) {
					case 0: _procs[_i] = new test_0(true, _i); break;
					case 1: _procs[_i] = new insert1_0(true, _i); break;
					case 2: _procs[_i] = new insert2_0(true, _i); break;
					case 3: _procs[_i] = new insert5_0(true, _i); break;
					case 4: _procs[_i] = new buy_water_0(true, _i); break;
					case 5: _procs[_i] = new buy_cake_0(true, _i); break;
					case 6: _procs[_i] = new buy_coffee_0(true, _i); break;
					case 7: _procs[_i] = new out_0(true, _i); break;
					case 8: _procs[_i] = new VerificationCase0_0(true, _i); break;
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
		sb.append("+ PanModel: \n");
		sb.append("water = ").append(water).append('\t');
		sb.append("cake = ").append(cake).append('\t');
		sb.append("coffee = ").append(coffee).append('\t');
		sb.append("input = ").append(input).append('\t');
		sb.append("remaining = ").append(remaining).append('\t');
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

	public class test_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public test_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[3], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 53, 0, 2, "run VerificationCase0_0()") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							public final void takeImpl() throws ValidationException {
								addProcess(new VerificationCase0_0());
							}//1

							public final void undoImpl() {
								//undo statement
								endProcess();
							}
						};
					}
				};
			factory.append(
				new ElseTransitionFactory(54, 0, 1, false));
			_stateTable[0] = new State(test_0.this, factory, false, false, false);

			factory = 
				new PromelaTransitionFactory(true, 36, 1, 2, "skip") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							public final void undoImpl() {
								//undo statement
							}
						};
					}
				};
			_stateTable[1] = new State(test_0.this, factory, false, false, false);

			factory = 
				new EndTransitionFactory(33);
			_stateTable[2] = new State(test_0.this, factory, true, false, false);

		}

		public test_0() throws ValidationException {
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
			sb.append("test_0 (" +  _pid + "," + _sid + "): ");
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

	public class insert1_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public insert1_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 55, 0, 0, "input = (input + 1)") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								input = (input + 1);
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
							}
						};
					}
				};
			_stateTable[0] = new State(insert1_0.this, factory, false, false, false);

		}

		public insert1_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
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
			sb.append("insert1_0 (" +  _pid + "," + _sid + "): ");
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

	public class insert2_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public insert2_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 56, 0, 0, "input = (input + 2)") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								input = (input + 2);
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
							}
						};
					}
				};
			_stateTable[0] = new State(insert2_0.this, factory, false, false, false);

		}

		public insert2_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x2);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x2) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("insert2_0 (" +  _pid + "," + _sid + "): ");
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

	public class insert5_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public insert5_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 57, 0, 0, "input = (input + 5)") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								input = (input + 5);
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
							}
						};
					}
				};
			_stateTable[0] = new State(insert5_0.this, factory, false, false, false);

		}

		public insert5_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x3);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x3) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("insert5_0 (" +  _pid + "," + _sid + "): ");
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

	public class buy_water_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public buy_water_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 58, 0, 0, "if") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;
							private int _backup_remaining;

							public final void takeImpl() throws ValidationException {
								_backup_remaining = remaining;
								_backup_input = input;
								if((input >= water)) {
									input = (input - water);
									remaining = (remaining + water);
								} else {
								}
							}//1

							public final void undoImpl() {
								//undo statement
								remaining = _backup_remaining;
								input = _backup_input;
							}
						};
					}
				};
			_stateTable[0] = new State(buy_water_0.this, factory, false, false, false);

		}

		public buy_water_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x4);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x4) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("buy_water_0 (" +  _pid + "," + _sid + "): ");
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

	public class buy_cake_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public buy_cake_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 59, 0, 0, "if") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;
							private int _backup_remaining;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								_backup_remaining = remaining;
								if((input >= cake)) {
									input = (input - cake);
									remaining = (remaining + cake);
								} else {
								}
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
								remaining = _backup_remaining;
							}
						};
					}
				};
			_stateTable[0] = new State(buy_cake_0.this, factory, false, false, false);

		}

		public buy_cake_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x5);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x5) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("buy_cake_0 (" +  _pid + "," + _sid + "): ");
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

	public class buy_coffee_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public buy_coffee_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 60, 0, 0, "if") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;
							private int _backup_remaining;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								_backup_remaining = remaining;
								if((input >= coffee)) {
									input = (input - coffee);
									remaining = (remaining + coffee);
								} else {
								}
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
								remaining = _backup_remaining;
							}
						};
					}
				};
			_stateTable[0] = new State(buy_coffee_0.this, factory, false, false, false);

		}

		public buy_coffee_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x6);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x6) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("buy_coffee_0 (" +  _pid + "," + _sid + "): ");
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

	public class out_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public out_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[1], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 61, 0, 0, "input = 0") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_input;

							public final void takeImpl() throws ValidationException {
								_backup_input = input;
								input = 0;
							}//1

							public final void undoImpl() {
								//undo statement
								input = _backup_input;
							}
						};
					}
				};
			_stateTable[0] = new State(out_0.this, factory, false, false, false);

		}

		public out_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x7);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x7) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("out_0 (" +  _pid + "," + _sid + "): ");
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

	public class VerificationCase0_0 extends PromelaProcess {
		protected int _pc;
		protected int _pid;

		public VerificationCase0_0(boolean decoding, int pid) {
			super(PanModel.this, pid, new State[2], 0);

			PromelaTransitionFactory factory;
			factory = 
				new PromelaTransitionFactory(false, 51, 0, 1, "printf(\"config2\");; water = 1; cake = 6; coffee = 8; remaining = 10; run insert1_0(); run insert2_0(); run insert5_0(); run buy_water_0(); run buy_cake_0(); run buy_coffee_0(); run out_0()") {
					public final PromelaTransition newTransition() {
						return new NonAtomicTransition() {
							private int _backup_coffee;
							private int _backup_water;
							private int _backup_cake;
							private int _backup_remaining;

							public final void takeImpl() throws ValidationException {
								printf("config2");
								_backup_water = water;
								water = 1;
								_backup_cake = cake;
								cake = 6;
								_backup_coffee = coffee;
								coffee = 8;
								_backup_remaining = remaining;
								remaining = 10;
								addProcess(new insert1_0());
								addProcess(new insert2_0());
								addProcess(new insert5_0());
								addProcess(new buy_water_0());
								addProcess(new buy_cake_0());
								addProcess(new buy_coffee_0());
								addProcess(new out_0());
							}//1

							public final void undoImpl() {
								//undo statement
								endProcess();
								endProcess();
								endProcess();
								endProcess();
								endProcess();
								endProcess();
								endProcess();
								remaining = _backup_remaining;
								coffee = _backup_coffee;
								cake = _backup_cake;
								water = _backup_water;
							}
						};
					}
				};
			_stateTable[0] = new State(VerificationCase0_0.this, factory, false, false, false);

			factory = 
				new EndTransitionFactory(52);
			_stateTable[1] = new State(VerificationCase0_0.this, factory, true, false, false);

		}

		public VerificationCase0_0() throws ValidationException {
			this(false, _nrProcs);

			_pc = -1;
			_pid = -1;
		}

		public int getSize() {
		  return 5;
		}

		public void encode(DataWriter _writer) {
			_writer.writeByte(0x8);
			_writer.writeByte(_sid);
			_writer.writeShort(_pc);
			_writer.writeByte(_pid);
		}

		public boolean decode(DataReader _reader) {
			if(_reader.readByte() != 0x8) return false;
			_sid = _reader.readByte();
			_pc = _reader.readShort();
			_pid = _reader.readByte();
			return true;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(_exclusive ==  _pid) sb.append("<atomic>");
			sb.append("VerificationCase0_0 (" +  _pid + "," + _sid + "): ");
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
	
	//AG<=20 (remaining >= 0)  
	public boolean[] init_sf() {
		boolean checked[] = new boolean[2];
		checked[1] = true ; //(remaining >= 0) ;
		return checked ;
	}
	
	public boolean[] init_atomicf() {
		boolean result[] = new boolean[2];
		result[1] = (remaining >= 0) ;
		return result ;
	}
	
	public boolean stateCheck() {
		return false ;
	}
	public boolean modelCheck() {
		return true ;
	}
	
	public boolean collectState() {
		return false ;
	}
}
