package agent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Random;

import tools.IEnvironment;
import tools.Option;

/**
 * @author vkovec
 *
 */
public abstract class Agent{
	
	private boolean isI = false;
	
	//temporary
	protected double[][] ps;
	public double alpha = 10.0;
	
	protected boolean isGrid = true;
	protected boolean withPa = true;
	
	protected boolean average = false;
	
	private double[][][] avgPol;
	private double[][][] avgQ;
	
	protected int[] stateCount;
	
	//the state we want to examine more closely
	protected int stat;
	
	protected int[] actionDist = new int[6];
	
	protected PrintWriter writer;
	protected PrintWriter qWriter;
	protected PrintWriter psWriter;
	protected PrintWriter paWriter;
	protected PrintWriter pjWriter;
	protected PrintWriter DWriter;
	protected PrintWriter polWriter;
	
	protected IEnvironment env;
	
	protected double[] values = null;
	protected int startState;
	protected int goalState;
	
	protected int timeSteps = 1000;
	
	protected String[] actions = new String[6];
	
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
		
		actions = new String[4 + env.howManyOptions()];
		
		actions[0] = "up";
		actions[1] = "down";
		actions[2] = "left";
		actions[3] = "right";
		
		Enumeration<Option> opts = env.getOptions();
		int i = 4;
		while(opts.hasMoreElements()){
			actions[i] = opts.nextElement().getName();
			i++;
		}
		
		qValues =  new double[qValues.length][actions.length];
		
		env.getQVals(qValues);
		
