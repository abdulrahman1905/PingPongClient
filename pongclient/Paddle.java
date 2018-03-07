package pongclient;

import java.awt.Color;
import java.awt.Graphics;

public class Paddle {

	public int playerNumber;
	public final int WIDTH = 120;
	public final int HEIGHT = 20;
	public int x;
	public int y;
	public int paddleSpeed;
	private Pong pong;
	
	public Paddle(Pong pong, int playerNumber){
		this.pong = pong;
		this.playerNumber = playerNumber;
		if(playerNumber == 1){
			//the player at the bottom
			y = pong.GAME_PANEL_BOTTOM_WALL - HEIGHT;
			paddleSpeed = 15;
		}
		else if(playerNumber == 2){
			//the opponent at the top
			y = pong.GAME_PANEL_TOP_WALL;
			
			if(pong.difficulty == 0){
				paddleSpeed = 8;
			}else if(pong.difficulty == 1){
				paddleSpeed = 11;
			}else if(pong.difficulty == 2){
				paddleSpeed = 14;
			}
		}
		x = (pong.WINDOW_WIDTH/2) - (WIDTH/2);
	}
	
	public void movePaddle(boolean moveRight){
		//move by player
		if(moveRight){
			if((x + WIDTH + paddleSpeed) < pong.WINDOW_WIDTH){
				x = x + paddleSpeed;
			}
			else{
				x = pong.WINDOW_WIDTH - WIDTH;
			}
		}
		else{
			if(x - paddleSpeed > 0){
				x = x - paddleSpeed;
			}
			else{
				x = 0;
			}
		}
	}
	public void updateCPUPaddle(Ball ball){
		//move by opponent
		if(x + WIDTH/2 < ball.x){
			movePaddle(true);
		}
		if(x + WIDTH/2 > ball.x){
			movePaddle(false);
		}
	}
	
	public void render(Graphics g){
		if(pong.gameMode == 2){
			if(pong.playerNumber == 1){
				if(playerNumber == 1){
					g.setColor(Color.RED);
				}else if(playerNumber == 2){
					g.setColor(Color.BLUE);
				}
				
			}else if(pong.playerNumber == 2){
				if(playerNumber == 1){
					g.setColor(Color.BLUE);
				}else if(playerNumber == 2){
					g.setColor(Color.RED);
				}
			}
		}else if(pong.gameMode == 1){
			g.setColor(Color.WHITE);
		}
		g.fillRect(x, y, WIDTH, HEIGHT);
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void setX(int x){
		this.x = x;
	}
}
