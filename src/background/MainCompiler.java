package background;
import java.io.*;

public class MainCompiler {

	static FileReader fin; // input file
	static FileWriter foutput; // output file and errors showing
	static FileWriter fcodelist; // target code list file
	static FileWriter fcode; // virtual machine code output
	static FileWriter fresult; // executing result output
	static FileWriter ftable; // symbol table output
	public static File foutputFile;
	public static File fresultFile;
	public static File fcodeFile;
	public static File fstackFile;
	public static boolean isCompileSuccess = false;
	public static int codeIndex = 0;
	
	public static void mainCompiler(String fin_name) throws IOException {
		SimpleCompiler.initParse(fin_name);
		SimpleCompiler.syntaxParserMain();
		
		fin = SimpleCompiler.fin;
		foutput = SimpleCompiler.foutput;
		fcodelist = SimpleCompiler.fcodelist;
		ftable = SimpleCompiler.ftable;
		foutputFile = new File(SimpleCompiler.fileOutputName);
		fresultFile = new File(SimpleCompiler.fileResultName);
		fcodeFile = new File(SimpleCompiler.fileCodeName);
		fstackFile = new File(SimpleCompiler.fileStackName);
		fresult = SimpleCompiler.fresult;
		
		int err = SimpleCompiler.err;
		if (err == 0) {
			isCompileSuccess = true;
			System.out.println("Parsing success!");
			foutput.write("Parsing success!\n");
			
			SimpleCompiler.listall();
			fcode = SimpleCompiler.fcode;
			fcode.close();
			
			SimpleCompiler.interpret();
		}
		else {
			isCompileSuccess = false;
			System.out.println("\n" + err + " errors in this program!");
			foutput.write("\n" + String.valueOf(err) + " errors in this program!\n");
		}
		
		ftable.close();
		foutput.close();
		fin.close();
		fcodelist.close();
		fresult.close();
	}
	
	public static String interpretOneStep() throws IOException {
		if(isCompileSuccess == false) {
			SimpleCompiler.listStackSwitch = false;
			fresult.write("Interpretation is over!\n");
			fresult.close();
			return null;
		}
		if (codeIndex == 0) {
			SimpleCompiler.initStepInterpret();
			SimpleCompiler.interpretOneInstruction();
			codeIndex ++;
			return SimpleCompiler.getInstructionStr();
		}
		else {
			if (SimpleCompiler.isInterpretOver()) {
				codeIndex = 0;
				return null;
			}
			SimpleCompiler.interpretOneInstruction();
		}
		return SimpleCompiler.getInstructionStr();
	}
}
