import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class MorseEncoder {
	private HashMap<Character, String> morseDict;
	private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz1234567890";
	private static final String[] CODES = {".-", "-...", "-.-.", "-..", ".", 		//ABCDE
											"..-.", "--.", "....", "..", ".---",	//FGHIJ
											"-.-", ".-..", "--", "-.", "---",		//KLMNO
											".--.", "--.-", ".-.", "...", "-",		//PQRST
											"..-", "...-", ".--", "-..-", "-.--", "--..", //UVWXYZ
											".----", "..---", "...--", "....-", ".....",  //12345
											"-....", "--...", "---..", "----.", "-----"}; //67890	
	
	public MorseEncoder(){
		morseDict = new HashMap<Character, String>();
		
		for (int i=0; i<LETTERS.length(); i++){
			morseDict.put(LETTERS.charAt(i), CODES[i]);
		}	
	}
	
	public void encodeInput(){
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter text to be converted to morse code: ");
		String s = scan.nextLine();
		scan.close();
		
		s = s.toLowerCase();
		String output = "";
		
		for(int i=0; i<s.length(); i++){
			if (Character.isWhitespace(s.charAt(i))){
				output += "/";
			} else {
				output += morseDict.get(s.charAt(i));
			}
		}
		
		System.out.println(output);
		saveEncoding(output);
	}
	
	public void saveEncoding(String encoding){
		ArrayList<Byte> rawData = new ArrayList<Byte>();
		for(int i=0; i<encoding.length(); i++){
			int soundLength = 0;
			
			if (Character.toString(encoding.charAt(i)).equals("/")){
				
				// add empty bytes for no sound between words
				for (int j=0; j < 10000; j++){
					rawData.add(new Byte ((byte)0));
				}
			} else {
				if (Character.toString(encoding.charAt(i)).equals("-")){
					soundLength = 600;
				} else if (Character.toString(encoding.charAt(i)).equals(".")){
					soundLength = 300;
				}
				
				// add beeps for letters
                for ( int k = 0; k < soundLength * (float)44100 / 1000; k++ ) {
                    double angle = k / ( (float)44100 / 440 ) * 2.0 * Math.PI;
                    rawData.add( (byte)( Math.sin( angle ) * 100 ) );
                }
				
			}

			
			// add break between chars
			for (int j=0; j < 2000; j++){
				rawData.add(new Byte ((byte)0));
			}
		}
		
		byte[] audio = new byte[rawData.size()];
		for (int i=0; i<rawData.size(); i++){
			audio[i] = rawData.get(i).byteValue();
		}
        InputStream byteArrayInputStream = new ByteArrayInputStream( audio );
        AudioFormat audioFormat = new AudioFormat( (float)44100, 8, 1, true, false );
        AudioInputStream audioInputStream = new AudioInputStream( byteArrayInputStream, audioFormat, audio.length / audioFormat.getFrameSize() );
        try {
            AudioSystem.write( audioInputStream, AudioFileFormat.Type.WAVE, new File( "morse-code.wav" ));
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
		
		
		
	}
	

}
