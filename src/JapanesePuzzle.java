/*
Japanese Puzzle Solver

Different solver techniques:
- simple exhaustive search [DONE]
- quite complex exhaustive search [DONE]
- very complex exhaustive search [-]
- 'swap random' randomized search [-]
- deduction search [DONE] <-- this is good enough!

Esa Junttila, 30.9.2007
*/

import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;


public class JapanesePuzzle {

	private static final int MAX_INPUT_FILE_SIZE = 10000000; // ten megabytes

	// ---------------------- //

	public static final String PUZZLE_STRING_SEPARATOR = " ";
	public static final char PUZZLE_STRING_SEPARATOR_CHAR = ' ';

	public static final char PUZZLE_STRING_ROW_SYMBOL_CHAR = '_';
	public static final char PUZZLE_STRING_COLUMN_SYMBOL_CHAR = '|';
	public static final String PUZZLE_STRING_ROW_SYMBOL_STRING = "" + PUZZLE_STRING_ROW_SYMBOL_CHAR;
	public static final String PUZZLE_STRING_COLUMN_SYMBOL_STRING = "" + PUZZLE_STRING_COLUMN_SYMBOL_CHAR;

	// ---------------------- //

	private static final int INPUT_TYPE_ROW_SYMBOL = 171;
	private static final int INPUT_TYPE_COLUMN_SYMBOL = 172;
	private static final int INPUT_TYPE_NUMBER = 173;

	//private static final int PUZZLE_STRING_MODE_ROW_COUNT = 170;
	//private static final int PUZZLE_STRING_MODE_COL_COUNT = 171;
	//private static final int PUZZLE_STRING_MODE_ROW_SEQUENCE_COUNT = 172;
	//private static final int PUZZLE_STRING_MODE_COL_SEQUENCE_COUNT = 173;
	//private static final int PUZZLE_STRING_MODE_ROW_SEQUENCE = 174;
	//private static final int PUZZLE_STRING_MODE_COL_SEQUENCE = 175;
	//private static final int PUZZLE_STRING_MODE_READY = 176;

	private static final int PUZZLE_NO_SEQUENCE = -1;

	private static final int PUZZLE_INITIAL_VALUE = 0;
	private static final int PUZZLE_TRUE = 1001;
	private static final int PUZZLE_FALSE = 1002;
	private static final int PUZZLE_TEMP_TRUE = 1003;
	private static final int PUZZLE_TEMP_FALSE = 1004;

	private static final char PUZZLE_MATRIX_TRUE_CHAR = '#';
	private static final char PUZZLE_MATRIX_FALSE_CHAR = '.';

	private static final String PUZZLE_MATRIX_TRUE_STRING = "" + PUZZLE_MATRIX_TRUE_CHAR;
	private static final String PUZZLE_MATRIX_FALSE_STRING = "" + PUZZLE_MATRIX_FALSE_CHAR;
	private static final String PUZZLE_MATRIX_INITIAL_VALUE_STRING = " ";

	private static int tokenIndex = 0;

	// ---------------------- //

	private int[][] rowSequences = null;
	private int[][] colSequences = null;
	private int[][] puzzleMatrix = null;


	// ---------------------- //

	public static void main(String[] args) {
		//String puzzle = "2 2 1 1 1 1 1 1 1 1";
		//String puzzle = "4 3 2 1 1 2 1 1 1 1 2 1 1 2 2 1 0 1 4";
		//JapanesePuzzle jp = JapanesePuzzle.createPuzzleFromSequences(puzzle);
/*
		int[][] sol = {
			{0,0,0,1,0,0,1,0,1,1},
			{0,0,1,1,0,1,0,0,0,1},
			{0,1,1,1,0,0,1,0,0,1},
			{1,1,1,0,0,0,0,1,0,1},
			{1,1,0,0,0,1,0,0,1,1},
			{1,0,0,1,0,1,0,0,1,1},
			{0,0,0,0,0,0,0,0,0,0},
			{1,1,1,1,0,1,1,1,1,1}
		};

		boolean[][] puzzleSolution = new boolean[sol.length][sol[0].length];
		for (int row = 0; row < sol.length; row++) {
			for (int col = 0; col < sol[0].length; col++) {
				puzzleSolution[row][col] = (sol[row][col] == 1 ? true : false);
			}
		}
*/
		BufferedInputStream bis = null;

		if (args.length == 0) {
			printUsage();
			System.err.println("..waiting for standard input. Stop with Ctrl+Z (Windows) or Ctrl-D (Linux).\n");
			bis = new BufferedInputStream(System.in);
		}
		else if (args.length == 1) {
			String filename = args[0];
			try {
				bis = new BufferedInputStream(new FileInputStream(filename));
			}
			catch (FileNotFoundException fnfe) {
				System.err.println("ERROR: " + fnfe.getMessage());
				System.exit(-1);
			}
		}
		else {
			printUsage();
		}

		//String seqInput = "40 30 4 9 3 1 1 4 4 1 5 3 6 4 1 2 5 2 3 5 4 1 1 3 5 8 4 1 2 2 2 1 1 1 5 4 1 1 2 5 8 4 1 2 1 1 1 1 1 5 5 1 1 5 5 7 1 2 5 1 1 3 1 8 1 2 1 2 3 1 1 4 8 1 2 2 2 1 1 2 1 8 1 2 1 4 1 1 1 3 8 1 1 4 1 1 1 1 3 8 1 2 1 5 1 1 1 3 7 1 1 6 1 1 1 3 6 1 1 8 2 1 2 6 1 8 1 2 1 2 4 8 3 1 2 7 5 4 4 1 1 1 1 7 6 3 4 1 1 1 1 5 3 1 4 1 4 6 1 6 1 5 1 3 4 10 4 1 6 6 7 3 4 1 2 4 6 6 2 1 4 3 5 4 10 4 2 6 5 5 4 4 2 6 6 2 2 2 2 2 6 4 5 2 2 6 5 3 2 2 3 6 5 3 4 2 3 5 5 3 5 1 3 3 4 6 1 3 2 3 7 2 3 3 4 1 4 3 2 1 4 2 5 2 2 4 3 3 1 1 2 1 5 5 8 3 5 1 3 8 8 1 1 1 3 5 1 3 4 8 9 1 2 8 8 1 1 1 2 5 1 3 6 1 10 2 5 1 2 8 1 1 1 1 1 4 1 2 5 1 4 2 1 3 10 1 1 1 1 1 1 2 4 1 2 6 16 2 2 4 1 2 5 3 3 2 1 2 4 4 4 1 2 4 5 3 1 1 3 1 7 4 4 1 13 3 1 4 1 13 5 2 6 7 1 10 9 5 1 7 3 1 4 8 12 2 1 7 2 1 5 2 6 2 1 7 1 3 3 3 1 1 3 6 2 5 3 1 1 5 5 1 3 1 7 2 3 1 1 18 4 1 9 8 4 4 1 3 2 1 3 2 7 8 5 6 4 1 1 8 5 3 1 4 6 10 2 15 11 6 3 1 1 1 7 10 2 16 9";
		//JapanesePuzzle jp = JapanesePuzzle.createPuzzleFromSequences(seqInput);
		//JapanesePuzzle jp = JapanesePuzzle.createPuzzleFromSolutionInput(bis);
		//JapanesePuzzle jp = JapanesePuzzle.createPuzzleFromSequences(bis);

		// CHOOSE input file type automatically:

		JapanesePuzzle jpSol = null;
		JapanesePuzzle jpSeq = null;
		Exception excSol = null;
		Exception excSeq = null;
		boolean inputSolOk = true;
		boolean inputSeqOk = true;

		bis.mark(MAX_INPUT_FILE_SIZE);

		// check input type 'solution'
		try { jpSol = JapanesePuzzle.createPuzzleFromSolutionInput(bis); }
		catch (Exception e) { excSol = e; inputSolOk = false; }

		// reset input back to start:
		try { bis.reset(); } catch (IOException ioe) {
			System.err.println("ERROR: " + ioe);
			System.exit(-1);
		}

		// check input type 'sequences'
		try { jpSeq = JapanesePuzzle.createPuzzleFromSequences(bis); }
		catch (Exception e) { excSeq = e; inputSeqOk = false; }

		// reset input back to start:
		try { bis.reset(); } catch (IOException ioe) {
			System.err.println("ERROR: " + ioe);
			System.exit(-1);
		}


		JapanesePuzzle jp = null;
		if (inputSolOk) jp = jpSol;
		else if (inputSeqOk) jp = jpSeq;
		else {
			System.err.println("ERROR: Illegal input!");
			System.err.println("Solution input error: " + excSol.getMessage());
			System.err.println("Sequence input error: " + excSeq.getMessage());
			System.exit(-1);
		}

		System.err.print("COMPUTING...");
		long startTime = System.currentTimeMillis();

		//ArrayList< int[][] > sol = jp.solveQuickExhaustive();
		ArrayList< int[][] > sol = jp.solveDeduction();

		long endTime = System.currentTimeMillis();
		System.err.print("...OK (" + (endTime - startTime) + " ms)\n");

		System.out.println();

		jp.printSolutions(sol);
	}

