package pongclient;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel{

	Pong pong;
	private static final long serialVersionUID = 1L;
	
	public GamePanel(Pong pong){
		this.pong = pong;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		pong.renderGamePanel((Graphics2D) g);
	}
}
