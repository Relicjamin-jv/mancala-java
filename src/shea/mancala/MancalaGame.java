package shea.mancala;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JPanel;


/**
 * This class handles all game operations
 * @author Shea Bunge
 * @version 1.0
 */
@SuppressWarnings("serial")
class MancalaGame extends JPanel implements MouseListener {

	/**
	 * Holds an instance of the Board class
	 */
	final Board board;

	private int turnNumber = 0;

	/**
	 * Defines the amount of stones in the pits
	 */
	public int[] pitStones = new int[] { 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0 };

	/**
	 * The player currently having a turn.
	 * Cannot be any number besides 1 or 2
	 */
	private int currentPlayer = 1;

	/**
	 * Player 2 will be the AI if this boolean is set to true
	 */
	private boolean AI = true;

	/**
	 * Determines when the game is won and who by
	 *
	 * Valid values:
	 * -1 = game has not ended
	 *  0 = game ended in tie
	 *  1 = player 1 won
	 *  2 = player 2 won
	 */
	private int winningPlayer = -1;

	/**
	 * Initialize the class
	 */
	public MancalaGame() {
		board = new Board(this, Color.blue, Color.red);

		setBorder(BorderFactory.createLineBorder(Color.black));
		addMouseListener(this);
	}

	/**
	 * Set the size of the window to the size of the board
	 * @return the size of the Mancala board
	 */
	@Override
	public Dimension getPreferredSize() {
		return board.getSize();
	}

	/**
	 * Retrieve the player who is currently having a turn
	 * @return the current player number
	 * @throws RuntimeException if the player does not have a valid number
	 */
	public int getCurrentPlayer() throws RuntimeException {
		if ( currentPlayer != 1 && currentPlayer != 2 ) {
			throw new RuntimeException("currentPlayer must be either 1 or 2");
		}

		return currentPlayer;
	}

	/**
	 * Retrieve the player who is *not* currently having a turn
	 * @return the other player number
	 */
	public int getOtherPlayer() {
		return currentPlayer == 1 ? 2 : 1;
	}

	/**
	 * Perform a player's turn by moving the stones between pits
	 * @param pit the pit selected by the user
	 * @return whether the user's turn is ended
	 */
	protected boolean moveStones(final int pit) {
		int pointer = pit;
		System.out.println(pit);

		// return if pit has no stones
		if ( pitStones[pit] < 1 ) {
			return true;  //true means go again
		}

		// take stones out of pit
		int stones = pitStones[pit];
		pitStones[pit] = 0;

		while ( stones > 0 ) {
			++pointer;

			// skip other player's storage pit and reset pointer
			if (pointer == 13) {
				pointer = 0;
			} else {
				pitStones[pointer]++;
				stones--;
			}

			repaint();
		}

		// set to point to the opposite pit
		int inversePointer = -pointer + 12;

		// Check for capture
		if (pointer < 6 && pitStones[pointer] == 1 && pitStones[inversePointer] > 0) {

			// Transfer this stone along with opposite pit's stones to store
			pitStones[6] += pitStones[inversePointer] + 1;

			// Clear the pits
			pitStones[pointer] = 0;
			pitStones[inversePointer] = 0;
		}

		// return true if the turn ended in storage pit
		printTheBoard();
		return pointer == 6;
	}

	/**
	 * Begin the other player's turn
	 */
	public void switchTurn() {
	// If the AI is present
	if(!AI) {
		// Change the active player
		currentPlayer = getOtherPlayer();

		// Reverse the pit positions
		int[] newStones = new int[14];
		System.arraycopy(pitStones, 7, newStones, 0, 7);
		System.arraycopy(pitStones, 0, newStones, 7, 7);

		pitStones = newStones;
		repaint();
	}else{ //There is an AI
		currentPlayer = getOtherPlayer(); //return player 2 which in this case is the AI

		// Reverse the pit positions
		int[] newStones = new int[14];  //need to reverse the pits to comply with logic
		System.arraycopy(pitStones, 7, newStones, 0, 7);
		System.arraycopy(pitStones, 0, newStones, 7, 7);

		pitStones = newStones;
		repaint();

		//Code for the AI
		if(getCurrentPlayer() == 2) {
			AILogic();
		}
	}

	}

	//run the AI code here for min max
	public void AILogic(){
//		Random rand = new Random();
//		int randomIndex = rand.nextInt(6) + 1;
//		System.out.println("The AI is picking index: " + randomIndex);
//		doPlayerTurn(randomIndex); //doing the player


	}

	/*
	*	logic from: https://www.youtube.com/watch?v=8r78GYmuHaY
	 */
	public int alphaBeta(int[] pitStones, int depth, int alpha, int beta, boolean isMax){
		if(checkForWin()){
			return -1;
		}else if (depth==0){
			return heuristicStoneCompare();
		}

	}


