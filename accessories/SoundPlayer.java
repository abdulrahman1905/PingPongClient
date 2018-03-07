package accessories;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer
{		
	public SoundPlayer()
	{		
	}
	
	public void playSelectedSound(String fileName)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{	
				try
				{
					URL url = this.getClass().getClassLoader().getResource(fileName);
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				}
				
				catch (UnsupportedAudioFileException e)
				{
					e.printStackTrace();
				}
				
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				catch (LineUnavailableException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}
