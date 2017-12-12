//CheckersApplet.java
//
// Ryan Swope and Phil Bohlman
// CS 201 Final Project
//
// 5/16/16

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.JOptionPane;
public class CheckersApplet extends Applet implements ActionListener {
	//instance variables
	Label turn;
	checkerBoard visBoard;
	Image king;

	public void init() {

		setFont(new Font("Times",Font.BOLD, 18));
		turn = new Label("Turn: BLACK");
		turn.setAlignment(Label.CENTER);
		king = getImage(getDocumentBase(), "Crown_Symbol.png");
		visBoard = new checkerBoard(this);
		setBackground(Color.white);

		Panel columns = new Panel();	// create column labels
		columns.setLayout(new GridLayout(1,8));
		for(char alphabet = 'a';alphabet<='h';alphabet++){
			columns.add(bwLabel(""+alphabet));
		}

		Panel rows = new Panel();	//create row labels
		rows.setLayout(new GridLayout(8,1));
		for(int i=1;i<9;i++){
			rows.add(bwLabel(" "+ i +" "));
		}

		Panel bottom = new Panel();	//create bottom panel
		bottom.setLayout(new GridLayout(1,2));
        bottom.add(turn);
        bottom.add(CButton("Start/Reset", Color.black, Color.red));

		Panel empty1 = new Panel(); //create empty

		setLayout(new BorderLayout()); //set layout
		add("Center", visBoard);
        add("North", columns);
        add("South", bottom);
        add("East", empty1);
        add("West", rows);

        setSize(800,800);
        setVisible(true);
	}

	// create a colored button
    protected Button CButton(String s, Color fg, Color bg) {
        Button b = new Button(s);
        b.setBackground(bg);
        b.setForeground(fg);
        b.addActionListener(this);
        return b;
    }

	// create black and white label
    protected Label bwLabel(String s) {
        Label b = new Label(s);
        b.setBackground(Color.white);
        b.setForeground(Color.black);
        b.setAlignment(Label.CENTER);
        return b;
    }

    // handles button clicks
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Button) {
            String label = ((Button)e.getSource()).getLabel();
            if (label.equals("Start/Reset")) {
                reset();
        }
    }
}
  //reset visual board as well as board object
    public void reset() {
    	visBoard.clear();
    	turn.setText("Turn: BLACK");
    }

class checkerBoard extends Canvas implements MouseListener{

	int xClick = 0;
	int yClick = 0;
	board myBoard = new board();
	Boolean win = false;
	CheckersApplet parent;
	int spaceWidth;
	int spaceHeight;

	//constructor
	public checkerBoard(CheckersApplet c){
		addMouseListener(this);
		parent = c;
		myBoard.reset();
	}

	//Resets data-structure board, resets game, and repaints
	public void clear(){
		myBoard.reset();
		win = false;
		repaint();
	}

	public void paint(Graphics g){
		update(g);
	}

	//Paints board based on data-structure board
	public void update(Graphics g){
		spaceWidth=getWidth()/8;
    	spaceHeight=getHeight()/8;

		for (int row = 0; row <= 7; row++) {
			for (int col = 0; col <= 7; col++) {
				if (row%2 == col%2){
					drawSpace(Color.gray, col*spaceWidth, row*spaceHeight);
					if (!myBoard.isEmpty(myBoard.get(row,col)))
						drawChecker(col,row);
					} else
						drawSpace(Color.white, col*spaceWidth, row*spaceHeight);
			}
		}
	}

	//Draws checker space given color and coordinates
	public void drawSpace(Color color, int x, int y){
		Graphics g = getGraphics();

		g.setColor(color);
		g.fillRect(x,y, spaceWidth, spaceHeight);
		g.setColor(Color.black);
		g.drawRect(x,y,spaceWidth, spaceHeight);
	}

	//Draws oval shaped checker piece based on color of checker
	//at input coordinates in back-end board
    public void drawChecker(int x, int y) {
    	Graphics g = getGraphics();

    	g.setColor(myBoard.get(y,x).getColor());
    	g.fillOval(x*spaceWidth,y*spaceHeight,spaceWidth,spaceHeight);
    	if(myBoard.get(y, x).isSelected())
    		g.setColor(Color.yellow);
    	g.drawOval(x*spaceWidth,y*spaceHeight,spaceWidth,spaceHeight);
    	if(myBoard.get(y, x).isKing())
    		g.drawImage(parent.king, (x*spaceWidth) + spaceWidth/4 ,(y*spaceHeight) + spaceWidth/8 ,spaceWidth/2 , spaceHeight/2, this);
    }

    //Pop-up window for win, prompts to start a new game
    public int winWindow(String winner){
    	String winString = winner + "\nPlay Again?";
    	return JOptionPane.showConfirmDialog(parent, winString);
    }

	/* Mouse events
	 *
	 * User clicks a checker of correct color given the turn =>
	 * Checker is "selected", and its coordinates are stored
	 *
	 * User clicks a blank space first without selecting checker =>
	 * Nothing happens
	 *
	 * User clicks a space after checker is "selected" =>
	 * Move will try to be implemented for the selected checker
	 * into the space. If move succeeds: checker will move, else: error.
	 *
	 */
    public void mousePressed(MouseEvent event){
    	Point p = event.getPoint();
    	Graphics g = getGraphics();
    	int x = (p.x)/spaceWidth;
    	int y = (p.y)/spaceHeight;
    	checker currentSpace = myBoard.get(y,x);
    	checker oldSpace = myBoard.get(yClick,xClick);

    	//if a checker is clicked that is the right color based on the turn
    	if ((!myBoard.isEmpty(currentSpace)) && (currentSpace.getColor() == myBoard.turn())) {
    		currentSpace.select();

    		//if a checker has already been selected, select new checker and unselect old
	    	if (!(oldSpace!=null && oldSpace==currentSpace)&&(oldSpace!=null && oldSpace.isSelected())) {
    			oldSpace.unselect();
    		}
	    	repaint();
    		xClick = x;
    		yClick = y;

    	//if an empty space is being selected
    	} else if (oldSpace != null && myBoard.isEmpty(currentSpace)) {

    		// if moving selected piece to empty space succeeds
    		 if (myBoard.move(yClick,xClick,y,x)) {
    			oldSpace.unselect();
    			repaint();
    			win = myBoard.checkWin()!="";

    			if (!win){
					if (myBoard.getTurn()%2 == 0)
						parent.turn.setText("Turn: BLACK");
					else
						parent.turn.setText("Turn: RED");

    			} else{
    				parent.turn.setText("Game Over");
    				if(winWindow(myBoard.checkWin())==JOptionPane.YES_OPTION)
    					reset();
    			}

    		// if the player has selected a piece but tries to make an invalid move (if move fails)
			} else if (!(oldSpace == null) && oldSpace.isSelected()){

			    if (myBoard.getTurn()%2 == 0)
					parent.turn.setText("Invalid Move! Turn: BLACK");
				else
					parent.turn.setText("Invalid Move! Turn: RED");
			}

    	}
    }
    public void mouseClicked(MouseEvent event) { }
    public void mouseReleased(MouseEvent event) { }
    public void mouseEntered(MouseEvent event) { }
    public void mouseExited(MouseEvent event) { }
}
}
