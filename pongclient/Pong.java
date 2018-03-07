package pongclient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import accessories.SoundPlayer;
import accessories.TopScore;

public class Pong implements ActionListener, WindowListener, KeyListener {

	//public static Pong pong;
	
	public JFrame gameWindow;
	public Timer timer;
	public GamePanel gamePanel;
	public final int WINDOW_WIDTH = 500;
	public final int WINDOW_HEIGHT = 500;
	public final int GAME_PANEL_BOTTOM_WALL = 490;
	public final int GAME_PANEL_TOP_WALL = 55;
	
	public Paddle player;
	public Paddle opponent;
	public Ball ball;
	public ConnectionHandler connection;
	public TopScore ts;
	public SoundPlayer sp;
	
	public int winner;
	public int livesLeft;
	public int scoreAgainstCPU;
	public int gameState = 0; //0-Game Over, 1-Game Playing, 2-Game Paused
	public int menuState = 0; //0-Game Mode Menu, 1-Difficulty Level, 2-Closed, 3-Connected
								//4-Game Point Menu, 5-Wait for Player 1, 6-connection lost
	public int gameMode = 1; //1-Player against CPU, 2-Player against Player
	public int difficulty = 0; //0-Easy, 1-Medium, 2-Hard
	public String modeString = "Single Player";
	public String diffString; 
	
	int x1[] = {300,320,280};int x2[] = {280,320,300};
	int y1[] = {220,240,240};int y2[] = {285,285,305};
	int[] scores;
	String[] names;
	String playerName;
	Boolean scoreSaved = false;
	Boolean isConnected = false;
	int playerNumber;
	int gamePoint = 1;
	int player1Point;
	int player2Point;
	String regex = "<:&!%#}>";
	int count = 0;
	public boolean player2Ready = false;
	public boolean moveRight,moveLeft;
	
	public static void main(String[] args){
		new Pong();
	}
	
	public Pong(){
		timer = new Timer(20, this);
		
		gameWindow = new JFrame("Pong Pong Game");
		gameWindow.setBackground(Color.GREEN);
		gameWindow.addWindowListener(this);
		gameWindow.addKeyListener(this);
		gamePanel = new GamePanel(this);
		gameWindow.setSize(WINDOW_WIDTH + 6, WINDOW_HEIGHT + 28);
		gameWindow.setResizable(false);
		gameWindow.add(gamePanel);
		gameWindow.setLocationRelativeTo(null);
		gameWindow.setVisible(true);
		
		ts = new TopScore();
		scores = ts.getScores();
		names = ts.getNames();
		sp = new SoundPlayer();
		
		timer.start();
	}
	
	public void startGame(){
		sp.playSelectedSound("files/GameStartOver.wav");
		gameState = 1;
		if(gameMode == 2){
			player1Point = 0;
			player2Point = 0;
			winner = 0;
		}
		else if(gameMode == 1){
			livesLeft = 3;
			scoreSaved = false;
			scoreAgainstCPU = 0;
		}
		player = new Paddle(this, 1);
		opponent = new Paddle(this, 2);
		ball = new Ball(this);
	}
	
	public void renderGamePanel(Graphics2D g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(5f));
		g.drawLine(0, 50, WINDOW_WIDTH, 50);
		g.drawLine(0, WINDOW_HEIGHT-5, WINDOW_WIDTH, WINDOW_HEIGHT-5);
		
		if(menuState == 0 || menuState == 1 || (gameState == 0 && menuState == 2)){
			//display top scores in both menus in single player and also when game over
			if(gameMode == 1 || !isConnected){
				g.setColor(Color.GREEN);
				g.setFont(new Font("Lucida Console", 4, 12));
				g.drawString("Top Scores: ", 5, 15);
				g.drawString(names[0] +" - "+ scores[0], 90, 15);
				g.drawString(names[1] +" - "+ scores[1], 90, 30);
				g.drawString(names[2] +" - "+ scores[2], 90, 45);
			}
		}
		if(menuState == 3 || menuState == 4 || menuState == 5 || gameState == 1 || gameState == 2 || (gameState == 0 && menuState == 2)){
			if(gameMode == 2){
				if(playerNumber == 1){
					g.setColor(Color.RED);
				}else if (playerNumber == 2){
					g.setColor(Color.BLUE);
				}
				g.setFont(new Font("Lucida Console", 1, 40));
				g.drawString(String.valueOf(playerNumber), 235, 40);
			}
		}
		
