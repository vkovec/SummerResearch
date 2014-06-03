package agent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import tools.IEnvironment;

/**
 * @author vkovec
 *
 */
public abstract class Agent{
	protected PrintWriter writer;
	
	protected IEnvironment env;
	
	protected double[] values = null;
	protected int startState;
	protected int goalState;
	
	protected String[] actions = {"up", "down", "left", "right", "odown", "oup"};
	//protected String[] actions = {"up",  "down", "left", "right", "odown", "oright"};
	
	//just trying something to avoid performing options in states not in the initiation set
	protected String[] actions1 = {"up", "down", "left", "right"};
	protected String[] actions2 = {"up", "down", "left", "right", "oup"};
	protected String[] actions3 = {"up", "down", "left", "right", "odown"};
	
	//for stochastic policies
	//(i.e. probability of taking action a in state s)
	protected double[][] sPolicy = null;
	
	//the Q-values that we find after learning
	protected double[][] qValues = null;
	
	protected int getActionIndex(String action){
		for(int i = 0; i < actions.length; i++){
			if(actions[i].equals(action)){
				return i;
			}
		}
		return -1;
	}
	
	public void setEnv(IEnvironment e, int start, int goal){
		env = e;
		startState = start;
		goalState = goal;
	}
	
	public Agent(int n){
		try {
			writer = new PrintWriter("cs2.txt", "UTF-8");
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(values == null){
			values = new double[2*n-1];
			//values = new double[n*n];
		}
		if(qValues == null){
			qValues = new double[2*n-1][actions.length];
			//qValues = new double[n*n][actions.length];
		}
		if(sPolicy == null){
			sPolicy = new double[2*n-1][actions.length];
			//sPolicy = new double[n*n][actions.length];
		}
	}
	
	public double[] getValues(){
		return values;
	}
	
	public double[][] getQValues(){
		return qValues;
	}
	
	/**
	 * @return the policy
	 */
	public String[] getPolicy(){
		String[] policy = new String[values.length];
		//use stochastic policy to get deterministic policy
		for(int x = 0; x < sPolicy.length; x++){
			int action = 0;
			for(int a = 0; a < actions.length; a++){
				if(sPolicy[x][a] > sPolicy[x][action]){
					action = a;
				}
			}
			switch(action){
				case 0: 
					policy[x] = "^";
					break;
				case 1:
					policy[x] = "v";
					break;
				case 2:
					policy[x] = "<";
					break;
				case 3:
					policy[x] = ">";
					break;
				case 4:
					policy[x] = "^^";
					break;
				case 5:
					policy[x] = "vv";
					break;
			}
		}
		
		return policy;
	}
	
	public abstract void learn(int steps);
	
	public void learnTrial(int eps){
		for(int i = 0; i < eps; i++){
			learn(1000);
			//print the policy for a given state to a file
			printPolicyToFile(2);
			
			env.gotoState(startState);
		}
		for(int i = 0; i < sPolicy.length; i++){
			for(int j = 0; j < actions.length; j++){
				System.out.println("State: " + i + ", Action " + actions[j] + ": " + sPolicy[i][j]);
			}
		}
		writer.close();
	}
	
	public void printPolicyToFile(int s){
		writer.println("");
		for(int i = 0; i < actions.length; i++){
			writer.print(sPolicy[s][i] + ",");
		}
	}
}