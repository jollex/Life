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
	
	private JPanel grid = new JPanel();
	
	private int rows = 30;
	private int cols = 16;
	private JLabel cells[][];
	private boolean alive[][];
	private boolean mark[][];
	private boolean gameStarted = false;
	
	private ImageIcon white = new javax.swing.ImageIcon(getClass().getResource("images/white.gif"));
	private ImageIcon black = new javax.swing.ImageIcon(getClass().getResource("images/black.gif"));
	
	MouseAdapter click = new java.awt.event.MouseAdapter() {
		public void mouseReleased(java.awt.event.MouseEvent e) {
			manualCell(e);
		}
	};
	
	ActionListener updater = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateCells();
		}
	};
	Timer timer = new Timer(200, updater);
	
	public Game() {
		int screenWidth = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
		int screenHeight = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
		
		rows = screenWidth / 18;
		cols = (screenHeight - 22) / 18;
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(rows * 18, (cols * 18) + 22);
		this.setResizable(false);
		
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					if (!gameStarted) {
						startGame();
					} else {
						stopGame();
					}
				}
			}
		});
		this.requestFocus();
		
		createCells();
		this.add(grid);
		
		this.setVisible(true);
	}
	
	private void createCells() {
		grid.setSize(rows * 18, (cols * 18) + 22);
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints g = new GridBagConstraints();
		grid.setLayout(gridBag);
		
		cells = new JLabel[rows][cols];
		alive = new boolean[rows][cols];
		mark = new boolean[rows][cols];
		
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				cells[x][y] = new JLabel();
				cells[x][y].setIcon(white);
				cells[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				alive[x][y] = false;
				mark[x][y] = false;
				
				cells[x][y].addMouseListener(click);
				
				g.gridx = x;
				g.gridy = y;
				grid.add(cells[x][y], g);
			}
		}
	}
	
	private void manualCell(java.awt.event.MouseEvent e) {
		JLabel cell = (JLabel)e.getSource();
		int x = cell.getX() / 18;
		int y = cell.getY() / 18;
		
		if (alive[x][y]) {
			alive[x][y] = false;
			cells[x][y].setIcon(white);
		} else {
			alive[x][y] = true;
			cells[x][y].setIcon(black);
		}
	}
	
	private void startGame() {
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				cells[x][y].removeMouseListener(click);
			}
		}
		timer.start();
		gameStarted = true;
	}
	
	private void stopGame() {
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				cells[x][y].addMouseListener(click);
			}
		}
		timer.stop();
		gameStarted = false;
	}
	
	private void updateCells() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int around = count(row, col);
				
				if (around < 2) {
					if (alive[row][col]) {
						mark[row][col] = true;
					}
				} else if (around == 3) {
					if (!alive[row][col]) {
						mark[row][col] = true;
					}
				} else if (around > 3) {
					if (alive[row][col]) {
						mark[row][col] = true;
					}
				}
			}
		}
		updateIcons();
	}
	
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

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Game game = new Game();
	}

}
