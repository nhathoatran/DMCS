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

package sspinja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

import spinja.options.BooleanOption;
import spinja.options.MultiStringOption;
import spinja.options.OptionParser;
import spinja.options.StringOption;
import spinja.promela.compiler.Instantiate;
import spinja.promela.compiler.ProcInstance;
import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.Specification;
import spinja.promela.compiler.automaton.State;
import spinja.promela.compiler.automaton.Transition;
import spinja.promela.compiler.optimizer.GraphOptimizer;
import spinja.promela.compiler.optimizer.RemoveUselessActions;
import spinja.promela.compiler.optimizer.RemoveUselessGotos;
import spinja.promela.compiler.optimizer.RenumberAll;
import spinja.promela.compiler.optimizer.StateMerging;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Preprocessor;
import spinja.promela.compiler.parser.Promela;
import spinja.promela.compiler.parser.PromelaTokenManager;
import spinja.promela.compiler.parser.SimpleCharStream;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.parser.TokenMgrError;

public class CompileScheduler {
	private static Specification compile(final File promFile, 
	                                     final String name,
		                                 final boolean useStateMerging,
		                                 final boolean verbose) {
		try {
			Preprocessor.setFilename(promFile.getName());
			String path = promFile.getAbsolutePath();
			Preprocessor.setDirname(path.substring(0, path.lastIndexOf(File.separator)));//HIL Fix

			System.out.print("Start parsing " + promFile.getName() + "...");
			final Promela prom = new Promela(new FileInputStream(promFile));
			final Specification spec = prom.spec(name);
			Instantiate inst=new Instantiate(spec);
			inst.createInstantiate();
			Iterator<ProcInstance> it=spec.iterator();
			spec.getProcs().clear();
		    while(it.hasNext()){
		    	spec.getProcs().add(it.next());
		    }
			System.out.println("done");
			System.out.println("");

			System.out.println("Optimizing graphs...");
			final GraphOptimizer[] optimizers = new GraphOptimizer[] {
					useStateMerging ? new StateMerging() : null, new RemoveUselessActions(),
					new RemoveUselessGotos(), new RenumberAll(),
			};
			
			for (final GraphOptimizer opt : optimizers) {
				if (opt == null) continue;
				int reduction = 0;
				for (final Proctype proc : spec.getProcs()) {
					if (verbose) {
						System.out.println("Initial graph for process " + proc + ":");
						System.out.println(proc.getAutomaton());
					}
					reduction += opt.optimize(proc.getAutomaton());
					if (verbose) {
						System.out.println("After " + opt.getClass().getSimpleName() + ":");
						System.out.println(proc.getAutomaton());
					}
					if (verbose) System.out.println(proc.getAutomaton());
				}
				System.out.println("   "+ opt.getClass().getSimpleName() +" changed "+ reduction +" states/transitions.");
			}

			final Proctype never = spec.getNever();
			if (never != null) {
				if (verbose) {
					System.out.println("Initial graph for never claim:");
					System.out.println(never.getAutomaton());
				}
				for (final GraphOptimizer opt : optimizers) {
					if (opt == null) continue;
					int reduction = opt.optimize(never.getAutomaton());
					System.out.println("   "+ opt.getClass().getSimpleName() +" reduces "+ reduction +" states");
					if (verbose) {
						System.out.println("After " + opt.getClass().getSimpleName() + ":");
						System.out.println(never.getAutomaton());
					}
				}
				if (verbose) System.out.println(never.getAutomaton());
			}
			
			final Proctype sporadic = spec.getSporadic();
			if (sporadic!= null) {
				if (verbose) {
					System.out.println("Initial graph for sporadic process:");
					System.out.println(never.getAutomaton());
				}
				for (final GraphOptimizer opt : optimizers) {
					if (opt == null) continue;
					int reduction = opt.optimize(sporadic.getAutomaton());
					System.out.println("   "+ opt.getClass().getSimpleName() +" reduces "+ reduction +" states");
					if (verbose) {
						System.out.println("After " + opt.getClass().getSimpleName() + ":");
						System.out.println(sporadic.getAutomaton());
					}
				}
				if (verbose) System.out.println(sporadic.getAutomaton());
			}
			
			System.out.println("Optimization done");
			System.out.println("");
			return spec;
		} catch (final FileNotFoundException ex) {
			System.out.println("Promela file " + promFile.getName() + " could not be found.");
		} catch (final ParseException ex) {
			System.out.println("Parse exception in file " + Preprocessor.getFileName() + ": "
								+ ex.getMessage());
		}
		return null;
	}