	public static void printUsage() {
		System.err.println(
			"usage: java JapanesePuzzle <puzzleFile>       (direct file)\n" +
			"   OR: java JapanesePuzzle < <puzzleInput>    (redirect file as input)\n" +
			"   OR: java JapanesePuzzle                    (type input)\n"
		);
	}


	public static JapanesePuzzle createPuzzleFromSolutionInput(InputStream is) throws IOException {
		ArrayList< ArrayList<Boolean> > matrix = new ArrayList< ArrayList<Boolean> >();
		boolean newRowStarts = true;
		int c;
		while ( (c = is.read()) >= 0) {
			if (newRowStarts) {
				if (c == PUZZLE_MATRIX_TRUE_CHAR ||
					c == PUZZLE_MATRIX_FALSE_CHAR) {
					matrix.add(new ArrayList<Boolean>());
					newRowStarts = false;
				}
			}

			if (c == PUZZLE_MATRIX_TRUE_CHAR) {
				matrix.get(matrix.size() - 1).add(true);
			}
			else if (c == PUZZLE_MATRIX_FALSE_CHAR) {
				matrix.get(matrix.size() - 1).add(false);
			}
			else if (c == '\n') {
				newRowStarts = true;
			}
			else if (c == ' ') {
				/* nothing */
			}
			else if (c == '\t') {
				/* nothing */
			}
			else if (c == '\r') { // carriage return
				/* nothing */
			}
			else throw new RuntimeException("ERROR: illegal input character: (" + ((char) c) + ")");
		}

		int rowCount = matrix.size();
		int colCount = -1;
		for (int row = 0; row < rowCount; row++) {
			if (colCount < 0) colCount = matrix.get(row).size();
			else if (matrix.get(row).size() != colCount) throw new RuntimeException("ERROR: uneven input matrix rows");
		}
		if (colCount < 0) throw new RuntimeException("ERROR: Empty input matrix");

		boolean[][] puzzleMatrix = new boolean[rowCount][colCount];
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				puzzleMatrix[row][col] = matrix.get(row).get(col);
			}
		}

		return createPuzzleFromSolution(puzzleMatrix);
	}


	public static JapanesePuzzle createPuzzleFromSolution(int[][] solution) {
		if (solution == null) throw new RuntimeException("ERROR: solution is null");
		if (solution.length == 0)  throw new RuntimeException("ERROR: solution has no rows (empty)!");

		boolean[][] puzzleSolution = new boolean[solution.length][solution[0].length];
		for (int row = 0; row < solution.length; row++) {
			for (int col = 0; col < solution[0].length; col++) {
				int value = solution[row][col];
				if (value == 0 || value == 1) {
					puzzleSolution[row][col] = (value == 1 ? true : false);
				}
				else throw new RuntimeException("ERROR: solution contains other values than 1 or 0.");
			}
		}

		return createPuzzleFromSolution(puzzleSolution);
	}


	public static JapanesePuzzle createPuzzleFromSolution(boolean[][] solution) {
		if (solution == null) throw new RuntimeException("ERROR: solution is null");
		if (solution.length == 0)  throw new RuntimeException("ERROR: solution has no rows (empty)!");

		ArrayList<Byte> sequences = new ArrayList<Byte>();

		int numRows = solution.length;
		int numCols = solution[0].length;

		// add row sequences
		for (int row = 0; row < numRows; row++) {
			sequences.add((byte) PUZZLE_STRING_ROW_SYMBOL_CHAR); // row symbol
			sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);  // separator

			int sequenceLengthCounter = 0;
			boolean insideSequence = false;
			for (int col = 0; col < numCols; col++) {
				if (solution[row][col]) {
					if (!insideSequence) {
						insideSequence = true;
					}
					sequenceLengthCounter++;
				}
				else { // solution[row][col] == false
					if (insideSequence) {
						// end of one sequence
						byte[] numBytes = new Integer(sequenceLengthCounter).toString().getBytes();

						for (int b = 0; b < numBytes.length; b++) {
							sequences.add(numBytes[b]);
						}
						sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);
						sequenceLengthCounter = 0;
						insideSequence = false;
					}
				}
			}
			if (insideSequence) {
				// end of last sequence on this row
				byte[] numBytes = new Integer(sequenceLengthCounter).toString().getBytes();

				for (int b = 0; b < numBytes.length; b++) {
					sequences.add(numBytes[b]);
				}
				sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);
				sequenceLengthCounter = 0;
				insideSequence = false;
			}
		}

		// add column sequences
		for (int col = 0; col < numCols; col++) {
			sequences.add((byte) PUZZLE_STRING_COLUMN_SYMBOL_CHAR); // row symbol
			sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);  // separator

			int sequenceLengthCounter = 0;
			boolean insideSequence = false;
			for (int row = 0; row < numRows; row++) {
				if (solution[row][col]) {
					if (!insideSequence) {
						insideSequence = true;
					}
					sequenceLengthCounter++;
				}
				else { // solution[row][col] == false
					if (insideSequence) {
						// end of one sequence
						byte[] numBytes = new Integer(sequenceLengthCounter).toString().getBytes();

						for (int b = 0; b < numBytes.length; b++) {
							sequences.add(numBytes[b]);
						}
						sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);
						sequenceLengthCounter = 0;
						insideSequence = false;
					}
				}
			}
			if (insideSequence) {
				// end of last sequence on this row
				byte[] numBytes = new Integer(sequenceLengthCounter).toString().getBytes();

				for (int b = 0; b < numBytes.length; b++) {
					sequences.add(numBytes[b]);
				}
				sequences.add((byte) PUZZLE_STRING_SEPARATOR_CHAR);
				sequenceLengthCounter = 0;
				insideSequence = false;
			}
		}

		byte[] sequenceBytes = new byte[sequences.size()];
		for (int i = 0; i < sequences.size(); i++) {
			sequenceBytes[i] = sequences.get(i);
		}
		String sequenceString = new String(sequenceBytes);

		return createPuzzleFromSequences(sequenceString);
	}




	public static JapanesePuzzle createPuzzleFromSequences(InputStream is) throws IOException {
		ArrayList<Character> numbers = new ArrayList<Character>();
		int c;
		boolean insideNumber = false;

		while ( (c = is.read()) >= 0) {
			if (Character.isDigit(c)) {
				numbers.add(new Character( (char) c ));
				insideNumber = true;
			}
			else {
				if (insideNumber) {
					numbers.add(PUZZLE_STRING_SEPARATOR_CHAR); // add a separator
					insideNumber = false;
				}

				if (c == PUZZLE_STRING_ROW_SYMBOL_CHAR ||
					c == PUZZLE_STRING_COLUMN_SYMBOL_CHAR) {

					numbers.add(new Character( (char) c ));
					numbers.add(PUZZLE_STRING_SEPARATOR_CHAR); // add a separator
				}
				else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
					/* nothing */
				}
				else throw new RuntimeException(
					"ERROR: sequence contains an illegal character: (" + (char) c + ")");
			}
		}

		char[] characters = new char[numbers.size()];
		for (int i = 0; i < numbers.size(); i++) {
			characters[i] = numbers.get(i);
		}
		String sequences = new String(characters);

		return createPuzzleFromSequences(sequences);
	}


	private static int sequenceSum(int[][] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				int val = array[i][j];
				sum += (val > 0 ? val : 0);
			}
		}
		return sum;
	}

	public static JapanesePuzzle createPuzzleFromSequences(String sequences) {
		// Sequence, all values in one string separated by ' '.
		// nr = number of rows
		// nc = number of columns
		// nrs = number of row sequences
		// ncs = number of column sequences
		// START -->
		// _
		// <rowSequence_1_1>
		// <rowSequence_1_2>
		// ...
		// <rowSequence_1_(nrs_1)>
		// _
		// <rowSequence_2_1>
		// <rowSequence_2_2>
		// ...
		// <rowSequence_2_(nrs_2)>
		// ...
		// _
		// <rowSequence_(nr)_1>
		// <rowSequence_(nr)_2>
		// ...
		// <rowSequence_(nr)_(nrs_nr)>
		// |
		// <columnSequence_1_1>
		// <columnSequence_1_2>
		// ...
		// <columnSequence_1_(ncs_1)>
		// |
		// <columnSequence_2_1>
		// <columnSequence_2_2>
		// ...
		// <columnSequence_2_(ncs_2)>
		// |
		// ...
		// |
		// <columnSequence_(nc)_1>
		// <columnSequence_(nc)_2>
		// ...
		// <columnSequence_(nc)_(ncs_nc)>
		//
		// <-- END
		//

		if (sequences == null) throw new NullPointerException("INTERNAL ERROR: Puzzle string is null");

		String[] tokens = sequences.split(PUZZLE_STRING_SEPARATOR);
		if (tokens.length < 2) {
			throw new RuntimeException(
				"ERROR: Invalid puzzle string: too few parameters");
		}

		ArrayList< ArrayList<Integer> > rowSequences = new ArrayList< ArrayList<Integer> >();
		ArrayList< ArrayList<Integer> > colSequences = new ArrayList< ArrayList<Integer> >();
		int tokenIndex = -1;
		boolean endOfRows = false;

		// process input:
		while (true) {
			// FIND OUT THE TYPE OF NEXT TOKEN
			tokenIndex++;
			if (tokenIndex >= tokens.length) break;

			String str = tokens[tokenIndex];
			int inputType;
			int inputValue = -1;
			if (str.equals(PUZZLE_STRING_ROW_SYMBOL_STRING)) {
				inputType = INPUT_TYPE_ROW_SYMBOL;
			}
			else if (str.equals(PUZZLE_STRING_COLUMN_SYMBOL_STRING)) {
				inputType = INPUT_TYPE_COLUMN_SYMBOL;
			}
			else {
				inputType = INPUT_TYPE_NUMBER;
				try { inputValue = Integer.parseInt(str); }
				catch(NumberFormatException nfe) { throw new RuntimeException(nfe); }
			}


			if (inputType == INPUT_TYPE_ROW_SYMBOL) {
				if (endOfRows) {
					throw new RuntimeException("ERROR: a row symbol among columns");
				}
				rowSequences.add(new ArrayList<Integer>());
			}
			else if (inputType == INPUT_TYPE_COLUMN_SYMBOL) {
				endOfRows = true;
				colSequences.add(new ArrayList<Integer>());
			}
			else if (inputType == INPUT_TYPE_NUMBER) {
				if (inputValue == 0) throw new RuntimeException(
					"ERROR: zero sequence length!");
				else if (inputValue < 0) throw new RuntimeException(
					"INTERNAL ERROR: negative sequence length!");

				if (endOfRows) {
					int lastColIndex = colSequences.size() - 1;
					if (lastColIndex < 0) {
						throw new RuntimeException("ERROR: Missing column symbol before sequences!");
					}
					colSequences.get(lastColIndex).add(inputValue);
				}
				else {
					int lastRowIndex = rowSequences.size() - 1;
					if (lastRowIndex < 0) {
						throw new RuntimeException("ERROR: Missing row symbol before sequences!");
					}
					rowSequences.get(lastRowIndex).add(inputValue);
				}
			}
			else {
				throw new RuntimeException("INTERNAL ERROR: illegal puzzle string token type.");
			}
		}


		// CONSTRUCT FINAL PUZZLE ARRAYS:
		int numRows = rowSequences.size();
		int numCols = colSequences.size();

		// maximum sequence counts:
		int maxRowSequenceCount = -1;
		for (int r = 0; r < numRows; r++) {
			int s = rowSequences.get(r).size();
			if (s > maxRowSequenceCount) maxRowSequenceCount = s;
		}
		int maxColSequenceCount = -1;
		for (int c = 0; c < numCols; c++) {
			int s = colSequences.get(c).size();
			if (s > maxColSequenceCount) maxColSequenceCount = s;
		}

		// copy sequence values:
		int[][] puzzleRowSequences = new int[numRows][maxRowSequenceCount];
		for (int r = 0; r < numRows; r++) {
			for (int s = 0; s < maxRowSequenceCount; s++) {
				int seqValue;
				if (s < rowSequences.get(r).size()) seqValue = rowSequences.get(r).get(s);
				else seqValue = PUZZLE_NO_SEQUENCE;
				puzzleRowSequences[r][s] = seqValue;
			}
		}
		int[][] puzzleColSequences = new int[numCols][maxColSequenceCount];
		for (int c = 0; c < numCols; c++) {
			for (int s = 0; s < maxColSequenceCount; s++) {
				int seqValue;
				if (s < colSequences.get(c).size()) seqValue = colSequences.get(c).get(s);
				else seqValue = PUZZLE_NO_SEQUENCE;
				puzzleColSequences[c][s] = seqValue;
			}
		}

		// check that row and column sums are equal
		int rowSums = sequenceSum(puzzleRowSequences);
		int colSums = sequenceSum(puzzleColSequences);
		if (rowSums != colSums) {
			throw new IllegalArgumentException(
				"Mismatch in row sums and column sums: " + rowSums + " vs " + colSums);
		}

		return new JapanesePuzzle(puzzleRowSequences, puzzleColSequences);
	}



