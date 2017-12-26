package edu.vt.cs5044;

import edu.vt.cs5044.tetris.Board;
import edu.vt.cs5044.tetris.Brain;
import edu.vt.cs5044.tetris.Move;
import edu.vt.cs5044.tetris.Piece;
import edu.vt.cs5044.tetris.Shape;

public class CleverBrain implements Brain {

	private static final double DEFAULT_SCORE = -100.0;

	private double score;

	/**
	 * Default constructor
	 */
	public CleverBrain() {
		score = 0.0;
	}

	/**
	 * Calculate the maximum height of the board in all rows.
	 * 
	 * @param board
	 *            The current board
	 * @return int
	 */
	public int getMaxColumnHeight(Board board) {
		int maxHeight = -1; // initial value impossibly low
		for (int col = 0; col < Board.WIDTH; col++) {
			int colHeight = getColHeightAt(board, col);
			if (colHeight > maxHeight) {
				maxHeight = colHeight;
			}
		}
		return maxHeight;
	}

	/**
	 * Returns the height of specified column in the board
	 * 
	 * @param board
	 *            The current board
	 * @param column
	 *            Specified column
	 * @return int
	 */
	private int getColHeightAt(Board board, int column) {
		for (int row = Board.HEIGHT; row >= 0; row--) {
			if (board.isBlockAt(column, row)) { // found a block?
				return row + 1; // return height
			}
		}
		return 0; // no blocks in column? return zero
	}

	/**
	 * Selects the best move required by brute forcing all the rotation of piece
	 * and putting all of them in every row possible. The rating is then
	 * compared to the highest score.
	 *
	 * @param board
	 *            The current board
	 * @param piece
	 *            The piece on the board
	 * @param move
	 *            Movement of the piece
	 * @return Move
	 */
	public Move bestMoveSelect(Board board, Shape shape) {
		Move bestMove = new Move(0, 0, Double.POSITIVE_INFINITY); // initial
																	// best move
																	// with
																	// highest
																	// cost
		int compareCost = 0; // for comparing the value with the lowest cost

		for (int rotation = 0; rotation < shape.countOrientations(); rotation++) // checks
																					// for
																					// each
																					// rotation
																					// of
																					// the
																					// particular
																					// piece
		{
			Piece piece = new Piece(shape, rotation);

			for (int column = 0; column < (Board.WIDTH - piece.getWidth() + 1); column++) // checks
																							// for
																							// each
																							// column
																							// of
																							// the
																							// particular
																							// piece
			{

				Board tempBoard = new Board(); // Temporarily creating a new
												// board
				tempBoard = board.getResultBoard(piece, column); // Putting the
																	// newly
																	// created
																	// board
																	// with the
																	// upcoming
																	// piece
																	// with
																	// rotation
																	// and in
																	// column

				score = scoreBoard(tempBoard);
				if (score < 0 && bestMove.getCost() < 0) { // if score and cost
															// both are negative
					compareCost = Double.compare(bestMove.getCost(), score);
				} else if (score == 0 && bestMove.getCost() == 0) { // if both
																	// the
																	// numbers
																	// are equal
																	// then no
																	// operation
																	// is
																	// required
					compareCost = 1;
				} else {
					compareCost = Double.compare(score, bestMove.getCost());
				}
				if (compareCost < 0) {
					bestMove = new Move(column, rotation, score);
				}
			}

		}
		return bestMove;
	}

	/**
	 * Scores the board based on rows, holes, blockades and height.
	 *
	 * @param board
	 *            The current board
	 * @return double Returns the calculated entire score which will be
	 *         evaluated
	 */
	private double scoreBoard(Board board) {
		final double bonusForClearingRow = clearRows(board) * 3.1;
		final double penaltyForHoles = numberOfHoles(board) * -4.5;
		final double penaltyForBlockades = numberOfBlockades(board) * .55;
		final double penaltyForHigestColumn = getMaxColumnHeight(board) * -3.25;
		final double score = bonusForClearingRow + penaltyForHoles + penaltyForBlockades + penaltyForHigestColumn;

		return score;
	}

	/**
	 * Checks the board for any rows that can be cleared after putting the
	 * piece.
	 * 
	 * @param board
	 *            The current board
	 * @return int Returns the number of cleared rows
	 */
	public int clearRows(Board board) {
		int count = 0;

		for (int row = 0; row < getMaxColumnHeight(board); row++) {
			if (getBlocksInRow(board, row) == Board.WIDTH) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Counts the number of blocks in the provided row.
	 * 
	 * @param board
	 *            The current Board
	 * @param row
	 * @return int Returns the number of blocks in the entire row
	 */
	public int getBlocksInRow(Board board, int row) {
		int blocks = 0;
		for (int column = 0; column < Board.WIDTH; column++) {
			if (board.isBlockAt(column, row)) {
				blocks++;
			}
		}
		return blocks;
	}

	/**
	 * Checks the board for the number of holes in the board. A hole is
	 * determined by a block being on top of an empty space
	 *
	 * @param board
	 *            The current board
	 * @return int Returns the number of holes
	 */
	public int numberOfHoles(Board board) {
		int count = 0;

		for (int x = 0; x < Board.WIDTH; x++) {
			for (int y = 0; y < Board.HEIGHT; y++) {
				if (!board.isBlockAt(x, y) && board.isBlockAt(x, y + 1)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Checks the board for the number of blockades. A blockade is determined by
	 * the number of blocks above an empty space.
	 *
	 * @param board
	 *            The current board
	 * @return int Returns the number of blockades
	 */
	public int numberOfBlockades(Board board) {
		int count = 0;

		for (int x = 0; x < Board.WIDTH; x++) {

			boolean hasHole = false;

			for (int y = 0; y < Board.HEIGHT; y++) {

				if (!board.isBlockAt(x, y) && board.isBlockAt(x, y + 1)) {
					hasHole = true;
				} else if (hasHole && board.isBlockAt(x, y)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * The default and compulsory method which is created in the interface
	 */
	@Override
	public Move getBestMove(Board currentBoard, Shape shape) {
		Move placePiece = new Move(0, 0, DEFAULT_SCORE);
		System.out.println(Board.WIDTH + "<=\n\n");
		placePiece = bestMoveSelect(currentBoard, shape); // calling the method
															// to select the
															// best move for the
															// upcoming piece
		return placePiece;
	}
}
