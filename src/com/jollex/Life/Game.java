package com.jollex.Life;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import javax.swing.*;

public class Game extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//JPanel that holds all cells
	private JPanel grid = new JPanel();
	
	//Variables needed during game
	private int rows = 30;
	private int cols = 16;
	private JLabel cells[][];
	private boolean alive[][];
	private boolean mark[][];
	private boolean gameStarted = false;
	
	//Icons for white and black cell states
	private ImageIcon white = new javax.swing.ImageIcon(getClass().getResource("images/white.gif"));
	private ImageIcon black = new javax.swing.ImageIcon(getClass().getResource("images/black.gif"));
	
	//Mouse listener that will be added to each cell
	MouseAdapter click = new java.awt.event.MouseAdapter() {
		public void mouseReleased(java.awt.event.MouseEvent e) {
			manualCell(e);
		}
	};
	
	//Creates timer and actionlistener that will update the cells every 200 ms
	ActionListener updater = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateCells();
		}
	};
	Timer timer = new Timer(200, updater);
	
	//Constructor
	public Game() {
		//Gets maximum width and height of user's screen
		int screenWidth = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		int screenHeight = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
		
		//Calculates maximum amount of cells that can fit in user's screen
		rows = screenWidth / 18;
		cols = (screenHeight - 22) / 18;
		
		/*
		 * Sets the default close operation
		 * Sets the size to fit the cells perfectly
		 * Sets frame to not be resizable
		*/
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(rows * 18, (cols * 18) + 22);
		this.setResizable(false);
		
		//Adds a keylistener to the frame that starts and stops the game when enter is pressed
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					if (!gameStarted)
						startGame();
					else
						stopGame();
				}
			}
		});
		this.requestFocus();
		
		//Creates the cells and adds their panel to the frame
		createCells();
		this.add(grid);
		
		//Sets the frame to be visible
		this.setVisible(true);
	}
	
	private void createCells() {
		//Sets the right size for the panel and sets up a GridBagLayout
		grid.setSize(rows * 18, (cols * 18) + 22);
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints g = new GridBagConstraints();
		grid.setLayout(gridBag);
		
		//Creates the arrays needed for the cells
		cells = new JLabel[rows][cols];
		alive = new boolean[rows][cols];
		mark = new boolean[rows][cols];
		
		//Two for loops with which we can reach every cell
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				//Makes a new JLabel for each cell, sets its icon to blank, and adds a border
				cells[x][y] = new JLabel();
				cells[x][y].setIcon(white);
				cells[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				
				//All cells start dead and not marked for change
				alive[x][y] = false;
				mark[x][y] = false;
				
				//Adds the mouselistener to each cell so it's state can be changed
				cells[x][y].addMouseListener(click);
				
				//Adds the cell to the correct position in the grid
				g.gridx = x;
				g.gridy = y;
				grid.add(cells[x][y], g);
			}
		}
	}
	
	//Used to change cell states manually when game isn't running
	private void manualCell(java.awt.event.MouseEvent e) {
		//Gets coordinates of clicked cell
		JLabel cell = (JLabel)e.getSource();
		int x = cell.getX() / 18;
		int y = cell.getY() / 18;
		
		//Changes state of the cell
		if (alive[x][y]) {
			alive[x][y] = false;
			cells[x][y].setIcon(white);
		} else {
			alive[x][y] = true;
			cells[x][y].setIcon(black);
		}
	}
	
	//Starts the game
	private void startGame() {
		//Removes mouselistener from each cell so it cannot be changed while game is running
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				cells[x][y].removeMouseListener(click);
			}
		}
		
		//Starts timer and changes game state
		timer.start();
		gameStarted = true;
	}
	
	//Stops game
	private void stopGame() {
		//Adds mouselistener to each cell again so they can be changed manually
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				cells[x][y].addMouseListener(click);
			}
		}
		
		//Stops timer and changes game state
		timer.stop();
		gameStarted = false;
	}
	
	//Updates cells when game is running
	private void updateCells() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				//Get amount of neighbors
				int around = count(row, col);
				
				//When a cell is marked, it means it's state will be changed
				if (around < 2) {
					//If a cell is alive and has less than two neighbors, it dies
					if (alive[row][col]) {
						mark[row][col] = true;
					}
				} else if (around == 3) {
					//If a cell is dead and has 3 neighbors, it becomes live
					if (!alive[row][col]) {
						mark[row][col] = true;
					}
				} else if (around > 3) {
					//If a cell is alive and has more than 3 neighbors, it dies
					if (alive[row][col]) {
						mark[row][col] = true;
					}
				}
				//If a cell is alive and has 2 or 3 neighbors, it doesn't change state.
			}
		}
		
		//Updates all cells after they've been marked
		updateIcons();
	}
	
	//Calculates amount of neighbors toa  cell
	private int count(int row, int col) {
		int around = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				int i = row + x;
				int j = col + y;
				
				if (i >= 0 && i < cells.length && j >= 0 && j < cells[row].length && !(i == row && j == col)) {
					if (alive[i][j]) {
						around++;
					}
				}
			}
		}
		return around;
	}
	
	//Changes state of all marked cells
	private void updateIcons() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				if (mark[row][col]) {
					if (alive[row][col]) {
						cells[row][col].setIcon(white);
						alive[row][col] = false;
					} else {
						cells[row][col].setIcon(black);
						alive[row][col] = true;
					}
					mark[row][col] = false;
				}
			}
		}
	}

	//Main function, used to start the game at the beginning
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Game game = new Game();
	}

}