/*
	public int[][] solve() {
		
	}
*/

	public String toString() {
		String str = "";

		int rowSeqMaxCount = this.rowSequences[0].length;
		String startIndention = "";
		for (int i = 0; i < rowSeqMaxCount; i++) {
			startIndention += "   ";
		}
		startIndention += "  "; // instead of a whitespace and a border character

		// column sequences
		int colSeqMaxCount = this.colSequences[0].length;
		for (int line = 0; line < colSeqMaxCount; line++) {
			String sequences = startIndention;
			for (int col = 0; col < this.colSequences.length; col++) {
				int seq = this.colSequences[col][line];
				if (seq == PUZZLE_NO_SEQUENCE) {
					sequences += "   ";	
				}
				else {
					if (seq < 10) sequences += " " + seq + " ";
					else if (seq < 100) sequences += " " + seq;
					else sequences += "" + seq;
				}
			}
			sequences += "\n";
			str += sequences;
		}

		// upper border
		String border = startIndention;
		for (int i = 0; i < this.colSequences.length; i++) border += "---";
		border += "\n";
		str += border;

		// row sequences and puzzle values
		for (int row = 0; row < this.rowSequences.length; row++) {
			String line = "";

			// row sequences:
			for (int seqIndex = 0; seqIndex < this.rowSequences[0].length; seqIndex++) {
				int seq = this.rowSequences[row][seqIndex];
				if (seq == PUZZLE_NO_SEQUENCE) {
					line += "   ";
				}
				else {
					if (seq < 10) line += "  " + seq;
					else if (seq < 100) line += " " + seq;
					else line += "" + seq;
				}
			}
			line += " |";

			// puzzle values:
			for (int col = 0; col < this.colSequences.length; col++) {
				String s;
				if (this.puzzleMatrix[row][col] == PUZZLE_INITIAL_VALUE)
					s = PUZZLE_MATRIX_INITIAL_VALUE_STRING;
				else if (this.puzzleMatrix[row][col] == PUZZLE_TRUE)
					s = PUZZLE_MATRIX_TRUE_STRING;
				else if (this.puzzleMatrix[row][col] == PUZZLE_FALSE)
					s = PUZZLE_MATRIX_FALSE_STRING;
				else throw new RuntimeException("ERROR: Internal error: invalid puzzleMatrix value");
				line += " " + s + " ";
			}
			line += "|\n";

			str += line;
		}

		// lower border
		str += border;

		return str;
	}


	public void printSolutions(ArrayList< int[][] > solutions) {
		if (solutions == null) {
			System.out.println("No solutions!");
			return;
		}

		for (int i = 0; i < solutions.size(); i++) {
			int[][] puzzle = solutions.get(i);
			this.puzzleMatrix = puzzle;
			System.out.println("Solution " + (i+1) +":");
			System.out.println(this + "\n");
		}
	}


	// ------------------ //

	private JapanesePuzzle() { /* do not call */ }


	private JapanesePuzzle(int[][] rowSequences, int[][] colSequences) {
		this.rowSequences = rowSequences;
		this.colSequences = colSequences;

		this.puzzleMatrix = new int[rowSequences.length][colSequences.length]; // default initial value 0
	}



	private static final int ILLEGAL_END_INDEX = -1;