		if(menuState == 0){
			//mode Selection menu
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			g.drawString("Select Game Mode", 125, 200);
			g.fillPolygon(x1, y1, 3);
			g.fillPolygon(x2, y2, 3);
			g.drawString("Mode: " + modeString, 120, 270);
			g.drawString("Press Space to Continue", 93, 350);
		}
		else if(menuState == 1){
			//difficulty Selection menu
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			g.drawString("Select Difficulty", 145, 200);
			g.fillPolygon(x1, y1, 3);
			g.fillPolygon(x2, y2, 3);
			if(difficulty == 0){
				diffString = "Easy";
			}
			else if(difficulty == 1){
				diffString = "Medium";
			}
			else if(difficulty == 2){
				diffString = "Hard";
			}
			g.drawString("Difficulty: " + diffString, 150, 270);
			g.drawString("Press Space to Play", 120, 350);
			g.drawString("Press ESC for Main Menu", 80, 410);
		}
		else if(menuState == 3){
			//connected to server menu
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			g.drawString("Connected as Player " + playerNumber, 100, 200);
			g.drawString("Press SPACE to Continue", 75, 260);
		}
		else if(menuState == 4){
			//select game point limit menu (only in Player 1)
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			if(!player2Ready){
				g.drawString("Waiting for Player 2", 100, 250);
				if(count <= 19){
					g.drawString(".", 362, 250);
				}
				else if(count > 20 && count < 39){
					g.drawString("..", 362, 250);
				}
				else if(count > 40 && count < 59){
					g.drawString("...", 362, 250);
				}
			}
			else if(player2Ready){
				g.drawString("Select Game Point Limit", 90, 200);
				g.fillPolygon(x1, y1, 3);
				g.fillPolygon(x2, y2, 3);
				g.drawString("Game Point:   " + gamePoint, 90, 270);
				g.drawString("Press SPACE to Continue", 85, 350);
			}
		}
		else if(menuState == 5){
			//wait for player 1 menu (only in Player 2)
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			g.drawString("Waiting for Player 1", 100, 200);
			if(count <= 19){
				g.drawString(".", 362, 200);
			}
			else if(count > 20 && count < 39){
				g.drawString("..", 362, 200);
			}
			else if(count > 40 && count < 59){
				g.drawString("...", 362, 200);
			}
			g.drawString("Get Ready!", 170, 260);
		}
		else if(menuState == 6){
			//connection lost menu
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 30));
			g.drawString("Connection to Server Lost", 80, 200);
			g.drawString("Press ESC for Main Menu", 80, 260);
		}
		
		
		if(gameState == 1 || gameState == 2){
			//game paused
			if(gameState == 2){
				g.setColor(Color.GREEN);
				g.setFont(new Font("Arial", 4, 25));
				g.drawString("Game Paused", 160, 220);
				g.drawString("Press SPACE to Resume", 100, 260);
				g.drawString("Press ESC for Main Menu", 98, 300);
			}
			//game playing and paused
			if(gameMode == 1){
				g.setColor(Color.GREEN);
				g.setFont(new Font("Lucida Console", 4, 12));
				g.drawString("Press SPACE to PAUSE", 5, 15);
				g.drawString("Game Mode: " + modeString, 5, 30);
				g.drawString("Highest Score: " + ts.getTopScore() , 5, 45);
				g.drawString("Difficulty Level: " + diffString, 325, 15);
				g.drawString("Score: " + scoreAgainstCPU, 325, 30);
				g.drawString("Lives: " + livesLeft, 325, 45);
			}else if(gameMode == 2){
				g.setColor(Color.GREEN);
				g.setFont(new Font("Lucida Console", 4, 12));
				g.drawString("Press SPACE to PAUSE", 5, 15);
				g.drawString("Game Mode: " + modeString, 5, 30);
				g.drawString("Game Point: " + gamePoint, 5, 45);
				g.setColor(Color.RED);
				g.drawString("Player 1: " + player1Point, 390, 20);
				g.setColor(Color.BLUE);
				g.drawString("Player 2: " + player2Point, 390, 40);
			}
			
			player.render(g);
			opponent.render(g);
			ball.render(g);
		}
		
		else if(gameState == 0 && menuState == 2){
			//game over
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", 4, 25));
			g.drawString("Game Over", 190, 150);
			if(gameMode == 1){
				if(ts.checkScore(scoreAgainstCPU) == 0){
					if(!scoreSaved){
						g.drawString("New High Score: " + scoreAgainstCPU, 150, 200);
						g.drawString("Press SHIFT to Save Score", 100, 240);
					}
				}
				else if(ts.checkScore(scoreAgainstCPU) == 1){
					if(!scoreSaved){
						g.drawString("New Second Best Score: " + scoreAgainstCPU, 100, 200);
						g.drawString("Press SHIFT to Save Score", 100, 240);
					}
				}
				else if(ts.checkScore(scoreAgainstCPU) == 2){
					if(!scoreSaved){
						g.drawString("New Third Best Score: " + scoreAgainstCPU, 100, 200);
						g.drawString("Press SHIFT to Save Score", 100, 240);
					}
				}
				else{
					if(!scoreSaved)
						g.drawString("Score: " + scoreAgainstCPU, 200, 200);
				}
				
				if(scoreSaved){
					g.drawString("Score Saved", 181, 240);
				}
			}
			else if(gameMode == 2){
				g.setColor(Color.RED);
				g.drawString("Player 1 - "+player1Point , 190, 200);
				g.setColor(Color.BLUE);
				g.drawString("Player 2 - "+player2Point , 190, 240);
				g.setColor(Color.GREEN);
				if(winner == 1){
					if(playerNumber == 1){
						g.drawString("You Win!", 200, 280);
					}else if(playerNumber == 2){
						g.drawString("Player 1 Wins!", 175, 280);
					}
				}
				else if(winner == 2){
					if(playerNumber == 1){
						g.drawString("Player 2 Wins!", 175, 280);
					}else if(playerNumber == 2){
						g.drawString("You Win!", 200, 280);
					}
				}
				if(playerNumber == 1){
					g.drawString("Press SHIFT to change Game Point", 50, 350);
				}
			}
			if(gameMode == 1 || (gameMode == 2 && playerNumber == 1)){
				g.drawString("Press SPACE to Restart", 115, 390);
				g.drawString("Press ESC for Main Menu", 110, 430);
			}
		}
	}

	private void updateGame() {
		if(moveRight){
			player.movePaddle(true);
			if(isConnected && gameMode == 2){
				connection.writeToServer("paddle_pos" + regex + player.x);
			}
		}
		if(moveLeft){
			player.movePaddle(false);
			if(isConnected && gameMode == 2){
				connection.writeToServer("paddle_pos" + regex + player.x);
			}
		}
		
		ball.update();
		
		if(gameMode == 1){
			opponent.updateCPUPaddle(ball);
			if(livesLeft < 0){
				gameState = 0;
				sp.playSelectedSound("files/GameStartOver.wav");
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//to perform every 20 milliseconds by Timer
		if(isConnected && gameMode == 2){
			connection.readFromServer();
		}
		
		if(gameState == 1){
			updateGame();
		}
		
		gamePanel.repaint();
		
		scores = ts.getScores();
		names = ts.getNames();
		
		count++;
		if(count == 60){
			count = 0;
		}
	}
	@Override
	public void keyPressed(KeyEvent evt) {
		if(evt.getKeyCode() == KeyEvent.VK_RIGHT){
			moveRight = true;
		}
		else if (evt.getKeyCode() == KeyEvent.VK_LEFT){
			moveLeft = true;
		}
		else if (evt.getKeyCode() == KeyEvent.VK_UP){
			if(menuState == 0){
				if(gameMode == 1){
					gameMode = 2;
					modeString = " Multiplayer ";
				}
				else if(gameMode == 2){
					gameMode = 1;
					modeString = "Single Player";
				}
			}
			else if(menuState == 1){
				if(difficulty > 0){
					difficulty --;
				}
				else{
					difficulty = 2;
				}
			}
			else if(menuState == 4){
				if(player2Ready){
					gamePoint++;
					if(gamePoint > 50){
						gamePoint = 1;
					}
				}
			}
		}
		else if (evt.getKeyCode() == KeyEvent.VK_DOWN){
			if(menuState == 0){
				if(gameMode == 1){
					gameMode = 2;
					modeString = "  Multiplayer  ";
				}
				else if(gameMode == 2){
					gameMode = 1;
					modeString = "Single Player";
				}
			}
			else if(menuState == 1){
				if(difficulty < 2){
					difficulty ++;
				}
				else{
					difficulty = 0;
				}
			}
			else if(menuState == 4){
				if(player2Ready){
					gamePoint--;
					if(gamePoint < 1){
						gamePoint = 50;
					}
				}
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent evt) {
		if(evt.getKeyCode() == KeyEvent.VK_RIGHT){
			moveRight = false;
		}
		else if (evt.getKeyCode() == KeyEvent.VK_LEFT){
			moveLeft = false;
		}
		if(evt.getKeyCode() == KeyEvent.VK_SPACE){
			if(menuState == 0){
				if(gameMode == 1){
					menuState = 1;
				}
				else{
					connection = new ConnectionHandler(this);
				}
			}
			else if(menuState == 1){
				menuState = 2;
				startGame();
			}
			else if(gameState == 1){
				gameState = 2;
				if(gameMode == 2){
					connection.writeToServer("pause");
				}
			}
			else if(gameState == 2){
				gameState = 1;
				if(gameMode == 2){
					connection.writeToServer("resume");
				}
			}
			else if(menuState == 3){
				if(playerNumber == 1){
					menuState = 4;
				}else if(playerNumber == 2){
					connection.writeToServer("ready");
					menuState = 5;
				}
			}
			else if(menuState == 4 && player2Ready){
				connection.initiateGame();
			}
			else if(menuState == 5){
				
			}
			else if(menuState == 6){
				
			}
			else if(gameState == 0 && menuState == 2){
				if(gameMode == 1){
					startGame();
				}else if(gameMode == 2 && playerNumber == 1){
					connection.initiateGame();
				}
			}
		}
		else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE){
			if(gameState == 0 && menuState == 2){
				if(gameMode == 1){
					menuState = 0;
				}else if(gameMode == 2 && playerNumber == 1){
					if(isConnected){
						connection.writeToServer("disconnect");
						isConnected = false;
						try {
							connection.input.close();
							connection.output.close();
							connection.socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					menuState = 0;
				}
			}
			else if(menuState == 1 || menuState == 6){
				menuState = 0;
			}
			else if(gameState == 2){
				gameState = 0;
				menuState = 0;
				if(gameMode == 2){
					connection.writeToServer("disconnect");
				}
			}
		}
		else if(evt.getKeyCode() == KeyEvent.VK_SHIFT){
			if(gameState == 0 && menuState == 2){
				if(gameMode == 1){
					if(!scoreSaved){
						if(ts.checkScore(scoreAgainstCPU) == 0 || ts.checkScore(scoreAgainstCPU) == 1 || ts.checkScore(scoreAgainstCPU) == 2){
							//if score is one of the best scores
							playerName = (String)JOptionPane.showInputDialog(gameWindow, "Enter your name", "Save Score", JOptionPane.PLAIN_MESSAGE);
							if(playerName != null && playerName.length() > 0){
								ts.addScore(playerName, scoreAgainstCPU);
								scoreSaved = true;
							}
						}
					}
				}
				else if(gameMode == 2 && playerNumber == 1){
					menuState = 4;
				}
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent evt) {	
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(isConnected){
			connection.writeToServer("disconnect");
		}
		System.exit(0);
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