		for(int j = 0; j < actions.length; j++){
			System.out.println(actions[j]);
		}
	}
	
	public Agent(int n){
		
		try {
			if(isGrid){
				writer = new PrintWriter("gc2.txt", "UTF-8");
				qWriter = new PrintWriter("gc2q.txt", "UTF-8");
				polWriter = new PrintWriter("gc2pol.txt", "UTF-8");
				
				psWriter = new PrintWriter("gc2ps.txt", "UTF-8");
				paWriter = new PrintWriter("gc2pa.txt", "UTF-8");
				pjWriter = new PrintWriter("gc2pj.txt", "UTF-8");
				DWriter = new PrintWriter("gc2D.txt", "UTF-8");
			}
			else{
				writer = new PrintWriter("cs2.txt", "UTF-8");
				qWriter = new PrintWriter("cs2q.txt", "UTF-8");
				
				psWriter = new PrintWriter("cs2ps.txt", "UTF-8");
				paWriter = new PrintWriter("cs2pa.txt", "UTF-8");
				pjWriter = new PrintWriter("cs2pj.txt", "UTF-8");
				DWriter = new PrintWriter("cs2D.txt", "UTF-8");
			}
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(values == null){
			if(!isGrid){
				values = new double[2*n-1];
				stateCount = new int[2*n-1];
			}
			else{
				values = new double[n*n];
				stateCount = new int[n*n];
			}
		}
		if(qValues == null){
			if(!isGrid){
				qValues = new double[2*n-1][actions.length];
				avgQ = new double[timeSteps][2*n-1][actions.length];
			}
			else{
				qValues = new double[n*n][actions.length];
				avgQ = new double[timeSteps][n*n][actions.length];
			}
		}
		if(sPolicy == null){
			if(!isGrid){
				sPolicy = new double[2*n-1][actions.length];
				avgPol = new double[timeSteps][2*n-1][actions.length];
			}
			else{
				sPolicy = new double[n*n][actions.length];
				avgPol = new double[timeSteps][n*n][actions.length];
			}
		}
	}
	
	public double[] getValues(){
		return values;
	}
	
	public double[][] getQValues(){
		return qValues;
	}
	
	public double[][] getSPolicy(){
		return sPolicy;
	}
	
	public int[] getStateCount(){
		return stateCount;
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
				default:
					String act = actions[action];
					if(act.equals("oup")){
						policy[x] = "vv";
					}
					else if(act.equals("odown")){
						policy[x] = "^^";
					}
					else if(act.equals("oright")){
						policy[x] = ">>";
					}
					else if(act.equals("oleft")){
						policy[x] = "<<";
					}
					else if(act.equals("old")){
						policy[x] = "v<";
					}
					else if(act.equals("ord")){
						policy[x] = ">v";
					}
					else{
						policy[x] = actions[action];
					}
					break;
			}
		}
		
		return policy;
	}
	
	/**
	 * For learning the model without any Q values.
	 * Random exploration around the environment.
	 * @param steps
	 */
	public abstract void preLearn(int steps);
	
	public abstract void learn(int steps);
	
	public void learnTrial(int eps){
		
		randomizePolicy(sPolicy);
		//qValues =  new double[qValues.length][actions.length];
		
		Random rand = new Random();
		
		//int stat;
		if(!isGrid){
			stat = 2;
		}
		else{
			if(isI){
				//state = 14;
				stat = 52;
			}
			else{
				//stat = 55;
				stat = 9;
			}
		}
		
		for(int i = 0; i < eps; i++){
			/*if(i == eps-1){
				average = true;
			}*/
			learn(timeSteps);
	
			if(!average){
				printPolicyToFile(stat);
			}
			else{
				//average the policy and qValues for each trial
				addArray(avgPol[i], sPolicy);
				addArray(avgQ[i], qValues);
			}
			//always change starting state after each trial
			if(!isGrid){
				startState = rand.nextInt(21);
			}
			env.gotoState(startState);
		}
		for(int i = 0; i < sPolicy.length; i++){
			for(int j = 0; j < actions.length; j++){
				if(sPolicy[i][j] < (Math.pow(10,-5))){
					sPolicy[i][j] = 0;
				}
				System.out.println("State: " + i + ", Action " + actions[j] + ": " + sPolicy[i][j]);
				polWriter.println("State: " + i + ", Action " + actions[j] + ": " + sPolicy[i][j]);
			}
		}

		//print out the action distribution for state 48
		System.out.println("");
		for(int a = 0; a < actions.length; a++){
			System.out.print(actionDist[a] + ", ");
		}
		
		//print out the Q-values for the 6 states we want to look at
		System.out.println("\nQ-values");
		int[] states = {8, 9, 18, 19, 28, 29};
		for(int i = 0; i < states.length; i++){
			for(int a = 0; a < actions.length; a++){
				System.out.println("State: " + states[i] + ", Action: " + actions[a] 
						+ ": " + qValues[states[i]][a]);
			}
		}
		
		//print ps for state to a file (timestep vs. probability)
		for(int i = 0; i < timeSteps; i++){
			psWriter.println("");
			psWriter.print(ps[i][stat]);
		}
		
		writer.close();
		qWriter.close();
		psWriter.close();
		paWriter.close();
		pjWriter.close();
		DWriter.close();
		polWriter.close();
	}
	
	public void printPolicyToFile(int s){
		writer.println("");
		qWriter.println("");
		for(int i = 0; i < actions.length; i++){
			writer.print(sPolicy[s][i] + ",");
			qWriter.print(qValues[s][i] + ",");
		}
	}
	
	public void printAverages(){
		//assuming number of eps == number of timeSteps
		
	}
	
	public void randomizePolicy(double[][] policy){
		
		Option o;
		for(int i = 0; i < policy.length; i++){
			double sum = 0;
			for(int j = 0; j < actions.length; j++){
				if(j > 4){ //this is a option
					o = env.getOption(actions[j]);
					if(o.isExecutable(i)){
						policy[i][j] = Math.random();
						sum += policy[i][j];
					}
					else{
						policy[i][j] = 0;
					}
				}
				else{
					policy[i][j] = Math.random();
					sum += policy[i][j];
				}
			}
			for(int j = 0; j < actions.length; j++){
				policy[i][j] = policy[i][j]/sum;
			}
		}
	}
	
	public double[][] copyArray(double[][] a){
		double[][] array = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			System.arraycopy(a[i], 0, array[i], 0, a[i].length);
		}
		return array;
	}
	
	public void addArray(double[][] a, double[][] toAdd){
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				a[i][j] += toAdd[i][j];
			}
		}
	}
}