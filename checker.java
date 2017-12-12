// Checker.java
// Ryan Swope and Phil Bohlman
// CS 201 Final Project
// 5/16/16

import java.awt.*;

public class checker {

	//instance variables
	protected Color color;
	protected Boolean king;
	protected Boolean selected;

	//constructor
	public checker(Color clr){
		color = clr;
		king = false;
		selected = false;
	}

	//instance methods

	// makes a checker a king
	public void kingMe(){
		king = true;
	}

	// returns whether the checker is a king
	public boolean isKing(){
		return king;
	}

	// returns the checker's color, represented by a color string
	public Color getColor(){
		return color;
	}

	// selects the checker
	public void select(){
		selected = true;
	}

	// unselects the checker
	public void unselect(){
		selected = false;
	}

	// returns whether the checker is selected
	public Boolean isSelected(){
		return selected;
	}
}
