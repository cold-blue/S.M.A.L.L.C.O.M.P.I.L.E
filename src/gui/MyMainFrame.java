package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import background.MainCompiler;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;


public class MyMainFrame extends JFrame{
	
	private static final long serialVersionUID = -6263975104443132420L;
	JButton CafBtn;
	JButton compileBtn;
    JButton runBtn;
    JButton runStepBtn;
    Color GreyBlue;
    Color GreyBlueLight;
    Color BtnColor;
    Color BtnLightColor;
    Color HighlightBlue;
    Color LowlightBlue;
    JTextField fileTextField;
    JTextPane sourceProTextPane;
    JTextPane compilResTextPane;
    JTextPane stackTextPane;
    JTextPane consoleTextPane;
    StyledDocument sourceProDoc;
    StyledDocument compilResDoc;
    StyledDocument stackDoc;
    SimpleAttributeSet highlightAttributes;
    SimpleAttributeSet lowlightAttributes;
    JScrollPane sourceProScrollPane;
    JScrollPane compilResScrollPane;
    JScrollPane stackScrollPane;
    JScrollPane consoleScrollPane;
    boolean isFileChosen = false;
    boolean isSrcTxtAreaWritten = false;
    File chosenFile;
    
    static HashSet<String> keyWords;
	private static int codeIndex = 0;
	private static String paneStackText = null;

	private static StyledDocument compileResStyledDoc;
	private static SimpleAttributeSet compileResAttributes = new SimpleAttributeSet();
	private static SimpleAttributeSet compileResAttributes2 = new SimpleAttributeSet();
    
