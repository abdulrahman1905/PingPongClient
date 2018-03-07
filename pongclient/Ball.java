package pongclient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Ball {
	public final int DIAMETER = 20;
	public int x, y, dx, dy;
	public Random random;
	private Pong pong;
	public Rectangle opponentRect;
	public Rectangle playerRect;
	public Rectangle ballRect;
	public int tempHitCounts;
	public int speed;
	public int scoreInc;
	
	public Ball(Pong pong){
		this.pong = pong;
		opponentRect = new Rectangle();
		playerRect = new Rectangle();
		ballRect = new Rectangle();
		if(pong.gameMode == 1){
			random = new Random();
			resetBall();
		}
	}
	public void update(){
		if(pong.gameMode == 1){
			if(pong.difficulty == 0){
				speed = 5;
				scoreInc = 3;
			}
			else if(pong.difficulty == 1){
				speed = 6;
				scoreInc = 4;
			}
			else if(pong.difficulty == 2){
				speed = 7;
				scoreInc = 5;
			}
			x = x + dx * speed;
			y = y + dy * speed;
		}
		//side walls
		if(x + dx < 0 || x + DIAMETER - dx > pong.WINDOW_WIDTH){
			if(pong.gameMode == 1){
				if(dx < 0){
					//moving left
					x = 0;
					dx = random.nextInt(4);
					if(dx == 0){
						dx = 1;
					}	
				}
				else{
					//moving right
					x = pong.WINDOW_WIDTH - DIAMETER;
					dx = -random.nextInt(4);
					if(dx == 0){
						dx = -1;
					}
				}
				pong.sp.playSelectedSound("files/BallSideWalls.wav");
			}
		}
		if(pong.gameMode == 2){
			if(x == 0 || x == pong.WINDOW_WIDTH - DIAMETER){
				pong.sp.playSelectedSound("files/BallSideWalls.wav");
			}
		}
		//top wall (opponent)
		if(y + dy < pong.GAME_PANEL_TOP_WALL){
			if(pong.gameMode == 1){
				dy = 1 + (tempHitCounts/20);
				dx = -2 + random.nextInt(4);
				if(dx == 0){
					dx = 1;
				}
				pong.scoreAgainstCPU += scoreInc;
				tempHitCounts = 0;
			}
			pong.sp.playSelectedSound("files/BallTopButtomWalls.wav");
		}
		//bottom wall (player)
		if(y + DIAMETER + dy > pong.GAME_PANEL_BOTTOM_WALL){
			if(pong.gameMode == 1){
				dy = -1 - (tempHitCounts/20);
				dx = -2 + random.nextInt(4);
				if(dx == 0){
					dx = 1;
				}
				pong.livesLeft--;
				tempHitCounts = 0;
			}
			pong.sp.playSelectedSound("files/BallTopButtomWalls.wav");
		}
		ballRect.setBounds(x, y, DIAMETER, DIAMETER);
		opponentRect.setBounds(pong.opponent.getX(), pong.opponent.getY(), 
				pong.opponent.WIDTH, pong.opponent.HEIGHT);
		playerRect.setBounds(pong.player.getX(), pong.player.getY(), 
				pong.player.WIDTH, pong.player.HEIGHT);
		//opponent paddle and ball collision
		if(opponentRect.intersects(ballRect)){
			if(pong.gameMode == 1){
				dy = 1 + (tempHitCounts/20);
				dx = -2 + random.nextInt(4);
				if(dx == 0){
					dx = 1;
				}
				tempHitCounts++;
			}
			pong.sp.playSelectedSound("files/BallPaddle.wav");
		}
		//player paddle and ball collision
		if(playerRect.intersects(ballRect)){
			if(pong.gameMode == 1){
				dy = -1 - (tempHitCounts/20);
				dx = -2 + random.nextInt(4);
				if(dx == 0){
					dx = 1;
				}
				tempHitCounts++;
				pong.scoreAgainstCPU = pong.scoreAgainstCPU + scoreInc;
			}
			pong.sp.playSelectedSound("files/BallPaddle.wav");
		}
	}
	private void resetBall() {
		tempHitCounts = 0;
		//ball in the middle of the park
		x = (pong.WINDOW_WIDTH/2)-(DIAMETER/2);
		y = ((pong.GAME_PANEL_BOTTOM_WALL+pong.GAME_PANEL_TOP_WALL)/2)-(DIAMETER/2);
		
		//ball always go to CPU first
		dy = -1;
		
		//returns true or false
		if(random.nextBoolean()){
			dx = 1;
		}
		else{
			dx = -1;
		}
	}
	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.fillOval(x, y, DIAMETER, DIAMETER);
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
	public void setY(int y){
		this.y = y;
	}
}
