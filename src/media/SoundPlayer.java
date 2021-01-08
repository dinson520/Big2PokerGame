package media;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;

public class SoundPlayer {
	private static SoundPlayer soundManager = new SoundPlayer();
	private Clip backgroundMusic;  //背景音樂 一次只能撥一首
	
	public static SoundPlayer getSoundManager(){
		return soundManager;
	}
	
	public void playSound(String path){
		 try {   
			new Thread(new Runnable() {
				 Clip soundClip = AudioSystem.getClip();
				    public void run() {
				      try {
				    	soundClip.open(AudioSystem.getAudioInputStream(new File(path).toURL()));
				  		soundClip.start();
				      } catch (Exception e) {
				        soundClip.close();
				     
				      }
				    }
				  }
			 ).start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * play the background music, the background music can only 
	 * exits only one at the same time.
	 */
	public void playLoopMusic(String path){
		try {
			if ( backgroundMusic != null && backgroundMusic.isOpen() )
				backgroundMusic.close();
			backgroundMusic = AudioSystem.getClip();
			backgroundMusic.open(AudioSystem.getAudioInputStream(new File(path).toURL()));
			backgroundMusic.loop(-1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
}