	/*
	* Returns value representing the goodness of the board for the current player
	* positive/ high values are favorable for current player
	* negative/ low values are unfavorable for current player
	*/
	public int heuristicStoneCompare() {
		int yourStones=0;
		for (int i = 0; i < pitStones.length/2;i++) {
			if(i==6) {
				yourStones+=pitStones[i] * 1.5;
			}else {
				yourStones+=pitStones[i];
			}
		}
		int enemyStones=0;
		for (int i = 7; i < pitStones.length;i++) {
			if(i==6) {
				enemyStones+=pitStones[i] * 1.5;
			}else {
				enemyStones+=pitStones[i];
			}
		}
		return yourStones - enemyStones;
	}

	/**
	 * Draw the stones in the pits
	 * @param g frame Graphics object
	 */
	protected void drawStones(Graphics g) {
		int cx, cy; // extra centering correction

		for (int pit = 0; pit < pitStones.length; ++pit) {
			if (pit == 6 || pit == 13) {
				cx = -3;
				cy = 0;
			} else if (pit > 9) {
				cx = 3;
				cy = 6;
			} else {
				cx = 7;
				cy = 9;
			}

			g.drawString( Integer.toString(pitStones[pit]), board.getPitCenterX(pit) + cx, board.getPitCenterY(pit) + cy );
		}
	}

	/**
	 * Paint information on the current player
	 * @param g Graphics object
	 */
	protected void paintPlayerInfo(Graphics g) {

		if ( winningPlayer < 0 ) {
			g.drawString("Player " + getCurrentPlayer() + "'s turn", 20, 20);
		} else {
			if (winningPlayer == 0) {
				g.drawString("Draw!", 20, 20);
			} else {
				g.drawString("Player " + winningPlayer + " wins!", 20, 20);
			}
		}
	}

	/**
	 * Draw the board and stones on the screen
	 * @param g frame Graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.black);
		board.drawBoard(g);

		g.setColor(Color.DARK_GRAY);
		drawStones(g);

		g.setColor(Color.black);
		paintPlayerInfo(g);

	}

	/**
	 * Check if either player has won the game
	 */
	public boolean checkForWin() {
		boolean topRowEmpty = true, bottomRowEmpty = true;

		// Check if the bottom row contains any stones
		for (int i = 0; i < 6; ++i) {
			if (pitStones[i] > 0) {
				bottomRowEmpty = false;
				break;
			}
		}

		// Check if the top row contains any stones
		for (int i = 7; i < 13; ++i) {
			if (pitStones[i] > 0) {
				topRowEmpty = false;
				break;
			}
		}

		// Take the stones from the non-empty row and add them to that player's store
		if (topRowEmpty || bottomRowEmpty) {
			if (topRowEmpty && ! bottomRowEmpty) {
				for (int i = 0; i < 6; ++i) {
					pitStones[6] += pitStones[i];
					pitStones[i] = 0;
				}
			} else if (! topRowEmpty && bottomRowEmpty) {
				for (int i = 7; i < 13; ++i) {
					pitStones[13] += pitStones[i];
					pitStones[i] = 0;
				}
			}

			// Determine which player holds the most stones
			if (pitStones[6] > pitStones[13] ) {
				winningPlayer = getCurrentPlayer();
			} else if (pitStones[6] < pitStones[13]) {
				winningPlayer = getOtherPlayer();
			} else {
				// tie
				winningPlayer = 0;
			}

			removeMouseListener(this);
		}
		return (topRowEmpty || bottomRowEmpty);

	}

	/**
	 * Perform a player's turn
	 * @param pit the pit selected by the player
	 */
	public void doPlayerTurn(int pit) {

		// perform the player's action
		boolean	result = moveStones(pit);
		System.out.println(result + " Player " + getCurrentPlayer() + " move");

		// make sure that a player hasn't run out of stones
		checkForWin();

		// change the player if the current turn is ended
		if ( ! result && winningPlayer < 0 ) {
			switchTurn();
		}else if(AI) {
			//if the AI is player 2 we want to go ahead and make another AI move
			AILogic();
		}
	}

	/**
	 * Watch for when the player selects a pit and perform the turn
	 * @param e the mouse click event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int x, y;
		int mx = e.getX();
		int my = e.getY();

		// loop through all pits in the bottom row
		for (int pit = 0; pit < 6; ++pit) {
			x = board.getPitX(pit);
			y = board.getPitY(pit);

			// check if the click was inside the pit area.
			if ( mx > x && mx < x + board.pitWidth && my > y && my < y + board.pitHeight )  {
				doPlayerTurn(pit);
			}
		}
	}

	public void printTheBoard(){
		turnNumber++;
		System.out.println("Mancala Turn " + turnNumber + ", Player: " + getCurrentPlayer());
		System.out.print("  "); //spacing
		//print out the top player
		for(int i = 5; i >= 0; i--){
			System.out.print(pitStones[i] + " ");
		}
		System.out.println();

		//print out the mancala pits
		System.out.println(pitStones[6] + "             " + pitStones[13]);
		System.out.print("  "); //Spacenig
		//print out the bottom player
		for(int i = 7; i < 13; i++){
			System.out.print(pitStones[i] + " ");
		}

		System.out.println("\n");
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}
