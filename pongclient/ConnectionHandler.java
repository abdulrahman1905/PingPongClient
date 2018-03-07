package pongclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ConnectionHandler implements ActionListener, KeyListener{

	public JLabel ipLabel;
	public JLabel portLabel;
	public JTextField serverIP;
	public JTextField port;
	public JButton connectButton;
	public JButton cancelButton;
	public JDialog connectionDialog;
	
	Socket socket;
	PrintWriter output;
	InputStreamReader isr;
	BufferedReader input;
	
	Pong pong;
	String serverIPAdress;
	int portNumber;
	String messageFromServer;
	String regex = "<:&!%#}>";
	
	public ConnectionHandler(Pong pong) {
		this.pong = pong;
		connectionDialog = new JDialog(pong.gameWindow, "Connection", true);
		connectionDialog.setSize(295,145);
		connectionDialog.setLayout(null);
		connectionDialog.setResizable(false);
		
		ipLabel = new JLabel("Server IP Address:");
		portLabel = new JLabel("Port Number:");
		serverIP = new JTextField("127.0.0.1");
		port = new JTextField("9900");
		connectButton = new JButton("Connect");
		cancelButton = new JButton("Cancel");
		
		connectButton.addActionListener(this);
		cancelButton.addActionListener(this);
		//connectionDialog.addKeyListener(this);
		serverIP.addKeyListener(this);
		port.addKeyListener(this);
		
		ipLabel.setBounds(10,10 , 110, 25);
		portLabel.setBounds(10, 40, 110, 25);
		serverIP.setBounds(120, 10, 150, 25);
		port.setBounds(120, 40, 150, 25);
		connectButton.setBounds(85, 70, 90, 25);
		cancelButton.setBounds(179, 70, 90, 25);
		
		connectionDialog.add(ipLabel);
		connectionDialog.add(portLabel);
		connectionDialog.add(serverIP);
		connectionDialog.add(port);
		connectionDialog.add(connectButton);
		connectionDialog.add(cancelButton);
		
		connectionDialog.setLocationRelativeTo(null);
		connectionDialog.setVisible(true);
	}
	
	public void connect(){
		if(!"".equals(serverIP.getText()) && !"".equals(port.getText())){
			serverIPAdress = serverIP.getText();
			portNumber = Integer.parseInt(port.getText());
			try {
				socket = new Socket(serverIPAdress, portNumber);
				connectionDialog.dispose();
				
				isr = new InputStreamReader(socket.getInputStream());
				input = new BufferedReader(isr);
				output = new PrintWriter(socket.getOutputStream());
				
				pong.playerNumber = Integer.parseInt(input.readLine());
				pong.menuState = 3;
				pong.isConnected = true;
				System.out.println("Connected");
				
			} catch (UnknownHostException | ConnectException e) {
				JOptionPane.showMessageDialog(connectionDialog, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(connectionDialog, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			
		}
	}
	
	public void initiateGame(){
		writeToServer("start" + regex + pong.gamePoint);
		try {
				messageFromServer = input.readLine();
				System.out.println(messageFromServer);
				if(messageFromServer.startsWith("ok")){
					pong.menuState = 2;
					pong.startGame();
				}
				else if(messageFromServer.startsWith("wait")){
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeToServer(String message){
		output.println(message);
		output.flush();
	}
	public void readFromServer(){
		try {
			if(input.ready()){
				messageFromServer = input.readLine();
				System.out.println(messageFromServer);
				
				if(messageFromServer.startsWith("start")){
					String[] temp = messageFromServer.split(regex);
					pong.gamePoint = Integer.parseInt(temp[1]);
					pong.menuState = 2;
					pong.startGame();
				}
				else if(messageFromServer.startsWith("ready")){
					pong.player2Ready = true;
				}
				else if (messageFromServer.startsWith("ballPos")){
					String[] temp = messageFromServer.split(regex);
					int ballXPos = Integer.parseInt(temp[1]);
					int ballYPos = Integer.parseInt(temp[2]);
					int oppPaddlePos = Integer.parseInt(temp[3]);
					pong.ball.setX(ballXPos);
					pong.ball.setY(ballYPos);
					pong.opponent.setX(oppPaddlePos);
					pong.player1Point = Integer.parseInt(temp[4]);
					pong.player2Point = Integer.parseInt(temp[5]);
					pong.winner = Integer.parseInt(temp[6]);
					if(pong.winner == 1 || pong.winner == 2){
						writeToServer("endACK");
						pong.gameState = 0;
						pong.sp.playSelectedSound("files/GameStartOver.wav");
					}
				}
				else if(messageFromServer.startsWith("pause")){
					pong.gameState = 2;
				}
				else if(messageFromServer.startsWith("resume")){
					pong.gameState = 1;
				}
				else if(messageFromServer.startsWith("stopped")){
					//sever closed, go to server close menu, press escape to main menu
					pong.menuState = 6;
					pong.isConnected = false;
					pong.player2Ready = false;
					pong.gameState = 0;
					
					input.close();
					output.close();
					socket.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(connectButton)){
			connect();
		}
		else if(e.getSource().equals(cancelButton)){
			connectionDialog.dispose();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			connectButton.doClick();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