	public static void main(final String[] args) throws IOException {
		Config.isCompileScheduler = true ; //for realize the sporadic process
		Config.searchOption = Config.SchedulerDFS ;
		
		final String  defaultname = "Pan";
		final String  shortd  = 
			"SSpinJa Promela Compiler - version " + Version.VERSION + " (" + Version.DATE + ")\n" +
			"with SpinJa 0.9.3 (C) University of Twente, Formal Methods and Tools group";
		final String  longd   = 
			"SpinJa Promela Compiler: this compiler converts a Promela source file\n" +
			"to a Java model that can be checked by the SpinJa Model Checker." ;

		final OptionParser parser = 
			new OptionParser("java spinja.Compiler", shortd, longd, true);

		final StringOption modelname = new StringOption('n',
			"sets the name of the model \n" +
			"(default: " + defaultname + ")", false);
		parser.addOption(modelname);

		final StringOption define = new StringOption('D',
			"sets preprocessor macro define value", true);
		parser.addOption(define);

		final BooleanOption dot = new BooleanOption('d',
			"only write dot output (ltsmin/spinja) \n");
		parser.addOption(dot);
		
		final BooleanOption ltsmin = new BooleanOption('l',
			"sets output to LTSmin \n");
		parser.addOption(ltsmin);

		final BooleanOption ltsmin_ltl = new BooleanOption('L',
			"sets output to LTSmin with LTSmin LTL semantics \n");
		parser.addOption(ltsmin_ltl);

        final BooleanOption textbook_ltl = new BooleanOption('t',
            "sets output to LTSmin with textbook LTL semantics \n");
        parser.addOption(textbook_ltl);
		
		final BooleanOption preprocessor = new BooleanOption('I',
			"prints output of preprocessor\n");
		parser.addOption(preprocessor);

		final StringOption srcDir = new StringOption('s',
			"sets the output directory for the sourcecode \n" +
			"(default: spinja)", false);
		parser.addOption(srcDir);

		final MultiStringOption optimalizations = new MultiStringOption('o',
			"disables one or more optimalisations", 
				new String[] { "3" }, 
				new String[] { "disables statement merging" }
			);
		parser.addOption(optimalizations);

		final BooleanOption verbose = new BooleanOption('v',
			"verbose: show diagnostic information on the \n" +
			"compilation process.");
		parser.addOption(verbose);

		parser.parse(args);
		final List<String> files = parser.getFiles();
		
		final File userDir = new File(System.getProperty("user.dir"));
//		files.add(userDir + "/src/needscheduling.pml") ;
//		System.out.print(userDir + "/src/needscheduling.pml");
		
		files.add(userDir + "/src/schedulingheuristicbetter.pml") ;
		System.out.print(userDir + "/src/schedulingheuristicbetter.pml");
		
		if (files.size() == 0) {
			System.out.println("Please specify one file that is to be compiled!");
			parser.printUsage();
		}
		File file = new File(files.get(0));
		if (!file.exists() || !file.isFile()) {
			System.out.println("File " + file.getName() + " does not exist or is not a valid file!");
			parser.printUsage();
		}
		
	    List<Path> inputs ;
	    
    	inputs = Arrays.asList(				
				Paths.get(file.getPath())
	    );		

		if (ltsmin.isSet() && ltsmin_ltl.isSet()) {
			System.err.println("Choose -l or -L.");
			System.exit(-1);
		}
        if (textbook_ltl.isSet()) {
            System.err.println("Textbook LTL semantics not yet implemented.");
            System.exit(-1);
        }
        
        String name = "Scheduler";
		if (modelname.isSet()) {
			name += modelname.getValue();
		} else {
			name += defaultname;
		}

		for (String def : define) {
		    int indexEq = def.indexOf('=');
		    String defName = def;
		    String defArg = "";
		    if (indexEq != -1) {
		        defName = def.substring(0, indexEq).trim();
		        defArg = def.substring(indexEq + 1);
		    }
		    Preprocessor.define.name = defName;
		    Preprocessor.addDefine(defArg, false);
		}

		if (preprocessor.isSet()) {
			Preprocessor.setFilename(file.getName());
			String path = file.getAbsolutePath();
			Preprocessor.setDirname(path.substring(0, path.lastIndexOf("/")));
			PromelaTokenManager tm;
			try {
				tm = new PromelaTokenManager(null, new SimpleCharStream(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				throw new AssertionError(e);
			}
			while (true) {
				try {
					Token t = tm.getNextToken();
					System.out.print(t.image);
					if (t.image == "") break;
				} catch (TokenMgrError e) {
					break;
				}
			}
			System.exit(0);
		}

		System.out.println("Compile file: " + file.toString());
		final Specification spec = 
			compile(file, name, !optimalizations.isSet("3"), verbose.isSet());

		if (spec == null) {
			System.exit(-4);
		}
		
		
		File outputDir = null;
		
		if (ltsmin.isSet() || ltsmin_ltl.isSet()) {
			if (dot.isSet()) {
				System.out.println("Written DOT file to " + outputDir + "/" + file.getName()+".spinja.dot");
			} else {
				System.out.println("Written C model to " + outputDir + "/" + file.getName()+".spinja.c");
			}
		} else {
			outputDir = new File(file.getParent(), "sspinja");	
			
			if (!outputDir.exists() && !outputDir.mkdirs()) {
				System.out.println("Error: could not generate directory " + outputDir.getName());
				System.exit(-3);
			}
			if (dot.isSet()) {
				writeDotFile(spec, file.getName(), outputDir);
				System.out.println("Written DOT file for '" + file + "' to\n" + outputDir + "/" + file.getName()+".spinja.dot");
			} else {
				writeGraphFile(spec, file.getName(), outputDir);
				writeFiles(spec, name, outputDir);
				System.out.println("Written Java files with scheduler for '" + file + "' to\n" + outputDir);
				
//				File verifierDir = new File(file.getParent(), "sspinja/verifier");
//				if (!verifierDir.exists() && !verifierDir.mkdirs()) {
//					System.out.println("Error: could not generate directory " + verifierDir.getName());
//					System.exit(-3);
//				}
//				writeVerifierFile(verifierDir);
//				
//				File simulatorDir = new File(file.getParent(), "sspinja/simulator");
//				if (!simulatorDir.exists() && !simulatorDir.mkdirs()) {
//					System.out.println("Error: could not generate directory " + simulatorDir.getName());
//					System.exit(-3);
//				}
//				writeSimulatorFile(simulatorDir);
//				
				try {
					//Utility.refreshDir();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	

	private static void writeVerifierFile (final File verifierDir) {
		final File verifierFile = new File(verifierDir, "Verifier.java");

		String out = "";
		
		out += 
				"package sspinja.verifier;\n" +
				"import sspinja.SchedulerPanModel;\n\n" +

				"public class Verifier {\n" +
					"\tpublic static void main(String[] args) {\n" +
						"\t\tSchedulerPanModel.main(args);\n"+
					"\t}\n" +
				"}";
		
		try {
			FileOutputStream fos = new FileOutputStream(verifierFile);
			fos.write(out.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeSimulatorFile (final File simulatorDir) {
		final File simulatorFile = new File(simulatorDir, "Simulator.java");

		String out = "";
		
		out += 
				"package sspinja.simulator;\n" +
				"import sspinja.ui.MainUI;\n\n" +

				"public class Simulator {\n" +
					"\tpublic static void main(String[] args) {\n" +
						"\t\tMainUI.main(args);\n"+
					"\t}\n" +
				"}";
		
		try {
			FileOutputStream fos = new FileOutputStream(simulatorFile);
			fos.write(out.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeGraphFile (final Specification spec, final String name, final File outputDir) {
		final File graphFile = new File(outputDir, name + ".graph");

		String out = "";
		for (Proctype proc : spec) {
			String pName = proc.getName();
			out += pName + "\n"; //proc
			for (State state : proc.getAutomaton()) {
				String from = state.getStateId() +"";
				
				boolean atomic = state.isInAtomic();
				for (Transition t : state.output) {
					atomic |= (t.getTo()!=null && t.getTo().isInAtomic());
					String to = (t.getTo()==null ? "end" : t.getTo().getStateId() +"");
					String p = pName +"_"+ from + "->"+  pName + "_"+ to +"->" + t.getTransId() + "->[" + t.getText() + "]";
					out += p +"\n";
				}
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(graphFile);
			fos.write(out.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeDotFile (final Specification spec, final String name, final File outputDir) {
		final File dotFile = new File(outputDir, name + ".spinja.dot");

		String out = "digraph {\n";
		for (Proctype proc : spec) {
			String n = proc.getName();
			for (State state : proc.getAutomaton()) {
				String from = state.getStateId() +"";
				boolean atomic = state.isInAtomic();
				out += "\t\""+ n +"_"+ from +"\" "+ (atomic ? "[penwidth=4]" : "") +"\n";
				for (Transition t : state.output) {
					atomic |= (t.getTo()!=null && t.getTo().isInAtomic());
					String to = (t.getTo()==null ? "end" : t.getTo().getStateId() +"");
					String p = "\"";
					p += n +"_"+ from +"\" -> \""+  n +"_"+ to +"\"";
					out += "\t"+ p + (atomic ? "[penwidth=4]" : "") +"\n";
				}
			}
		}
		out += "}\n";
		try {
			FileOutputStream fos = new FileOutputStream(dotFile);
			fos.write(out.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeFiles(final Specification spec, final String name, final File outputDir) {
		final File javaFile = new File(outputDir, name + "Model.java");
		try {
			final FileOutputStream fos = new FileOutputStream(javaFile);
			fos.write(spec.generateModel().getBytes());
			fos.flush();
			fos.close();
		} catch (final IOException ex) {
			System.out.println("IOException while writing java files: " + ex.getMessage());
			System.exit(-5);
		} catch (final ParseException ex) {
			System.out.println("Parse exception: " + ex.getMessage());
			System.exit(-6);
		}
	}
}