//board.java
//
// Ryan Swope and Phil Bohlman
// CS 201 Final Project
//
// 5/16/16

import java.awt.Color;

import structure5.Matrix;

public class board {

	// instance variables
	protected Matrix<checker> layout;
	protected int redPieces;
	protected int blackPieces;
	protected int turn;
	
	
	// constructor
	public board(){
		layout = new Matrix<checker>(8,8);
		redPieces = 0;
		blackPieces = 0;
	}
	
	//instance methods
	
	// creates a new empty board and sets it
	public void reset(){
		layout = new Matrix<checker>(8,8);
		setBoard();
	}
	
	// sets the board to its default state with 12 checkers per side
	public void setBoard(){
		// set red pieces
		for(int row=0; row<3; row++){
			for(int col=0; col<8; col++){
				if(row%2==0){
					if (col%2==0)
						layout.set(row, col, new checker(Color.red));
				} else {
					if (col%2==1)
						layout.set(row, col, new checker(Color.red));
				}
			}
		} redPieces = 12;
		
		//set black pieces
		for(int row=5; row<8; row++){
			for(int col=0; col<8; col++){
				if(row%2==1){
					if(col%2==1)
						layout.set(row, col, new checker(Color.black));
				} else {
					if(col%2==0)
						layout.set(row, col, new checker(Color.black));
				}
			}
		} blackPieces = 12;
		turn = 0;
	}
	
	// returns the checker at the specific row and column
	// returns null if no checker at location
	public checker get(int row, int col){
		return layout.get(row, col);
	}
	
	/* Returns whether or not a move is possible
	 * If it is possible (returns true), the matrix is updated
	 * Has a separate case for king movements and non-king movements
	 * Each case has subordinate cases depending on whether the checker
	 * is moving one space or capturing
	 * 
	 * Currently, double jumping is implemented for non-king checkers
	 * and double jump must be executed if available 
	 * 
	 */
	public boolean move(int row1,int col1,int row2,int col2){
		checker space1 = get(row1, col1); //first space
		checker space2 = get(row2, col2); //destination space
		int moveCase; //moveCase and capCase are helper variables for boolean calculations
		int capCase;  //they help move account for 
					  //the direction restrictions of the two checker colors
		
		if (!isEmpty(space1)&&isEmpty(space2)){
			if (space1.getColor()==Color.red){
				moveCase= 1;
				capCase = 2;
			} else {
				moveCase= -1;
				capCase = -2;			
			
			// king case
			} if (space1.isKing()){
				
				// 1 space king move
				if((Math.abs(col2-col1)==1)&&(Math.abs(row2-row1)==1)){ 
					moveHelper(row1,col1,row2,col2);
					turn++;
					return true;
				}	
				// king capture case
				else if ((Math.abs(col2-col1)==2)&&(Math.abs(row2-row1)==2) // correct distance
				&& (layout.get((row1+row2)/2,(col2+col1)/2).getColor()!=space1.getColor())) { //capturing enemy piece?
					
					moveHelper(row1,col1,row2,col2);
					if (layout.get((row1+row2)/2,(col2+col1)/2).getColor() == Color.red)
						redPieces--;
					else
						blackPieces--;
					clearSpace((row1+row2)/2,(col2+col1)/2);
					turn++;
					return true;
				}
			
			// 1 space non-king move case
			} if ((Math.abs(col2-col1)==1)&&(row2==row1+moveCase)){ //is this an adjacent tile?
				moveHelper(row1,col1,row2,col2);
				turn++;
				return true;
			}
			
			// non-king capture case
			if ((Math.abs(col2-col1)==2)&&((row2==row1+capCase)) // correct distance?
			&& (layout.get(row1+moveCase,(col2+col1)/2).getColor()!=space1.getColor())){ // is there an enemy piece?
				moveHelper(row1,col1,row2,col2);
				clearSpace(row1+moveCase,(col2+col1)/2);
				if(space1.getColor()==Color.red){
					blackPieces--;
					System.out.println("black: " +redPieces);
				} else {
					redPieces--;
					System.out.println("red: " + blackPieces);
				}
				
				//helper variables to make the conditions for double jumping more readable
				boolean rightPathInbounds = ((row2+capCase>=0)&&(row2+capCase<=7))&&(col2+2<=7);
				boolean leftPathInbounds = ((row2+capCase>=0)&&(row2+capCase<=7))&&(col2-2>=0);

				if(rightPathInbounds){
					boolean rightEnemyExists = !(isEmpty(layout.get(row2+moveCase,col2+1))) && 
							layout.get(row2+moveCase,col2+1).getColor()!=space1.getColor();
					boolean rightSpaceOpen = isEmpty(layout.get(row2+capCase, col2+2));
					if(rightEnemyExists&&rightSpaceOpen){ // right path exists, double jump!
						return true;
					}
				}
				if(leftPathInbounds){
					boolean leftEnemyExists = !(isEmpty(layout.get(row2+moveCase,col2-1))) &&
							layout.get(row2+moveCase,col2-1).getColor()!=space1.getColor();	
					boolean leftSpaceOpen = isEmpty(layout.get(row2+capCase, col2-2));
					if(leftEnemyExists&&leftSpaceOpen){ // left path exists, double jump!
						return true;
					}		
				} turn++; return true; // capture successful, no double jump
			} return false; //invalid move with valid space selections
		} return false; //invalid move without valid space selections
	}
	
	// to eliminate repeated code for updating the matrix
	public void moveHelper(int row1,int col1,int row2,int col2){
		layout.set(row2, col2, layout.get(row1, col1));
		clearSpace(row1,col1);
		if (get(row2,col2).getColor()==Color.red&&row2==7)
			get(row2,col2).kingMe();
		else if (get(row2,col2).getColor()==Color.black&&row2==0)
			get(row2,col2).kingMe();
	}
	
	// clears a space in the matrix
	public void clearSpace(int row1,int col1){
		layout.set(row1,col1,null);
	}
	
	// returns the turn counter
	public int getTurn(){
		return turn;
	}
	
	// returns whether a space in the matrix is empty
	public boolean isEmpty(checker space){
		return space==null;
	}

	// toString method for matrix, debugging
	public String toString(){
		return layout.toString();
	}
	
	// returns a string describing if a win condition has been met
	public String checkWin(){
		if (blackPieces == 0) 
			return("Red Wins!");
		else if (redPieces == 0)
			return("Black Wins!");
		else
			return"";
	}
	
	// returns a color string depending on whose turn it is 
	// used to make drawing checkers in checkerBoard() simpler
	public Color turn(){
		if (getTurn()%2 == 0)
			return(Color.black);
		else //odd num, red turn
			return(Color.red);
	}

}
