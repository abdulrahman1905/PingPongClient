package accessories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TopScore {
	File scoreFile;
	PrintWriter output;
	FileReader reader;
	BufferedReader input;
	
	String[] scoreText;
	int[] score;
	
	public int checkScore(int scoreToCheck){
		scoreText = new String[3];
		score = new int[3];
		int returnValue = -1;
		scoreFile = new File(System.getProperty("user.dir"), "pongscores.txt");
		try {
			reader = new FileReader(scoreFile);
			input = new BufferedReader(reader);
			for (int i = 0; i < 3; i++){
				scoreText[i] = input.readLine();
				String[] temp = scoreText[i].split("<:&!%#}>");
				score[i] = Integer.parseInt(temp[1]);
				if(scoreToCheck > score[i]){
					returnValue = i; //0-1st, 1-2nd, 2-3rd
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public int[] getScores(){
		scoreText = new String[3];
		score = new int[3];
		scoreFile = new File(System.getProperty("user.dir"), "pongscores.txt");
		try {
			if(!scoreFile.exists()){
				output = new PrintWriter(scoreFile);
				for(int i = 0; i < 3; i++){
					output.println("Player "+(i+1)+"<:&!%#}>"+"0");
					output.flush();
					System.out.println("Player "+(i+1)+"<:&!%#}>"+"0");
				}
				output.close();
			}
			reader = new FileReader(scoreFile);
			input = new BufferedReader(reader);
			for (int i = 0; i < 3; i++){
				scoreText[i] = input.readLine();
				String[] temp = scoreText[i].split("<:&!%#}>");
				score[i] = Integer.parseInt(temp[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return score;
	}
	
	public String[] getNames(){
		scoreText = new String[3];
		scoreFile = new File(System.getProperty("user.dir"), "pongscores.txt");
		try {
			reader = new FileReader(scoreFile);
			input = new BufferedReader(reader);
			for (int i = 0; i < 3; i++){
				scoreText[i] = input.readLine();
				String[] temp = scoreText[i].split("<:&!%#}>");
				scoreText[i] = temp[0];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scoreText;
	}
	
	public int getTopScore(){
		int[] temp = getScores();
		return temp[0];
	}
	
	public void addScore(String nameToAdd, int scoreToAdd){
		String tempName;
		int tempScore;
		String[] tempNames = getNames();
		int[] tempScores = getScores();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for(String n : tempNames)
			names.add(n);
		names.add(nameToAdd);
		for(int s : tempScores)
			scores.add(s);
		scores.add(scoreToAdd);
		
		for(int i = 3; i > 0; i--){
			if(scores.get(i) > scores.get(i-1)){
				tempScore = scores.get(i-1); tempName = names.get(i-1);
				scores.set(i-1, scores.get(i)); names.set(i-1, names.get(i));
				scores.set(i, tempScore); names.set(i, tempName);
			}
		}
		names.remove(names.size()-1);
		scores.remove(scores.size()-1);
		
		scoreFile = new File(System.getProperty("user.dir"), "pongscores.txt");
		try {
			output = new PrintWriter(scoreFile);
			for(int i = 0; i < scores.size(); i++){
				output.println(names.get(i)+"<:&!%#}>"+scores.get(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			output.flush();
			output.close();
		}
	}
}