//////////////////////////////
// EXHAUSTIVE SIMPLE SOLVER //
//////////////////////////////
	private ArrayList< int[][] > solveSimpleExhaustive() {
		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;

		int maxNumRowSeqs = this.rowSequences[0].length;
		int[][] rowSeqEndIndices = new int[numRows][maxNumRowSeqs];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < maxNumRowSeqs; j++) {
				rowSeqEndIndices[i][j] = ILLEGAL_END_INDEX;
			}
		}

		int[][] solution = new int[numRows][numCols]; // init: PUZZLE_INITIAL_VALUE
		ArrayList< int[][] > solutionList = this.solveSimpleExhaustiveRecursive(0, 0, solution, rowSeqEndIndices);

		return solutionList;
	}


	private ArrayList< int[][] > solveSimpleExhaustiveRecursive(int rowIndex, int rowSeqIndex, int[][] solution, int[][] rowSeqEndIndices) {
		ArrayList< int[][] > solutionList = null;

		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;
		int maxNumRowSeqs = this.rowSequences[0].length;

		// check end of recursion
		if (rowIndex >= numRows) {
			// puzzle ready!

			// row sequences are alright - now check column sequences
			boolean isValidSolution = simpleExhaustiveCheckColumnSequences(solution);
			if (isValidSolution) {
				int[][] solCopy = new int[numRows][numCols];
				for (int r = 0; r < numRows; r++) {
					for (int c = 0; c < numCols; c++) {
						solCopy[r][c] = solution[r][c];
					}
				}
				solutionList = new ArrayList< int[][] >();
				solutionList.add(solCopy);
			}
			return solutionList;
		}

		// check end of sequences on this row
		if (rowSeqIndex >= maxNumRowSeqs || this.rowSequences[rowIndex][rowSeqIndex] == PUZZLE_NO_SEQUENCE) {
			// add 'false's to the end of the row
			int falseStart; // first index to add 'false's to
			if (rowSeqIndex == 0) falseStart = 0;
			else falseStart = rowSeqEndIndices[rowIndex][rowSeqIndex - 1] + 2;
			for (int i = falseStart; i < numCols; i++) solution[rowIndex][i] = PUZZLE_FALSE;

			/* current row ready, call the first sequence on the next row */
			return this.solveSimpleExhaustiveRecursive(rowIndex + 1, 0, solution, rowSeqEndIndices);
		}

		int sequenceLength = this.rowSequences[rowIndex][rowSeqIndex];

		int maxEndPosition; // last index where this sequence may end
		int seqLengthSum = 0;
		for (int s = rowSeqIndex + 1; s < maxNumRowSeqs; s++) {
			if (this.rowSequences[rowIndex][s] == PUZZLE_NO_SEQUENCE) break;

			// sequence length plus mandatory preceding 'false'
			seqLengthSum += this.rowSequences[rowIndex][s] + 1;
		}
		maxEndPosition = numCols - seqLengthSum - 1;

		int minStartPosition; // from what index current sequence is starting from?
		if (rowSeqIndex == 0) minStartPosition = 0; // first sequence on the row
		else minStartPosition = rowSeqEndIndices[rowIndex][rowSeqIndex - 1] + 2;

		int minEndPosition = minStartPosition + sequenceLength - 1;

		// is there room for this sequence (and the others)?
		if (minEndPosition > maxEndPosition) {
			// no room!
			return null;
		}

		// browse through all possible placements
		for (int endPos = minEndPosition; endPos <= maxEndPosition; endPos++) {
			// undo previous placement of this sequence
			if (endPos != minEndPosition) { // not first placement?
				int undoIndex = endPos - sequenceLength;
				solution[rowIndex][undoIndex] = PUZZLE_FALSE;
			}

			// add sequence (new placement):
			for (int i = endPos; i > endPos - sequenceLength; i--) {
				solution[rowIndex][i] = PUZZLE_TRUE;
			}
			// add a succeeding 'false', if not yet at the end of the row.
			if (endPos < numCols - 1) solution[rowIndex][endPos + 1] = PUZZLE_FALSE;

			// update ending index for this sequence
			rowSeqEndIndices[rowIndex][rowSeqIndex] = endPos;

			// recursive call: next sequence on this row
			ArrayList< int[][] > list = this.solveSimpleExhaustiveRecursive(rowIndex, rowSeqIndex + 1, solution, rowSeqEndIndices);
			if (list != null) {
				if (solutionList == null) solutionList = new ArrayList< int[][] >();
				solutionList.addAll(list);
			}
		}
		return solutionList;
	}


	private boolean simpleExhaustiveCheckColumnSequences(int[][] solution) {
		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;

		int maxColumnSequences = this.colSequences[0].length;

		for (int col = 0; col < numCols ; col++) { // for all columns
			int rowIndex = 0;
			boolean browseToEndOfColumn = false;
			for (int seqIndex = 0; seqIndex < maxColumnSequences; seqIndex++) {
				int seqLength = this.colSequences[col][seqIndex];

				if (seqLength == PUZZLE_NO_SEQUENCE) break; // break inner 'for'

				if (rowIndex >= numRows) return false; // missing sequence

				int seqLengthCounter = 0;
				// browse through 'false's
				while (solution[rowIndex][col] == PUZZLE_FALSE) {
					rowIndex++;
					if (rowIndex >= numRows) return false; // missing sequence
				}
				while (solution[rowIndex][col] == PUZZLE_TRUE) {
					seqLengthCounter++;
					rowIndex++;
					if (rowIndex >= numRows) break; // break this 'while' loop
				}
				if (seqLengthCounter != seqLength) return false; // sequence is of wrong size
			}
			// browse to the end of column
			while (rowIndex < numRows) {
				if (solution[rowIndex][col] == PUZZLE_TRUE) return false; // extra sequence
				rowIndex++;
			}
		}
		return true;
	}



