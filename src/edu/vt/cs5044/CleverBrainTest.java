package edu.vt.cs5044;

import static org.junit.Assert.*;
import edu.vt.cs5044.tetris.*;
import org.junit.*;

public class CleverBrainTest {
	private Brain brain;
	private CleverBrain cleverBrain;

	@Before
	public void setUp() {
		brain = new CleverBrain();
		cleverBrain = new CleverBrain();
	}

	@Test
	public void testGetMaxColHeightBoard1() {
		Board brd = new Board(
				"          ",
				"  ##      ",
				"   ##     ",
				"###### #  ",
				"######### ",
				"######### ",
				"### ##### ",
				"######### "
			);
		assertEquals(7, cleverBrain.getMaxColumnHeight(brd));
	}
	@Test
	public void testBestMoveShapeOBoard2() {
		Board brd = new Board(
				"          ",
				"          ",
				"    ####  ",
				"    ##### ",
				"  ##### ##"
			);
		Shape shape = Shape.O;
		Move brainMove = brain.getBestMove(brd, shape); // ACTION
	
		assertEquals(new Move(0, 0, 0), brainMove); // cost is ignored for equality of Move objects
		assertEquals(0, brainMove.getColumn()); // sufficient, since we don't care about rotation here
		assertTrue(brainMove.getColumn() == 0 && brainMove.getRotation() == 0); // could become more complex
		Board expectedResult = new Board(
				"          ",
				"    ####  ",
				"##  ##### ",
				"####### ##"
					);
		assertEquals(expectedResult,
						brd.getResultBoard(new Piece(shape, brainMove.getRotation()), brainMove.getColumn()));
	}

	@Test
	public void testClearRowFunction() {
		Board brd = new Board(
				"          ",
				"    ####  ",
				"##  ##### ",
				"##########",
				"##########"
					);
		assertEquals(2, cleverBrain.clearRows(brd));
	}

}
