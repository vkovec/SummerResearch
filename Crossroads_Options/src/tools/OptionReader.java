package tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author vkovec
 * Reads in options from a text file.
 */
public class OptionReader{
	
	private BufferedReader reader;
	
	public OptionReader(){
		try {
			reader = new BufferedReader(new FileReader("options.txt"));
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
	}
	
	/**
	 * @return the options read in from the text file.
	 */
	public ArrayList<Option> getOptions(){
		ArrayList<Option> ops;
		ops = new ArrayList<Option>();
		
		String line = null;
		try {
			int size = (10*10)/2 + 1;
			
			String name = null;
			ArrayList<Integer> ini = null;
			ArrayList<String> pol = null;
			
			boolean passedGoal = false;
			while ((line = reader.readLine()) != null) {
				
				if(line.equals("")){
					continue;
				}
				
				//we have found a new option
				if(line.charAt(0) == 'o'){
					if(passedGoal){
						int[] in = new int[ini.size()];
						for(int i = 0; i < in.length; i++){
							in[i] = ini.get(i);
						}
						ops.add(new Option(name, size, in, pol.toArray(new String[0])));
					}
					name = line;
					ini = new ArrayList<Integer>();
					pol = new ArrayList<String>();
					passedGoal = false;
				}
				else if(line.charAt(0) == 'g'){
					passedGoal = true;
				}
				else if(passedGoal){
					//the policy
					pol.add(line.substring(line.indexOf(' ')+1, line.indexOf(',')));
				}
				else{
					//the initiation sets
					ini.add(Integer.parseInt(line));
				}
			
			}
			
			int[] in = new int[ini.size()];
			for(int i = 0; i < in.length; i++){
				in[i] = ini.get(i);
			}
			ops.add(new Option(name, size, in, pol.toArray(new String[0])));
			
		} catch (IOException e) {}
		
		return ops;
	}

}