/////////////////////////////
// EXHAUSTIVE QUICK SOLVER //
/////////////////////////////


	private ArrayList< int[][] > solveQuickExhaustive() {
		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;

		int maxNumRowSeqs = this.rowSequences[0].length;
		int[][] rowSeqEndIndices = new int[numRows][maxNumRowSeqs];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < maxNumRowSeqs; j++) {
				rowSeqEndIndices[i][j] = ILLEGAL_END_INDEX;
			}
		}

		int[][] solution = new int[numRows][numCols]; // init: PUZZLE_INITIAL_VALUE
		ArrayList< int[][] > solutionList = this.solveQuickExhaustiveRecursive(0, 0, solution, rowSeqEndIndices);

		return solutionList;
	}


	private ArrayList< int[][] > solveQuickExhaustiveRecursive(int rowIndex, int rowSeqIndex, int[][] solution, int[][] rowSeqEndIndices) {
		ArrayList< int[][] > solutionList = null;

		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;
		int maxNumRowSeqs = this.rowSequences[0].length;

		// check end of recursion
		if (rowIndex >= numRows) {
			// puzzle ready!
			// row sequences are alright - now check column sequences
			boolean isValidSolution = simpleExhaustiveCheckColumnSequences(solution);
			if (isValidSolution) {
				int[][] solCopy = new int[numRows][numCols];
				for (int r = 0; r < numRows; r++) {
					for (int c = 0; c < numCols; c++) {
						solCopy[r][c] = solution[r][c];
					}
				}
				solutionList = new ArrayList< int[][] >();
				solutionList.add(solCopy);
			}
			else {
				this.puzzleMatrix = solution; System.err.println("Invalid solution:\n"+this);
				throw new RuntimeException("ERROR: internal error, quick exhausive solver produced an invalid solution.");
			}

			return solutionList;
		}

		// check end of sequences on this row
		if (rowSeqIndex >= maxNumRowSeqs || this.rowSequences[rowIndex][rowSeqIndex] == PUZZLE_NO_SEQUENCE) {
			// add 'false's to the end of the row
			int falseStart; // first index to add 'false's to
			if (rowSeqIndex == 0) falseStart = 0;
			else falseStart = rowSeqEndIndices[rowIndex][rowSeqIndex - 1] + 2;

			for (int i = falseStart; i < numCols; i++) {
				solution[rowIndex][i] = PUZZLE_FALSE;
				if ( !this.quickExhaustiveCheckColumn(rowIndex, i, solution) ) return null;
			}

			/* current row ready, call the first sequence on the next row */
			return this.solveQuickExhaustiveRecursive(rowIndex + 1, 0, solution, rowSeqEndIndices);
		}

		int sequenceLength = this.rowSequences[rowIndex][rowSeqIndex];

		int maxEndPosition; // last index where this sequence may end
		int seqLengthSum = 0;
		for (int s = rowSeqIndex + 1; s < maxNumRowSeqs; s++) {
			if (this.rowSequences[rowIndex][s] == PUZZLE_NO_SEQUENCE) break;

			// sequence length plus mandatory preceding 'false'
			seqLengthSum += this.rowSequences[rowIndex][s] + 1;
		}
		maxEndPosition = numCols - seqLengthSum - 1;

		int minStartPosition; // from what index current sequence is starting from?
		if (rowSeqIndex == 0) minStartPosition = 0; // first sequence on the row
		else minStartPosition = rowSeqEndIndices[rowIndex][rowSeqIndex - 1] + 2;

		int minEndPosition = minStartPosition + sequenceLength - 1;

		// is there room for this sequence (and the others)?
		if (minEndPosition > maxEndPosition) {
			// no room!
			return null;
		}

		// browse through all possible placements
		outer: // label for 'for' loop
		for (int endPos = minEndPosition; endPos <= maxEndPosition; endPos++) {
			int startPos = endPos - sequenceLength + 1;

			// undo previous placement of this sequence
			if (endPos != minEndPosition) { // not first placement?
				// Undo the first 'true' of PRECEDING sequence position.
				// It is the item just before first 'true' of current sequence position.
				int undoIndex = startPos - 1; 
				solution[rowIndex][undoIndex] = PUZZLE_FALSE;
				if ( !this.quickExhaustiveCheckColumn(rowIndex, undoIndex, solution) ) {
					// It is impossible to have 'false' in (rowIndex, undoIndex).
					// Further position of this sequence won't help.
					break outer;
				}
			}

			// add sequence (new placement):
			for (int i = startPos; i <= endPos ; i++) {
				solution[rowIndex][i] = PUZZLE_TRUE;
				if ( !this.quickExhaustiveCheckColumn(rowIndex, i, solution) ) {
					// It is impossible to have 'true' now in (rowIndex, i).
					// Skip a few impossible sequence positions.
					int nextPossibleEndPos = i + sequenceLength;
					for (int j = startPos; j <= i - 1; j++) {
						solution[rowIndex][j] = PUZZLE_FALSE;
						// Is it impossible to have 'false' now in (rowIndex, undoIndex)?
						// In that case further position of this sequence won't help.
						if ( !this.quickExhaustiveCheckColumn(rowIndex, j, solution) ) break outer;
					}
					endPos = nextPossibleEndPos - 1; // minus one to counter subsequent "++"
					continue outer;
				}
			}
			// add a succeeding 'false', if not yet at the end of the row.
			if (endPos < numCols - 1) {
				solution[rowIndex][endPos + 1] = PUZZLE_FALSE;
				if ( !this.quickExhaustiveCheckColumn(rowIndex, endPos + 1, solution) ) continue outer;
			}

			// update ending index for this sequence (new ending index)
			rowSeqEndIndices[rowIndex][rowSeqIndex] = endPos;

			// recursive call: next sequence on this row
			ArrayList< int[][] > list = this.solveQuickExhaustiveRecursive(rowIndex, rowSeqIndex + 1, solution, rowSeqEndIndices);
			if (list != null) {
				if (solutionList == null) solutionList = new ArrayList< int[][] >();
				solutionList.addAll(list);
			}
		}
		return solutionList;
	}


	private boolean quickExhaustiveCheckColumn(int rowIndex, int colIndex, int[][] solution) {
		// check column <colIndex> with current values

		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;
		int maxNumColSeqs = this.colSequences[0].length;


		int nextColSeqIndex = 0; // first column sequence
		int currentLength = 0;

		// check values on rows 0...<rowIndex>:
		for (int r = 0; r <= rowIndex; r++) {
			if (solution[r][colIndex] == PUZZLE_TRUE) {
				if (nextColSeqIndex >= maxNumColSeqs || this.colSequences[colIndex][nextColSeqIndex] == PUZZLE_NO_SEQUENCE) {
					return false; // extraneous sequence
				}

				currentLength++;
				if (currentLength > this.colSequences[colIndex][nextColSeqIndex]) {
					return false; // too long sequence
				}

			}
			else if (solution[r][colIndex] == PUZZLE_FALSE) {
				if (nextColSeqIndex < maxNumColSeqs) {
					if (this.colSequences[colIndex][nextColSeqIndex] != PUZZLE_NO_SEQUENCE) {
						if (currentLength == this.colSequences[colIndex][nextColSeqIndex]) {
							// a sequence finished
							nextColSeqIndex++;
							currentLength = 0;
						}
						else if (currentLength > 0 && currentLength < this.colSequences[colIndex][nextColSeqIndex]) {
							return false; // too short sequence
						}
					}
				}
			}
			else throw new RuntimeException("ERROR: internal error: quick exhaustive solver:"+
				"illegal solution value ("+solution[r][colIndex]+")");
		}


		// check whether further sequences fit on rows <rowIndex>+1...<numRows>:

		// all sequences done?
		if (nextColSeqIndex >= maxNumColSeqs ||
			this.colSequences[colIndex][nextColSeqIndex] == PUZZLE_NO_SEQUENCE) {
			return true;
		}

		int extraItemsNeeded = 0;
		// current sequence
		extraItemsNeeded +=  this.colSequences[colIndex][nextColSeqIndex] - currentLength; // more 'trues' needed?
		nextColSeqIndex++;

		while (nextColSeqIndex < maxNumColSeqs &&
			this.colSequences[colIndex][nextColSeqIndex] != PUZZLE_NO_SEQUENCE) {
			extraItemsNeeded += 1 + this.colSequences[colIndex][nextColSeqIndex]; // sequence plus preceding 'false'
			nextColSeqIndex++;
		}

		if (extraItemsNeeded >= numRows - rowIndex) {
			return false; // further sequences can't fit on the column!
		}

		return true;
	}