    void writePane(JTextPane pane, File file) {
    		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			consoleTextPane.setText("File is invalid");
			return;
			//e2.printStackTrace();
		}
		StringBuffer myStringBuffer = new StringBuffer();
		String fileLine = "";
		try {
			while((fileLine = bufferedReader.readLine()) != null)
			{
				myStringBuffer.append(fileLine + "\n");
			}
		} catch (IOException e1) {
			consoleTextPane.setText("Something occured when getting lines.");
			return;
			//e1.printStackTrace();
		}
		try {
			bufferedReader.close();
		} catch (IOException e1) {
			consoleTextPane.setText("BufferedReader falied to close.");
			return;
			//e1.printStackTrace();
		}
		String finalString = myStringBuffer.toString();
		pane.setText(finalString);
    }
     
	class MyMouseAdapter1 extends MouseAdapter {
		private char ch;
		private int chIndex = 0;
		private String paneText = null;
		private int paneTextSize;
		
		public void mouseEntered(MouseEvent e) {
			CafBtn.setBackground(GreyBlueLight);
		}
		
		public void mouseExited(MouseEvent e) {
			CafBtn.setBackground(GreyBlue);
		}
		
		public void mouseClicked(MouseEvent e) {
			String relativePath = System.getProperty("user.dir") + "/TestData";
			JFileChooser myFileChooser = new JFileChooser(relativePath);
			
			myFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			myFileChooser.showDialog(new Label(), "Choose");
			chosenFile = myFileChooser.getSelectedFile();
			if(chosenFile == null)
				return;
			String filename = chosenFile.getName();
			if(!filename.endsWith(".txt"))
			{
				consoleTextPane.setText("File format must be txt!");
				return;
			}
			String filePath = chosenFile.getAbsolutePath();
			fileTextField.setText(filePath);
			isFileChosen = true;
			
			writePane(sourceProTextPane, chosenFile);
			
			chIndex = 0;
			paneText = sourceProTextPane.getText();
			
			paneTextSize = paneText.length();
			if (paneTextSize == 0) {
				System.out.println("no availible text");
				return;
			}
			
			getch();
			while(chIndex < paneTextSize) { // one word or annotation every circle
				String word = "";
				// jump over all whitespace \n \t \r
				while (Character.isWhitespace(ch) || ch == '\n' ||
						ch == '\t' || ch == '\r') {
					getch();
				}
				
				if (ch == '#') { // annotation
					int ind0 = chIndex - 1;
					while (ch != '\n' && ch != '\r') {
						word += ch;
						getch();
					}
					sourceProDoc.setCharacterAttributes(ind0, word.length(),
							lowlightAttributes, false);
					continue;
				}
				
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
					int ind0 = chIndex - 1;
					while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
						word += ch;
						getch();
					}
					if (keyWords.contains(word)) {
						sourceProDoc.setCharacterAttributes(ind0, word.length(),
								highlightAttributes, false);
					}
					else {
						continue;
					}
				}
				getch();
			}
			System.out.println("over");
		}
		
		private void getch() {
			if (chIndex >= paneTextSize) {
				ch = '\0';
				return;
			}
			ch = paneText.charAt(chIndex);
			chIndex ++;
		}
		
    }
	class MyMouseAdapter2 extends MouseAdapter {
		String InstructionStr;
		
		public void mouseEntered(MouseEvent e) {
			JButton eBtn = (JButton)e.getSource();
			eBtn.setBackground(BtnLightColor);
		}
		public void mouseExited(MouseEvent e) {
			JButton eBtn = (JButton)e.getSource();
			eBtn.setBackground(BtnColor);
		}
		public void mouseClicked(MouseEvent e) {
			JButton eBtn = (JButton)e.getSource();
			if(eBtn == compileBtn) {
				if(isFileChosen == false)
					consoleTextPane.setText("Please choose a file to compile!");
				else
				{
					String fileStr = chosenFile.getPath();
					try {
						MainCompiler.mainCompiler(fileStr);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					writePane(consoleTextPane, MainCompiler.foutputFile);
					if (MainCompiler.isCompileSuccess == true) {
						writePane(compilResTextPane, MainCompiler.fcodeFile);
						paneStackText = compilResTextPane.getText();
						codeIndex = 0;
					}
				}
			}
			else if (eBtn == runBtn) {
				if(isFileChosen == false)
					consoleTextPane.setText("Please choose a file to compile!");
				else
				{
					if (MainCompiler.isCompileSuccess == false) {
						consoleTextPane.setText("Your code hasn't been successfully compiled"
								+ "yet!");
						return;
					}
					writePane(consoleTextPane, MainCompiler.fresultFile);
				}
			}
			else if (eBtn == runStepBtn) {
				InstructionStr = null;
				if(isFileChosen == false)
					consoleTextPane.setText("Please choose a file to compile!");
				else
				{
					if (MainCompiler.isCompileSuccess == false) {
						consoleTextPane.setText("Your code hasn't been successfully compiled"
								+ "yet!");
						return;
					}
					
					try {
						InstructionStr = MainCompiler.interpretOneStep();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					if (InstructionStr == null) {
						writePane(consoleTextPane, MainCompiler.fresultFile);
						return;
					}
					writePane(stackTextPane, MainCompiler.fstackFile);
					findCodeIndex();
					compileResStyledDoc = compilResTextPane.getStyledDocument();
					compileResStyledDoc.setCharacterAttributes(0, 
							paneStackText.length(), 
							compileResAttributes2, false);
					compileResStyledDoc.setCharacterAttributes(codeIndex, 
							InstructionStr.length(), 
							compileResAttributes, false);
					writePane(consoleTextPane, MainCompiler.fresultFile);
				}
			}
		}
		
		private void findCodeIndex() {
			codeIndex = paneStackText.indexOf(InstructionStr);
			if (codeIndex == -1) {
				return;
			}
		}
    }
	
	public MyMainFrame() {
		setTitle("My Simple Compiler");
		setSize(850,750);
		Font myFont = new Font("Geomanist", Font.PLAIN, 17);
	    
	    Color MediumBlue = new Color(39, 114, 170);
	    Color MbWordColor = new Color(225, 247, 254);
	    Color DarkBlue = new Color(6, 29, 47);
	    Color DbWordColor = new Color(226, 246, 242);
	    GreyBlue = new Color(43, 62, 78);
	    GreyBlueLight = new Color(67, 84, 98);
	    //Color WhiteBackWordColor = new Color(34, 90, 116);
	    BtnColor = new Color(48, 129, 190);
	    BtnLightColor = new Color(61, 163, 228);
	    HighlightBlue = new Color(4, 65, 129);
	    LowlightBlue = new Color(161, 216, 252);
	    Color BtnWdColor = new Color(255, 255, 255);
	    Color Blue1 = new Color(66, 159, 199);
	    Color Blue2 = new Color(78, 178, 208);
	    Color Blue3 = new Color(121, 201, 223);
	    
	    highlightAttributes = new SimpleAttributeSet();
	    lowlightAttributes = new SimpleAttributeSet();
	    StyleConstants.setForeground(highlightAttributes, HighlightBlue);
	    StyleConstants.setForeground(lowlightAttributes, LowlightBlue);
	    String keyWordsArray[] = {"begin", "end", "call", "procedure", "const", "var", 
	                      		"array", "while", "do", "repeat", "until", "for", "if", "then", "else", "read",
	                    		"write", "XOR", "OR", "AND", "NOT", "exit", "case", "of", "continue", "break"};
	    keyWords = new HashSet<String>(Arrays.asList(keyWordsArray));
	    StyleConstants.setForeground(compileResAttributes, HighlightBlue);
	    StyleConstants.setForeground(compileResAttributes2, DbWordColor);
	    
	    //Container panel0 = getContentPane();
	    // panel1(head 3*1), panel2(main part 1*4) up on panel0(bottom)
	    // panel3(three win part 3*2), panel4(three btn 3*1) up on panel2
	    JPanel panel0 = new JPanel();
	    panel0.setBackground(MediumBlue);
	    setContentPane(panel0);
	    
	    GridBagLayout panel0Layout = new GridBagLayout();
	    panel0Layout.rowHeights = new int[]{70, 0};
	    panel0Layout.rowWeights = new double[]{0.0, 1.0};
	    panel0Layout.columnWeights = new double[]{1.0};
	    panel0.setLayout(panel0Layout);
	    
	    JPanel panel1 = new JPanel();
	    panel1.setBackground(DarkBlue);
	    GridBagConstraints panel0Gbc = new GridBagConstraints();
	    panel0Gbc.fill = GridBagConstraints.BOTH;
	    panel0Gbc.gridx = 0;
	    panel0Gbc.gridy = 0;
	    panel0.add(panel1, panel0Gbc);
	    JPanel panel2 = new JPanel();
	    panel2.setBackground(MediumBlue);
	    panel0Gbc.gridy = 1;
	    panel0Gbc.insets = new Insets(0, 20, 15, 20);
	    panel0.add(panel2, panel0Gbc);
	    
	    GridBagLayout panel1Layout = new GridBagLayout();
	    panel1Layout.columnWidths = new int[]{90, 0, 170};
	    panel1Layout.columnWeights = new double[]{0.0, 1.0, 0.0};
	    panel1.setLayout(panel1Layout);
	    
	    JLabel label1 = new JLabel("File Path:");
	    label1.setForeground(DbWordColor);
	    label1.setFont(myFont);
	    JLabel label2 = new JLabel("Source Program");
	    label2.setForeground(MbWordColor);
	    label2.setFont(myFont);
	    JLabel label3 = new JLabel("Compling Result");
	    label3.setForeground(MbWordColor);
	    label3.setFont(myFont);
	    JLabel label4 = new JLabel("Stack");
	    label4.setForeground(MbWordColor);
	    label4.setFont(myFont);
	    JLabel label5 = new JLabel("Console");
	    label5.setForeground(MbWordColor);
	    label5.setFont(myFont);
	    
	    Dimension CafBtnDm = new Dimension(150, 40);
	    Dimension ThreeBtnDm = new Dimension(230, 40);
	    CafBtn = new JButton("Choose a File");
	    CafBtn.setPreferredSize(CafBtnDm);
	    CafBtn.setBackground(GreyBlue);
	    CafBtn.setBorder(null);
	    CafBtn.setOpaque(true);
	    CafBtn.setForeground(DbWordColor);
	    CafBtn.setFont(myFont);
	    CafBtn.addMouseListener(new MyMouseAdapter1());
	    
	    fileTextField = new JTextField();
	    Dimension fileTextFieldDm = new Dimension(150, 38);
	    fileTextField.setPreferredSize(fileTextFieldDm);
	    fileTextField.setForeground(GreyBlue);
	    fileTextField.setBackground(DbWordColor);
	    fileTextField.setBorder(null);
	    fileTextField.setFont(myFont);
	    
	    GridBagConstraints panel1Gbc = new GridBagConstraints();
	    panel1Gbc.anchor = GridBagConstraints.EAST;
	    panel1Gbc.gridx = 0;
	    panel1Gbc.gridy = 0;
	    panel1.add(label1, panel1Gbc);
	    panel1Gbc.gridx = 2;
	    panel1Gbc.anchor = GridBagConstraints.WEST;
	    panel1.add(CafBtn, panel1Gbc);
	    panel1Gbc.gridx = 1;
	    panel1Gbc.fill = GridBagConstraints.HORIZONTAL;
	    panel1Gbc.insets = new Insets(0, 10, 0, 10);
	    panel1.add(fileTextField, panel1Gbc);
	    
	    GridBagLayout panel2Layout = new GridBagLayout();
	    panel2Layout.rowHeights = new int[]{250, 60, 20, 0};
	    panel2Layout.columnWidths = new int[]{0};
	    panel2Layout.rowWeights = new double[]{0.6, 0.0, 0.0, 0.4};
	    panel2Layout.columnWeights = new double[]{1.0};
	    panel2.setLayout(panel2Layout);
	    
	    JPanel panel3 = new JPanel();
	    panel3.setBackground(MediumBlue);
	    
	    GridBagConstraints panel2Gbc = new GridBagConstraints();
	    panel2Gbc.gridy = 0;
	    panel2Gbc.fill = GridBagConstraints.BOTH;
	    panel2.add(panel3, panel2Gbc);
	    
	    GridBagLayout panel3Layout = new GridBagLayout();
	    panel3Layout.columnWidths = new int[]{200, 150, 0};
	    panel3Layout.rowHeights = new int[]{30, 0};
	    panel3Layout.columnWeights = new double[]{0.3, 0.3, 0.3};
	    panel3Layout.rowWeights = new double[]{0, 1.0};
	    panel3.setLayout(panel3Layout);
	    
	    sourceProTextPane = new JTextPane();
	    sourceProDoc = sourceProTextPane.getStyledDocument();
	    sourceProTextPane.setBackground(Blue1);
	    sourceProTextPane.setForeground(BtnWdColor);
	    sourceProTextPane.setFont(myFont);
	    sourceProTextPane.setEditable(false);
	    sourceProTextPane.setAutoscrolls(true);
	    sourceProTextPane.setMargin(new Insets(0, 5, 0, 0));
	    
	    compilResTextPane = new JTextPane();
	    compilResTextPane.setBackground(Blue2);
	    compilResTextPane.setForeground(BtnWdColor);
	    compilResTextPane.setFont(myFont);
	    compilResTextPane.setEditable(false);
	    compilResTextPane.setAutoscrolls(true);
	    compilResTextPane.setMargin(new Insets(0, 5, 0, 0));
	    
	    stackTextPane = new JTextPane();
	    stackTextPane.setBackground(Blue3);
	    stackTextPane.setForeground(BtnWdColor);
	    stackTextPane.setFont(myFont);
	    stackTextPane.setEditable(false);
	    stackTextPane.setAutoscrolls(true);
	    stackTextPane.setMargin(new Insets(0, 5, 0, 0));
	    
	    consoleTextPane = new JTextPane();
	    consoleTextPane.setBackground(DbWordColor);
	    consoleTextPane.setForeground(HighlightBlue);
	    consoleTextPane.setFont(myFont);
	    consoleTextPane.setEditable(false);
	    consoleTextPane.setAutoscrolls(true);
	    consoleTextPane.setMargin(new Insets(0, 5, 0, 0));
	    
	    sourceProScrollPane = new JScrollPane();
	    sourceProScrollPane.setViewportView(sourceProTextPane);
	    sourceProScrollPane.setBorder(null);
	    
	    compilResScrollPane = new JScrollPane();
	    compilResScrollPane.setViewportView(compilResTextPane);
	    compilResScrollPane.setBorder(null);
	    
	    stackScrollPane = new JScrollPane();
	    stackScrollPane.setViewportView(stackTextPane);
	    stackScrollPane.setBorder(null);
	    
	    consoleScrollPane = new JScrollPane();
	    consoleScrollPane.setViewportView(consoleTextPane);
	    consoleScrollPane.setBorder(null);
	    
	    GridBagConstraints panel3Gbc = new GridBagConstraints();
	    panel3Gbc.gridx = 0;
	    panel3Gbc.gridy = 0;
	    panel3Gbc.anchor	 = GridBagConstraints.SOUTHWEST;
	    panel3.add(label2, panel3Gbc);
	    panel3Gbc.insets = new Insets(0, 20, 0, 0);
	    panel3Gbc.gridx = 1;
	    panel3.add(label3, panel3Gbc);
	    panel3Gbc.insets = new Insets(0, 0, 0, 0);
	    panel3Gbc.gridx = 2;
	    panel3.add(label4, panel3Gbc);
	    panel3Gbc.gridx = 0;
	    panel3Gbc.gridy = 1;
	    panel3Gbc.fill = GridBagConstraints.BOTH;
	    panel3Gbc.insets = new Insets(5, 0, 0, 0);
	    panel3.add(sourceProScrollPane, panel3Gbc);
	    panel3Gbc.gridx = 1;
	    panel3Gbc.fill = GridBagConstraints.BOTH;
	    panel3Gbc.insets = new Insets(5, 20, 0, 20);
	    panel3.add(compilResScrollPane, panel3Gbc);
	    panel3Gbc.gridx = 2;
	    panel3Gbc.fill = GridBagConstraints.BOTH;
	    panel3Gbc.insets = new Insets(5, 0, 0, 0);
	    panel3.add(stackScrollPane, panel3Gbc);
	    
	    JPanel panel4 = new JPanel();
	    panel4.setBackground(MediumBlue);
	    panel2Gbc.gridy = 1;
	    panel2.add(panel4, panel2Gbc);
	    
	    compileBtn = new JButton("Compile");
	    runBtn = new JButton("Run");
	    runStepBtn = new JButton("Run Step by Step");
	    compileBtn.setFont(myFont);
	    runBtn.setFont(myFont);
	    runStepBtn.setFont(myFont);
	    compileBtn.setPreferredSize(ThreeBtnDm);
	    runBtn.setPreferredSize(ThreeBtnDm);
	    runStepBtn.setPreferredSize(ThreeBtnDm);
	    compileBtn.setOpaque(true);
	    runBtn.setOpaque(true);
	    runStepBtn.setOpaque(true);
	    compileBtn.setBorder(null);
	    runBtn.setBorder(null);
	    runStepBtn.setBorder(null);
	    compileBtn.setBackground(BtnColor);
	    runBtn.setBackground(BtnColor);
	    runStepBtn.setBackground(BtnColor);
	    compileBtn.setForeground(BtnWdColor);
	    runBtn.setForeground(BtnWdColor);
	    runStepBtn.setForeground(BtnWdColor);
	    compileBtn.addMouseListener(new MyMouseAdapter2());
	    runBtn.addMouseListener(new MyMouseAdapter2());
	    runStepBtn.addMouseListener(new MyMouseAdapter2());
	    
	    
	    GridBagLayout panel4Layout = new GridBagLayout();
	    panel4Layout.columnWeights = new double[]{0.3, 0.3, 0.3};
	    panel4Layout.rowWeights = new double[]{0.0};
	    panel4.setLayout(panel4Layout);
	    GridBagConstraints panel4Gbc = new GridBagConstraints();
	    panel4Gbc.gridx = 0;
	    panel4Gbc.gridy = 0;
	    panel4Gbc.anchor = GridBagConstraints.WEST;
	    panel4.add(compileBtn, panel4Gbc);
	    panel4Gbc.gridx = 1;
	    panel4Gbc.anchor = GridBagConstraints.CENTER;
	    panel4.add(runBtn, panel4Gbc);
	    panel4Gbc.gridx = 2;
	    panel4Gbc.anchor = GridBagConstraints.EAST;
	    panel4.add(runStepBtn, panel4Gbc);
	    
	    panel2Gbc.gridy = 2;
	    panel2Gbc.anchor = GridBagConstraints.SOUTHWEST;
	    panel2.add(label5, panel2Gbc);
	    panel2Gbc.gridy = 3;
	    panel2Gbc.insets = new Insets(5, 0, 0, 0);
	    panel2.add(consoleScrollPane, panel2Gbc);
	    
	    setLocationRelativeTo(null);
	    setVisible(true);  
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