//////////////////////
// DEDUCTION SOLVER //
//////////////////////

	private int deductedItems = 0;

	private ArrayList< int[][] > solveDeduction() {
		final int numRows = this.rowSequences.length;
		final int numCols = this.colSequences.length;

		int[][] matrix = new int[numRows][numCols];
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				matrix[r][c] = PUZZLE_INITIAL_VALUE;
			}
		}

		boolean[] checkNeedRows = new boolean[numRows];
		boolean[] checkNeedCols = new boolean[numCols];
		for (int r = 0; r < numRows; r++) checkNeedRows[r] = true;
		for (int c = 0; c < numCols; c++) checkNeedCols[c] = true;

		this.deductedItems = 0;
		int prevDeductedItems = -1;
		boolean deductionReady = false;
		while ( !deductionReady ) {
			deductionReady = true;

			if (this.deductedItems > prevDeductedItems) {
				prevDeductedItems = this.deductedItems;
				System.err.println("Deducted: " + this.deductedItems + "/" + (numRows * numCols));
			}

			//System.out.println("Check need on rows:");
			//for (int r = 0; r < numRows; r++) {
			//	String str = (checkNeedRows[r] ? "#" : "." );
			//	System.out.print(str);
			//}
			//System.out.println();
			//System.out.println("Check need on columns:");
			//for (int c = 0; c < numCols; c++) {
			//	String str = (checkNeedCols[c] ? "#" : "." );
			//	System.out.print(str);
			//}
			//System.out.println();


			// all rows:
			for (int r = 0; r < numRows; r++) {
				System.err.print("_");
				//this.puzzleMatrix = matrix;
				//System.out.println(this);
				//System.out.println("Checking row "+(r+1));
				if (checkNeedRows[r]) {
					deductionReady = false;
					this.deductRow(r, matrix, checkNeedCols);
					checkNeedRows[r] = false;
				}
			}

			// all columns:
			for (int c = 0; c < numCols; c++) {
				System.err.print("|");
				//this.puzzleMatrix = matrix;
				//System.out.println(this);
				//System.out.println("Checking column "+(c+1));
				if (checkNeedCols[c]) {
					deductionReady = false;
					this.deductColumn(c, matrix, checkNeedRows);
					checkNeedCols[c] = false;
				}
			}
		}

		// check whether all values are legal:
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if (matrix[r][c] != PUZZLE_TRUE &&
					matrix[r][c] != PUZZLE_FALSE) {
					return null;
				}
			}
		}

		ArrayList< int[][] > solution = new ArrayList< int[][] >();
		solution.add(matrix);

		return solution;
	}


	private int[] rowItemTrueCounts = null;
	private int[] colItemTrueCounts = null;
	private int configurationCount = 0;


	private void deductRow(int rowIndex, int[][] matrix, boolean[] checkNeedCols) {
		final int numRows = this.rowSequences.length;
		final int numCols = this.colSequences.length;
		int maxNumRowSeqs = this.rowSequences[rowIndex].length;

		int[] rowItemValues = new int[numCols];
		for (int c = 0; c < numCols; c++) {
			rowItemValues[c] = PUZZLE_INITIAL_VALUE;
		}
		this.rowItemTrueCounts = new int[numCols]; // init: zeros
		this.configurationCount = 0;

		int[] rowSeqEndIndices = new int[maxNumRowSeqs];
		for (int j = 0; j < maxNumRowSeqs; j++) {
			rowSeqEndIndices[j] = ILLEGAL_END_INDEX;
		}

		this.deductRowRecursive(rowIndex, 0, rowItemValues, matrix, rowSeqEndIndices);

		if (configurationCount == 0) throw new RuntimeException("INTERNAL ERROR: configurations == 0");

		for (int c = 0; c < numCols; c++) {
			if (this.rowItemTrueCounts[c] == 0) {
				// it's impossible to legally place a 'true' --> 'false' is certain
				if (matrix[rowIndex][c] != PUZZLE_FALSE) {
					checkNeedCols[c] = true;
					matrix[rowIndex][c] = PUZZLE_FALSE;
					this.deductedItems++;
				}
			}
			else if (this.rowItemTrueCounts[c] == this.configurationCount) {
				// it's impossible to legally place a 'false' --> 'true' is certain
				if (matrix[rowIndex][c] != PUZZLE_TRUE) {
					checkNeedCols[c] = true;
					matrix[rowIndex][c] = PUZZLE_TRUE;
					this.deductedItems++;
				}
			}
		}
	}


	private void deductColumn(int colIndex, int[][] matrix, boolean[] checkNeedRows) {
		final int numRows = this.rowSequences.length;
		final int numCols = this.colSequences.length;
		int maxNumColSeqs = this.colSequences[colIndex].length;

		int[] colItemValues = new int[numRows];
		for (int r = 0; r < numRows; r++) {
			colItemValues[r] = PUZZLE_INITIAL_VALUE;
		}
		this.colItemTrueCounts = new int[numRows]; // init: zeros
		this.configurationCount = 0;

		int[] colSeqEndIndices = new int[maxNumColSeqs];
		for (int j = 0; j < maxNumColSeqs; j++) {
			colSeqEndIndices[j] = ILLEGAL_END_INDEX;
		}

		this.deductColRecursive(colIndex, 0, colItemValues, matrix, colSeqEndIndices);

		if (configurationCount == 0) throw new RuntimeException("INTERNAL ERROR: configurations == 0");

		for (int r = 0; r < numRows; r++) {
			if (this.colItemTrueCounts[r] == 0) {
				// it's impossible to legally place a 'true' --> 'false' is certain
				if (matrix[r][colIndex] != PUZZLE_FALSE) {
					checkNeedRows[r] = true;
					matrix[r][colIndex] = PUZZLE_FALSE;
					this.deductedItems++;
				}
			}
			else if (this.colItemTrueCounts[r] == this.configurationCount) {
				// it's impossible to legally place a 'false' --> 'true' is certain
				if (matrix[r][colIndex] != PUZZLE_TRUE) {
					checkNeedRows[r] = true;
					matrix[r][colIndex] = PUZZLE_TRUE;
					this.deductedItems++;
				}
			}
		}
	}


	private void deductRowRecursive(int rowIndex, int rowSeqIndex, int[] rowItemValues, int[][] matrix, int[] rowSeqEndIndices) {
		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;
		int maxNumRowSeqs = this.rowSequences[rowIndex].length;

		// check end of sequences on this row
		if (rowSeqIndex >= maxNumRowSeqs || this.rowSequences[rowIndex][rowSeqIndex] == PUZZLE_NO_SEQUENCE) {
			// add 'false's to the end of the row
			int falseStart; // first index to add 'false's to
			if (rowSeqIndex == 0) falseStart = 0;
			else falseStart = rowSeqEndIndices[rowSeqIndex - 1] + 2;

			for (int i = falseStart; i < numCols; i++) {
				if (matrix[rowIndex][i] == PUZZLE_TRUE) return;
				else rowItemValues[i] = PUZZLE_TEMP_FALSE;
			}

			// current row ready
			this.configurationCount++;
			for (int c = 0; c < numCols; c++) {
				if (rowItemValues[c] == PUZZLE_TEMP_TRUE) {
					this.rowItemTrueCounts[c]++;
				}
				else if (rowItemValues[c] == PUZZLE_TEMP_FALSE) {
					// do nothing
				}
				else throw new RuntimeException("INTERNAL ERROR: illegal rowItem value");
			}
			return;
		}

		int sequenceLength = this.rowSequences[rowIndex][rowSeqIndex];

		int maxEndPosition; // last index where this sequence may end
		int seqLengthSum = 0;
		for (int s = rowSeqIndex + 1; s < maxNumRowSeqs; s++) {
			if (this.rowSequences[rowIndex][s] == PUZZLE_NO_SEQUENCE) break;

			// sequence length plus mandatory preceding 'false'
			seqLengthSum += this.rowSequences[rowIndex][s] + 1;
		}
		maxEndPosition = numCols - seqLengthSum - 1;

		int minStartPosition; // from what index current sequence is starting from?
		if (rowSeqIndex == 0) minStartPosition = 0; // first sequence on the row
		else minStartPosition = rowSeqEndIndices[rowSeqIndex - 1] + 2;

		int minEndPosition = minStartPosition + sequenceLength - 1;

		// is there room for this sequence (and the others)?
		if (minEndPosition > maxEndPosition) {
			// no room!
			throw new RuntimeException("ERROR: No space for all sequences!");
		}

		// browse through all possible placements
		outer: // label for 'for' loop
		for (int endPos = minEndPosition; endPos <= maxEndPosition; endPos++) {
			int startPos = endPos - sequenceLength + 1;

			// undo previous placement of this sequence
			if (endPos != minEndPosition) { // not first placement?
				// Undo the first 'true' of PRECEDING sequence position.
				// It is the item just before first 'true' of current sequence position.
				int undoIndex = startPos - 1; 

				if (matrix[rowIndex][undoIndex] == PUZZLE_TRUE) break outer;
				else rowItemValues[undoIndex] = PUZZLE_TEMP_FALSE;
			}

			// add sequence (new placement):
			for (int i = startPos; i <= endPos ; i++) {
				if (matrix[rowIndex][i] == PUZZLE_FALSE) {
					// Optimization starts -->
					// It is impossible to have 'true' now in (rowIndex, i).
					// Skip a few impossible sequence positions.
					int nextPossibleEndPos = i + sequenceLength;
					for (int j = startPos; j <= i - 1; j++) {
						if (matrix[rowIndex][j] == PUZZLE_TRUE) {
							// It is impossible to have 'false' now in (rowIndex, j).
							// Further position of this sequence won't help.
							break outer;
						}
						else rowItemValues[j] = PUZZLE_TEMP_FALSE;
					}
					endPos = nextPossibleEndPos - 1; // minus one to counter subsequent "++" in 'for' loop
					// <-- Optimization ends
					continue outer;
				}
				else rowItemValues[i] = PUZZLE_TEMP_TRUE;
			}
			// add a succeeding 'false', if not yet at the end of the row.
			if (endPos < numCols - 1) {
				if (matrix[rowIndex][endPos + 1] == PUZZLE_TRUE) continue outer;
				else rowItemValues[endPos + 1] = PUZZLE_TEMP_FALSE;
			}

			// update ending index for this sequence (new ending index)
			rowSeqEndIndices[rowSeqIndex] = endPos;

			// recursive call:
			this.deductRowRecursive(rowIndex, rowSeqIndex + 1, rowItemValues, matrix, rowSeqEndIndices);
		}
	}



	private void deductColRecursive(int colIndex, int colSeqIndex, int[] colItemValues, int[][] matrix, int[] colSeqEndIndices) {
		int numRows = this.rowSequences.length;
		int numCols = this.colSequences.length;
		int maxNumColSeqs = this.colSequences[colIndex].length;

		// check end of sequences on this column
		if (colSeqIndex >= maxNumColSeqs || this.colSequences[colIndex][colSeqIndex] == PUZZLE_NO_SEQUENCE) {
			// add 'false's to the end of the column
			int falseStart; // first index to add 'false's to
			if (colSeqIndex == 0) falseStart = 0;
			else falseStart = colSeqEndIndices[colSeqIndex - 1] + 2;

			for (int i = falseStart; i < numRows; i++) {
				if (matrix[i][colIndex] == PUZZLE_TRUE) return;
				else colItemValues[i] = PUZZLE_TEMP_FALSE;
			}

			// current column ready
			this.configurationCount++;
			for (int r = 0; r < numRows; r++) {
				if (colItemValues[r] == PUZZLE_TEMP_TRUE) {
					this.colItemTrueCounts[r]++;
				}
				else if (colItemValues[r] == PUZZLE_TEMP_FALSE) {
					// do nothing
				}
				else throw new RuntimeException("INTERNAL ERROR: illegal colItem value");
			}
			return;
		}

		int sequenceLength = this.colSequences[colIndex][colSeqIndex];

		int maxEndPosition; // last index where this sequence may end
		int seqLengthSum = 0;
		for (int s = colSeqIndex + 1; s < maxNumColSeqs; s++) {
			if (this.colSequences[colIndex][s] == PUZZLE_NO_SEQUENCE) break;

			// sequence length plus mandatory preceding 'false'
			seqLengthSum += this.colSequences[colIndex][s] + 1;
		}
		maxEndPosition = numRows - seqLengthSum - 1;

		int minStartPosition; // from what index current sequence is starting from?
		if (colSeqIndex == 0) minStartPosition = 0; // first sequence on the column
		else minStartPosition = colSeqEndIndices[colSeqIndex - 1] + 2;

		int minEndPosition = minStartPosition + sequenceLength - 1;

		// is there room for this sequence (and the others)?
		if (minEndPosition > maxEndPosition) {
			// no room!
			throw new RuntimeException("ERROR: No space for all sequences!");
		}

		// browse through all possible placements
		outer: // label for 'for' loop
		for (int endPos = minEndPosition; endPos <= maxEndPosition; endPos++) {
			int startPos = endPos - sequenceLength + 1;

			// undo previous placement of this sequence
			if (endPos != minEndPosition) { // not first placement?
				// Undo the first 'true' of PRECEDING sequence position.
				// It is the item just before first 'true' of current sequence position.
				int undoIndex = startPos - 1; 

				if (matrix[undoIndex][colIndex] == PUZZLE_TRUE) break outer;
				else colItemValues[undoIndex] = PUZZLE_TEMP_FALSE;
			}

			// add sequence (new placement):
			for (int i = startPos; i <= endPos ; i++) {
				if (matrix[i][colIndex] == PUZZLE_FALSE) {
					// Optimization starts -->
					// It is impossible to have 'true' now in (i, colIndex).
					// Skip a few impossible sequence positions.
					int nextPossibleEndPos = i + sequenceLength;
					for (int j = startPos; j <= i - 1; j++) {
						if (matrix[j][colIndex] == PUZZLE_TRUE) {
							// It is impossible to have 'false' now in (j, colIndex).
							// Further position of this sequence won't help.
							break outer;
						}
						else colItemValues[j] = PUZZLE_TEMP_FALSE;
					}
					endPos = nextPossibleEndPos - 1; // minus one to counter subsequent "++" in 'for' loop
					// <-- Optimization ends
					continue outer;
				}
				else colItemValues[i] = PUZZLE_TEMP_TRUE;
			}
			// add a succeeding 'false', if not yet at the end of the column.
			if (endPos < numRows - 1) {
				if (matrix[endPos + 1][colIndex] == PUZZLE_TRUE) continue outer;
				else colItemValues[endPos + 1] = PUZZLE_TEMP_FALSE;
			}

			// update ending index for this sequence (new ending index)
			colSeqEndIndices[colSeqIndex] = endPos;

			// recursive call:
			this.deductColRecursive(colIndex, colSeqIndex + 1, colItemValues, matrix, colSeqEndIndices);
		}
	}
}















/*
	public static JapanesePuzzle createPuzzleFromSequences(InputStream is) throws IOException {
		ArrayList<Character> numbers = new ArrayList<Character>();
		int c;
		boolean separatorAdded = true;
		while ( (c = is.read()) >= 0) {
			if ( !separatorAdded ) {
				numbers.add(PUZZLE_STRING_SEPARATOR_CHAR);
				separatorAdded = true;
			}

			if (Character.isDigit(c)) {
				numbers.add(new Character( (char) c ));
			}
			else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
				separatorAdded = false;
			}
			else {
				throw new RuntimeException(
					"ERROR: sequence contains non-digit non-whitespace characters: (" + (char) c + ")");
			}
		}

		char[] characters = new char[numbers.size()];
		for (int i = 0; i < numbers.size(); i++) {
			characters[i] = numbers.get(i);
		}
		String sequences = new String(characters);

		return createPuzzleFromSequences(sequences);
	}



	public static JapanesePuzzle createPuzzleFromSequences(String sequences) {
		// Sequence, all values in one string separated by ' '.
		// 
		// <numRows = nr>
		// <numColumns = nc>
		//   <numRowSequences 1 = nrs_1>
		//     <rowSequence_1_1>
		//     <rowSequence_1_2>
		//     ...
		//     <rowSequence_1_(nrs_1)>
		//   <numRowSequences_2 = nrs_2>
		//     <rowSequence_2_1>
		//     <rowSequence_2_2>
		//     ...
		//     <rowSequence_2_(nrs_2)>
		//   ...
		//   <numRowSequences_(nr) = nrs_nr>
		//     <rowSequence_(nr)_1>
		//     <rowSequence_(nr)_2>
		//     ...
		//     <rowSequence_(nr)_(nrs_nr)>
		//   <numColumnSequences 1 = ncs_1>
		//     <columnSequence_1_1>
		//     <columnSequence_1_2>
		//     ...
		//     <columnSequence_1_(ncs_1)>
		//   <numColumnSequences_2 = ncs_2>
		//     <columnSequence_2_1>
		//     <columnSequence_2_2>
		//     ...
		//     <columnSequence_2_(ncs_2)>
		//   ...
		//   <numColumnSequences_(nr) = ncs_nc>
		//     <columnSequence_(nc)_1>
		//     <columnSequence_(nc)_2>
		//     ...
		//     <columnSequence_(nc)_(ncs_nc)>
		//
		//
		// Alternative string representation: (not implemented)
		// Sequence, all values in one string separated by ' '.
		// 
		// -
		// <rowSequence_1_1>
		// <rowSequence_1_2>
		// ...
		// <rowSequence_1_(nrs_1)>
		// -
		// <rowSequence_2_1>
		// <rowSequence_2_2>
		// ...
		// <rowSequence_2_(nrs_2)>
		// ...
		// -
		// <rowSequence_(nr)_1>
		// <rowSequence_(nr)_2>
		// ...
		// <rowSequence_(nr)_(nrs_nr)>
		// |
		// <columnSequence_1_1>
		// <columnSequence_1_2>
		// ...
		// <columnSequence_1_(ncs_1)>
		// |
		// <columnSequence_2_1>
		// <columnSequence_2_2>
		// ...
		// <columnSequence_2_(ncs_2)>
		// |
		// ...
		// |
		// <columnSequence_(nc)_1>
		// <columnSequence_(nc)_2>
		// ...
		// <columnSequence_(nc)_(ncs_nc)>
		//
		//

		if (sequences == null) throw new NullPointerException("Puzzle string");

		String[] tokens = sequences.split(PUZZLE_STRING_SEPARATOR);
		if (tokens.length < 2) {
			throw new RuntimeException(
				"ERROR: Invalid puzzle string: too few parameters (or wrong separator character)");
		}

		int numRows = -1;
		int numCols = -1;
		ArrayList< ArrayList<Integer> > rowSequences = new ArrayList< ArrayList<Integer> >();
		ArrayList< ArrayList<Integer> > colSequences = new ArrayList< ArrayList<Integer> >();
		int rowCounter = -1;
		int colCounter = -1;
		int sequenceCounter = -1;
		int mode = PUZZLE_STRING_MODE_ROW_COUNT; // starting state

		// process input:
		// FINITE STATE MACHINE:
		// STARTING STATE: '..._ROW_COUNT'
		// VALID ENDING STATE: '..._READY'
		tokenIndex = 0;
		while (true) {
			if (mode == PUZZLE_STRING_MODE_ROW_COUNT) {
				// read the number of rows the puzzle contains
				int value = nextTokenValue(tokens);
				if (value < 1) throw new RuntimeException("ERROR: number of rows must be at least one.");

				//System.out.println("Read: numRows: " + value);
				numRows = value;
				rowCounter = value;
				for (int i = 0; i < rowCounter; i++) {
					rowSequences.add(new ArrayList<Integer>());
				}
				mode = PUZZLE_STRING_MODE_COL_COUNT;
				tokenIndex++;
			}
			else if (mode == PUZZLE_STRING_MODE_COL_COUNT) {
				// read the number of columns the puzzle contains
				int value = nextTokenValue(tokens);
				if (value < 1) throw new RuntimeException("ERROR: number of columns must be at least one.");

				//System.out.println("Read: numCols: " + value);
				numCols = value;
				colCounter = value;
				for (int i = 0; i < colCounter; i++) {
					colSequences.add(new ArrayList<Integer>());
				}
				mode = PUZZLE_STRING_MODE_ROW_SEQUENCE_COUNT;
				tokenIndex++;
			}
			else if (mode == PUZZLE_STRING_MODE_ROW_SEQUENCE_COUNT) {
				// read the number of sequences a row contains
				int value = nextTokenValue(tokens);
				//System.out.println("Read: numRowSequences: " + value);
				sequenceCounter = value;
				mode = PUZZLE_STRING_MODE_ROW_SEQUENCE;
				tokenIndex++;
			}
			else if (mode == PUZZLE_STRING_MODE_COL_SEQUENCE_COUNT) {
				// read the number of sequences a column contains
				int value = nextTokenValue(tokens);
				//System.out.println("Read: numColSequences: " + value);
				sequenceCounter = value;
				mode = PUZZLE_STRING_MODE_COL_SEQUENCE;
				tokenIndex++;
			}
			else if (mode == PUZZLE_STRING_MODE_ROW_SEQUENCE) {
				if (sequenceCounter == 0) { // no more sequencies on this row
					rowCounter--; // move on to the next row
					if (rowCounter == 0) mode = PUZZLE_STRING_MODE_COL_SEQUENCE_COUNT; // last row?
					else mode = PUZZLE_STRING_MODE_ROW_SEQUENCE_COUNT;
				}
				else {
					// store a row sequence on the associated row
					int value = nextTokenValue(tokens);
				//System.out.println("Read: rowSequence: " + value);
					rowSequences.get(numRows - rowCounter).add(value);
					sequenceCounter--;
					tokenIndex++;
				}
			}
			else if (mode == PUZZLE_STRING_MODE_COL_SEQUENCE) {
				if (sequenceCounter == 0) { // no more sequencies on this column
					colCounter--; // move on to the next column
					if (colCounter == 0) mode = PUZZLE_STRING_MODE_READY; // last column?
					else mode = PUZZLE_STRING_MODE_COL_SEQUENCE_COUNT;
				}
				else {
					// store a column sequence on the associated colummn
					int value = nextTokenValue(tokens);
				//System.out.println("Read: colSequence: " + value);
					colSequences.get(numCols - colCounter).add(value);
					sequenceCounter--;
					tokenIndex++;
				}
			}
			else if (mode == PUZZLE_STRING_MODE_READY) {
				boolean hasNextToken = proceedToNextToken(tokens);
				if (hasNextToken) {
					String extraTokens = "";
					for (int i = tokenIndex; i < tokens.length; i++) {
						extraTokens += tokens[i] + " ";
					}
					throw new RuntimeException("ERROR: too many values in the Puzzle String, extras: '"+extraTokens+"'");
				}
				else break; // ending state
			}
			else {
				throw new RuntimeException("ERROR: Internal error: (PuzzleString mode)");
			}
		}


		// CONSTRUCT FINAL PUZZLE ARRAYS:

		// maximum sequence counts:
		int maxRowSequenceCount = -1;
		for (int r = 0; r < numRows; r++) {
			int s = rowSequences.get(r).size();
			if (s > maxRowSequenceCount) maxRowSequenceCount = s;
		}
		int maxColSequenceCount = -1;
		for (int c = 0; c < numCols; c++) {
			int s = colSequences.get(c).size();
			if (s > maxColSequenceCount) maxColSequenceCount = s;
		}

		// copy sequence values:
		int[][] puzzleRowSequences = new int[numRows][maxRowSequenceCount];
		for (int r = 0; r < numRows; r++) {
			for (int s = 0; s < maxRowSequenceCount; s++) {
				int seqValue;
				if (s < rowSequences.get(r).size()) seqValue = rowSequences.get(r).get(s);
				else seqValue = PUZZLE_NO_SEQUENCE;
				puzzleRowSequences[r][s] = seqValue;
			}
		}
		int[][] puzzleColSequences = new int[numCols][maxColSequenceCount];
		for (int c = 0; c < numCols; c++) {
			for (int s = 0; s < maxColSequenceCount; s++) {
				int seqValue;
				if (s < colSequences.get(c).size()) seqValue = colSequences.get(c).get(s);
				else seqValue = PUZZLE_NO_SEQUENCE;
				puzzleColSequences[c][s] = seqValue;
			}
		}

		return new JapanesePuzzle(puzzleRowSequences, puzzleColSequences);
	}


	private static int nextTokenValue(String[] tokens) {
		boolean hasNextValue = proceedToNextToken(tokens);
		if (!hasNextValue) throw new RuntimeException("ERROR: Puzzle String misses values");

		String token = tokens[tokenIndex];
		int value;
		try {
			value = Integer.parseInt(token);
			if (value < 0) throw new RuntimeException(
				"ERROR: Puzzle string includes negative numbers: ("+token+")");
		}
		catch(NumberFormatException nfe) {
			throw new RuntimeException("ERROR: Puzzle string includes non-digit characters: ("+token+")");
		}
		return value;
	}


	private static boolean proceedToNextToken(String[] tokens) {
		// ignore empty tokens
		if (tokenIndex >= tokens.length) return false;
		while (tokens[tokenIndex].length() == 0) {
			tokenIndex++;
			if (tokenIndex >= tokens.length) return false;
		}
		return true;
	}





	public static JapanesePuzzle createPuzzleFromSolution(boolean[][] solution) {
		if (solution == null) throw new RuntimeException("ERROR: solution is null");
		if (solution.length == 0)  throw new RuntimeException("ERROR: solution has no rows (empty)!");

		ArrayList<Integer> sequences = new ArrayList<Integer>();

		int numRows = solution.length;
		int numCols = solution[0].length;

		sequences.add(numRows);
		sequences.add(numCols);

		// add row sequences
		for (int row = 0; row < numRows; row++) {
			sequences.add(0); // initialValue (will be updated)
			int oldSequenceSize = sequences.size();

			int sequenceLengthCounter = 0;
			boolean insideSequence = false;
			for (int col = 0; col < numCols; col++) {
				if (solution[row][col]) {
					if (!insideSequence) {
						insideSequence = true;
					}
					sequenceLengthCounter++;
				}
				else {
					if (insideSequence) {
						// end of one sequence
						sequences.add(sequenceLengthCounter);
						sequenceLengthCounter = 0;
						insideSequence = false;
					}
				}
			}
			if (insideSequence) {
				// end of last sequence on this row
				sequences.add(sequenceLengthCounter);
				sequenceLengthCounter = 0;
				insideSequence = false;
			}

			int newSequenceSize = sequences.size();
			int rowSequencesCount = newSequenceSize - oldSequenceSize;
			sequences.set(oldSequenceSize - 1, rowSequencesCount); // udpate count info
		}

		// add column sequences
		for (int col = 0; col < numCols; col++) {
			sequences.add(0); // initialValue (will be updated)
			int oldSequenceSize = sequences.size();

			int sequenceLengthCounter = 0;
			boolean insideSequence = false;
			for (int row = 0; row < numRows; row++) {
				if (solution[row][col]) {
					if (!insideSequence) {
						insideSequence = true;
					}
					sequenceLengthCounter++;
				}
				else {
					if (insideSequence) {
						// end of one sequence
						sequences.add(sequenceLengthCounter);
						sequenceLengthCounter = 0;
						insideSequence = false;
					}
				}
			}
			if (insideSequence) {
				// end of last sequence on this column
				sequences.add(sequenceLengthCounter);
				sequenceLengthCounter = 0;
				insideSequence = false;
			}

			int newSequenceSize = sequences.size();
			int colSequencesCount = newSequenceSize - oldSequenceSize;
			sequences.set(oldSequenceSize - 1, colSequencesCount); // udpate count info
		}

		String sequenceString = "";
		for (int i = 0; i < sequences.size(); i++) {
			sequenceString += " " + sequences.get(i);
		}


		return createPuzzleFromSequences(sequenceString);
	}
*/