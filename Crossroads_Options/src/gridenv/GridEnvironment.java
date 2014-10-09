package gridenv;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import tools.IEnvironment;
import tools.Info;
import tools.Option;
import tools.OptionReader;
import tools.State;

/**
 * @author vkovec
 *	A 10 x 10 grid world with one start, one goal, and several obstacles (which may be arbitrarily placed)
 */
public class GridEnvironment implements IEnvironment{
	
	//want to print the random options to a file to know what they are
	private PrintWriter oWriter;
	
	//prevents actions from failing while trying to learn options
	private boolean opLearn = false;
	
	private boolean isI;
	private boolean isEmpty = false;
	
	//the grid could be a 2D array of states (simpler to manage)
	private State[][] states;
	private int gridSize;
	
	private Random rand = new Random();

	private String[] actions = {"up", "down", "left", "right"};
	
	//list of options
	private Hashtable<String, Option> options;
	
	private State currentState = null;
	
	private double[][] qValues = null;
	
	//n is the grid size
	public GridEnvironment(int n, boolean isI){
		this.isI = isI;
		
		gridSize = n;
		states = new State[n][n];
		
		int x = 0;
		//initialize the states
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				states[i][j] = new State(x, 0);
				x++;
			}
		}

		setObstacles();
		
		if(isEmpty){
			try {
				oWriter = new PrintWriter("options.txt", "UTF-8");
			} 
			catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		initializeOptions();
		
		if(isI){
			currentState = states[0][4];
			states[9][4].setReward(1);
		}
		else{
			currentState = states[0][0];
			//set the reward at the goal
			states[gridSize-1][gridSize-1].setReward(1);
		}
	}
	
	public void initializeOptions(){
		options = new Hashtable<String, Option>();
		
		
		//find which state this is in the array
		
		/*		if(s > gridSize*gridSize){
					return;
				}
				
				//i is the first number
				int i = s/gridSize;

				//remainder when dividing by gridSize will give j
				int j = s%gridSize;

				currentState = states[i][j];
				
		how to get s if we know i and j?
		
		gridSize*i + j (?) <- looks correct
		
		*/
		
		int size = (gridSize*gridSize)/2 + 1;
		
		if(!isI){
			//two different options, one for getting to the entrance of each corridor
			/*int[] ini = new int[9];
			String[] pol = new String[9];
		
			ini[0] = 0;
			ini[1] = 1;
			ini[2] = 2;
			ini[3] = 10;
			ini[4] = 11;
			ini[5] = 12;
			ini[6] = 20;
			ini[7] = 21;
			ini[8] = 22;
		
			for(int i = 0; i < ini.length; i++){
				if(i%3 == 0){
					pol[i] = "down";
				}
				else{
					pol[i] = "left";
				}
			}
		
			options.put("odown", new Option("odown", size, ini, pol));
	
			pol = new String[9];
			
			for(int i = 0; i < ini.length; i++){
				if(i < 6){
					pol[i] = "right";
				}
				else{
					pol[i] = "up";
				}
			}
		
			options.put("oright", new Option("oright", size, ini, pol));*/
		/*
			//recreating experiment with 3 random options
			int[] ini = new int[8];
			String[] pol = new String[8];
			
			ini[0] = 28;
			ini[1] = 18;
			ini[2] = 19;
			ini[3] = 29;
			ini[4] = 39;
			ini[5] = 8;
			ini[6] = 7;
			ini[7] = 17;

			pol[0] = "up";
			pol[1] = "up";
			pol[2] = "left";
			pol[3] = "left";
			pol[4] = "up";
			pol[5] = "left";
			pol[6] = "left";
			pol[7] = "up";
			
			options.put("o1", new Option("o1", size, ini, pol));
			
			ini = new int[9];
			pol = new String[9];
			
			ini[0] = 65;
			ini[1] = 66;
			ini[2] = 56;
			ini[3] = 55;
			ini[4] = 54;
			ini[5] = 53;
			ini[6] = 52;
			ini[7] = 51;
			ini[8] = 50;

			pol[0] = "up";
			pol[1] = "up";
			pol[2] = "left";
			pol[3] = "left";
			pol[4] = "left";
			pol[5] = "left";
			pol[6] = "left";
			pol[7] = "left";
			pol[8] = "up";
			
			options.put("o2", new Option("o2", size, ini, pol));
			
			ini = new int[9];
			pol = new String[9];
			
			ini[0] = 58;
			ini[1] = 68;
			ini[2] = 69;
			ini[3] = 78;
			ini[4] = 48;
			ini[5] = 59;
			ini[6] = 79;
			ini[7] = 49;
			ini[8] = 39;

			pol[0] = "up";
			pol[1] = "up";
			pol[2] = "left";
			pol[3] = "up";
			pol[4] = "up";
			pol[5] = "up";
			pol[6] = "up";
			pol[7] = "left";
			pol[8] = "left";
			
			options.put("o3", new Option("o3", size, ini, pol));
			//
			 */
		}
		
		else{
			//for the I environment
			int[] ini = new int[6];
			String[] pol = new String[6];
		
			ini[0] = 3;
			ini[1] = 4;
			ini[2] = 5;
			ini[3] = 13;
			ini[4] = 14;
			ini[5] = 15;
		
			for(int i = 0; i < ini.length; i++){
				if(i < 3){
					pol[i] = "down";
				}
				else{
					pol[i] = "left";
				}
			}
			options.put("oleft", new Option("oleft", size, ini, pol));
		
			for(int i = 0; i < ini.length; i++){
				if(i < 3){
					pol[i] = "down";
				}
				else{
					pol[i] = "right";
				}
			}
			options.put("oright", new Option("oright", size, ini, pol));
			
			//one going down left column
			ini = new int[10];
			pol = new String[10];
			
			for(int i = 0; i < 6; i++){
				ini[i] = 10*(i+2) + 2;
				pol[i] = "down";
			}
			for(int i = 6; i < ini.length; i++){
				ini[i] = 10*(i - 3) + 3;
				pol[i] = "left";
			}
			options.put("old", new Option("old", size, ini, pol));
			
			//one going down right column
			ini = new int[10];
			pol = new String[10];
			
			for(int i = 0; i < 6; i++){
				ini[i] = 10*(i+2) + 6;
				pol[i] = "down";
			}
			for(int i = 6; i < ini.length; i++){
				ini[i] = 10*(i - 3) + 5;
				pol[i] = "right";
			}
			options.put("ord", new Option("ord", size, ini, pol));
		}
		
		if(!isEmpty){
			//for reading in options from a file
			OptionReader reader = new OptionReader();
			ArrayList<Option> ops = reader.getOptions();
			for(Option o : ops){
				options.put(o.getName(), o);
			}
		}
		
		/*Option o = createRandomOption(size);
		options.put(o.getName(), o);
		
		o = createRandomOption(size);
		options.put(o.getName(), o);
		
		o = createRandomOption(size);
		options.put(o.getName(), o);
		
		o = createRandomOption(size);
		options.put(o.getName(), o);*/
		
		else{
			Option o = createRandomOption(size);
			options.put(o.getName(), o);
		
			//done creating options
			oWriter.close();
		}
	}
	
	//helper for below
	private boolean contains(int[] a, int c){
			
		for(int i = 0; i < a.length; i++){
			if(a[i] == c){
				return true;
			}
		}
			
		return false;
	}
		
	public Option createRandomOption(int n){
		
		//assuming we won't be deleting any options after they have
		//been created
		String name = "o" + (options.size()+1);
		
		oWriter.println(name);
			
		//want at least 3 states in the option and at most 5
		//int[] iniSet = new int[rand.nextInt(3) + 3];
		
		//want at least 5 states in the option and at most 9
		//int[] iniSet = new int[rand.nextInt(5) + 5];
		
		int[] iniSet;
		int goal = 0;
		if(!isEmpty){
			iniSet = new int[rand.nextInt(16) + 15];
		}
		else{
		
			iniSet = new int[99];
		
			goal = rand.nextInt(100);
		
			int k = 0;
			for(int i = 0; i < iniSet.length; i++){
				iniSet[i] = k;
				if(k == goal){
					i--;
				}
				k++;
			}
		}
		
		Option opt;
		if(!isEmpty){
			//pick a random state to start the option
			State first = states[rand.nextInt(gridSize)][rand.nextInt(gridSize)];
			while(first.isObstacle()){
				first = states[rand.nextInt(gridSize)][rand.nextInt(gridSize)];
			}
			currentState = first;
			iniSet[0] = first.getName();
			System.out.println(iniSet[0]);
			oWriter.println(iniSet[0]);
			
			//pick actions uniformly at random to move around the state space
			//and retrieve new states for the option
			for(int i = 1; i < iniSet.length; i++){
				performOption(actions[rand.nextInt(4)]);
			
				//to avoid having the same state more than once in the initiation set
				while(contains(iniSet, currentState.getName())){
					performOption(actions[rand.nextInt(4)]);
				}
				
				iniSet[i] = currentState.getName();
				System.out.println(iniSet[i]);
				oWriter.println(iniSet[i]);
			}
			
			//one more time to get the goal state
			while(contains(iniSet, currentState.getName())){
				performOption(actions[rand.nextInt(4)]);
			}
			System.out.println("goal: " + currentState.getName());
			oWriter.println("goal: " + currentState.getName());
			
			opt = new Option(name, n, iniSet);
			
			learnOption(opt, currentState.getName());
		}
		else{
			opt = new Option(name, n, iniSet);
		
			learnOption(opt, goal);
		}
		return opt;
	}
	
	/**
	 * Learn the policy for option o
	 * @param o option
	 * @param n goal state
	 */
	public void learnOption(Option o, int n){
		opLearn = true;
		
		int[] ini = o.getIni();
		//gives indices
		Hashtable<Integer, Integer> stateToState = new Hashtable<Integer, Integer>();
		//gives actual state
		Hashtable<Integer, Integer> reverse = new Hashtable<Integer, Integer>();
		for(int i = 0; i < o.sizeI(); i++){
			stateToState.put(ini[i], i);
			reverse.put(i, ini[i]);
		}
		
		double[][] qVals = new double[o.sizeI()][actions.length];
		
		//figure out how to give subgoals
		//maybe increase the reward at the subgoal states (for now only the center state)
		//and remove the reward at the actual goal (or just don't set an actual goal)
		getState(n).setReward(1);
		
		//use the modified Q-learning algorithm and above method to
		//learn from the environment
		int s;
		String action;
		Info result;
		int index;
		int nextState;
		for(int i = 0; i < 1000; i++){ //1000 trials
			//set a start state
			s = o.getRandomState();
			currentState = getState(s);
			for(int j = 0; j < 100; j++){ //100 steps
				
				//if we are in a state where the option ends then do not continue
				if(o.terminate(currentState.getName())){
					if(currentState.getName() != n){
						s = o.getRandomState();
						currentState = getState(s);
					}
					else{
						break;
					}
				}
				//pick an action randomly
				action = actions[rand.nextInt(actions.length)];
				
				result = performOption(action);
				
				index = getActionIndex(action);
				nextState = result.getState().getName();
				
				int st = stateToState.get(s);
				if(o.isExecutable(nextState)){
					//keep it from leaving the initiation set unless we are at the goal
					if(nextState != n){
						currentState = getState(s);
					}
					
					qVals[st][index] = qVals[st][index] + 0.1*(result.getReward()
							+ 0.9*getMaxQ(stateToState.get(nextState), qVals)
							- qVals[st][index]); 
				}
				else{
					qVals[st][index] = qVals[st][index] + 0.1*(result.getReward()
							+ 0.9*result.getReward()
							- qVals[st][index]);
				}
			}
		}
		
		//find a policy using the highest Q-values
		Hashtable<Integer, String> policy = new Hashtable<Integer, String>();
		
		for(int i = 0; i < qVals.length; i++){
			int act = -1;
			double val = -1;
			for(int j = 0; j < actions.length; j++){
				if(qVals[i][j] > val){
					val = qVals[i][j];
					act = j;
				}
			}
			//System.out.println(reverse.get(i) + ": " + actions[act] + ", val: " + val);
			oWriter.println(reverse.get(i) + ": " + actions[act] + ", val: " + val);
			policy.put(reverse.get(i), actions[act]);
		}
		System.out.println("");
		oWriter.println("");
		
		//set the policy for the option
		o.setPolicy(policy);
		
		//set the reward for the subgoal states back to 0
		getState(n).setReward(0);
		
		opLearn = false;
	}
	
	//helpers for above method
	private int getActionIndex(String action){
		for(int i = 0; i < actions.length; i++){
			if(actions[i].equals(action)){
				return i;
			}
		}
		return -1;
	}
	
	private double getMaxQ(int state, double[][] qVals){
		double currBest = Integer.MIN_VALUE;
		
		double currVal;
		for(int i = 0; i < qVals[0].length; i++){
			currVal = qVals[state][i];
			
			if(currVal > currBest){
				currBest = currVal;
			}
		}
		return currBest;
	}
	//
	
	//initializes the obstacles in the environment
	//change this to change how the environment looks
	public void setObstacles(){

		if(!isI && !isEmpty){
			/*createObstacle(3,8,2,3);
		
			states[3][1].setAsObstacle(true);
			states[3][2].setAsObstacle(true);
			states[3][7].setAsObstacle(true);
		
			for(int j = 1; j < 5; j++){
				states[4][j].setAsObstacle(true);
			}
			states[4][7].setAsObstacle(true);
		
			states[5][7].setAsObstacle(true);
		
			for(int j = 0; j < 5; j++){
				states[6][j].setAsObstacle(true);
			}
			states[6][7].setAsObstacle(true);
		
			states[7][6].setAsObstacle(true);
			states[7][7].setAsObstacle(true);*/
			
			createObstacle(2, 8, 2, 8);
		}
		else if(isI){
			//I environment
		
			//left side
			createObstacle(0, 2, 2, 8);
			//center top
			createObstacle(3, 6, 2, 3);
			//| part
			createObstacle(4, 5, 3, 8);
			//center bottom
			createObstacle(3, 6, 7, 8);
			//right side
			createObstacle(7, 10, 2, 8);
		}
	}
	
	
	/**
	 * Creates rectangular obstacles.
	 * @param wS width start -> j
	 * @param wE width end
	 * @param hS height start -> i
	 * @param hE height end
	 */
	public void createObstacle(int wS, int wE, int hS, int hE){
		for(int i = hS; i < hE; i++){
			for(int j = wS; j < wE; j++){
				states[i][j].setAsObstacle(true);
			}
		}
	}
	
	public int getGridSize(){
		return gridSize;
	}

	@Override
	public int getCurrentState() {
		return currentState.getName();
	}

	@Override
	public int getBernouilli(double p) {
		int x = 0;
		if(Math.random() < p){
			x++;
		}
		return x;
	}

	@Override
	public Info performOption(String o) {
		//this time the actions are even more stochastic than before
		//still a 0.7 probability of succeeding, but now if it doesn't succeed,
		//it chooses among the remaining three actions with equal probability
		//and that action succeeds for sure

		//if this is an option
		if(o.charAt(0) == 'o'){
			//unsure whether this actually works (appears to work though)
			Option op = options.get(o);
			
			//discount factor
			double gamma = 0.9;
			int t = 0;
			
			double reward = 0;
			Info inf;
			
			ArrayList<State> states = new ArrayList<State>();
			ArrayList<Double> rewards = new ArrayList<Double>();
			
			String act;
			int actInd;
			int prevState;
			//this may change if the policy is no longer deterministic (but prob not)
			while(op.isExecutable(currentState.getName()) && !op.terminate(currentState.getName())){
				prevState = currentState.getName();
				
				act = op.getAction(currentState.getName());

				inf = performOption(act);
				
				//set the Q-value for the sub action
				actInd = getActionIndex(act);
				qValues[prevState][actInd] = qValues[prevState][actInd] + 0.1*(inf.getReward()
						+ Math.pow(0.9, inf.getTimeSteps())
						*getMaxQ(inf.getState().getName(), qValues)
						- qValues[prevState][actInd]);
				/*if(prevState >= 8 && prevState < 20){
					System.out.println("state: " + prevState + ", a: " + act + ", q: " + qValues[prevState][actInd]);
				}*/
				//
				
				//store reward from t+1 onwards without discounting
				rewards.add(inf.getReward());
				
				reward += Math.pow(gamma, t)*inf.getReward();
				//not including the first state
				states.add(inf.getState());
				t++;
			}
			
			//the option could not be executed at this state
			if(t == 0){
				//maybe not the best way to do this
				//we just stayed in the same state and didn't get any reward
				return new Info(new State[]{currentState}, new Double[]{0.0}, 0);
			}
			
			//the complete discounted rewards for each state except for the last one
			Double[] discR = new Double[states.size()];
			//index of the reward corresponds to the index of the state where the reward was obtained
			//except for the final one which corresponds to the reward for the first action
			for(int r = 0; r < discR.length-1; r++){
				discR[r] = 0.0;
				for(int i = r+1; i < rewards.size(); i++){
					discR[r] += Math.pow(gamma, i-(r+1))*rewards.get(i);
				}
			}
			discR[discR.length-1] = reward;
			
			State[] tempS = new State[states.size()];
			tempS = states.toArray(tempS);
			
			return new Info(tempS, discR, t);
		}
		
		if(opLearn){
			currentState = getState(o);
			return new Info(new State[]{currentState}, new Double[]{(double) currentState.getReward()}, 1);
		}
		
		//interesting area is now an area where the probability of action succeeding is different
		int s = currentState.getName();
		if((s == 40 || s == 41 || s == 50 || s == 51 || s == 60 || s == 61) && !isEmpty){
			if(getBernouilli(Math.random()) == 1){
				currentState = getState(o);
			}
			else{
				Random rand = new Random();
				//choose from amongst the other actions with equal probability
				if(o.equals("up")){
					String[] acts = {"down", "left", "right"};
					currentState = getState(acts[rand.nextInt(3)]);
				}
				else if(o.equals("down")){
					String[] acts = {"up", "left", "right"};
					currentState = getState(acts[rand.nextInt(3)]);
				}
				else if(o.equals("left")){
					String[] acts = {"down", "up", "right"};
					currentState = getState(acts[rand.nextInt(3)]);
				}
				else{
					String[] acts = {"down", "left", "up"};
					currentState = getState(acts[rand.nextInt(3)]);
				}
			}
		}
		//if the action succeeds
		else if(getBernouilli(0.7) == 1){
			currentState = getState(o);
		}
		else{
			Random rand = new Random();
			//choose from amongst the other actions with equal probability
			if(o.equals("up")){
				String[] acts = {"down", "left", "right"};
				currentState = getState(acts[rand.nextInt(3)]);
			}
			else if(o.equals("down")){
				String[] acts = {"up", "left", "right"};
				currentState = getState(acts[rand.nextInt(3)]);
			}
			else if(o.equals("left")){
				String[] acts = {"down", "up", "right"};
				currentState = getState(acts[rand.nextInt(3)]);
			}
			else{
				String[] acts = {"down", "left", "up"};
				currentState = getState(acts[rand.nextInt(3)]);
			}
		}
		
		//interesting area
		//int s = currentState.getName();
		if(!isI){
			/*if(s == 53 || s == 54 || s == 55 || s == 56 || s == 65 || s == 66){
				return new Info(new State[]{currentState}, new Double[]{rand.nextGaussian()}, 1);
			}*/
		}
		else{
			/*if(s == 42 || s == 43 || s == 52 || s == 53){
				return new Info(new State[]{currentState}, new Double[]{rand.nextGaussian()}, 1);
			}*/
		}
		return new Info(new State[]{currentState}, new Double[]{(double) currentState.getReward()}, 1);
	}
	
	/**
	 * Helper for above
	 * @param a Action that was performed
	 * @return the state we should end up at after performing action a
	 */
	private State getState(String a){
		//also have to avoid obstacles
		
		State s = null;
		
		int name = currentState.getName();
		//i is the first number
		int i = name/gridSize;
		//remainder when dividing by gridSize will give j
		int j = name%gridSize;
		
		if(a.equals("up")){
			if(i == 0 || states[i-1][j].isObstacle()){
				s = currentState;
			}
			else{
				s = states[i-1][j];
			}
		}
		else if(a.equals("down")){
			if(i == gridSize-1 || states[i+1][j].isObstacle()){
				s = currentState;
			}
			else{
				s = states[i+1][j];
			}
		}
		else if(a.equals("left")){
			if(j == 0 || states[i][j-1].isObstacle()){
				s = currentState;
			}
			else{
				s = states[i][j-1];
			}
		}
		else if(a.equals("right")){
			if(j == gridSize-1 || states[i][j+1].isObstacle()){
				s = currentState;
			}
			else{
				s = states[i][j+1];
			}
		}
		
		return s;
	}
	
	public State getState(int s){
		//find which state this is in the array
		
		if(s > gridSize*gridSize){
			return null;
		}
				
		//i is the first number
		int i = s/gridSize;

		//remainder when dividing by gridSize will give j
		int j = s%gridSize;

		return states[i][j];
	}
	
	public boolean isObstacle(int s){
		
		//find which state this is in the array
		
		if(s > gridSize*gridSize){
			return true;
		}
				
		//i is the first number
		int i = s/gridSize;

		//remainder when dividing by gridSize will give j
		int j = s%gridSize;

		return states[i][j].isObstacle();
	}
	
	@Override
	public void gotoState(int s) {
		//find which state this is in the array
		
		if(s > gridSize*gridSize){
			return;
		}
		
		//i is the first number
		int i = s/gridSize;

		//remainder when dividing by gridSize will give j
		int j = s%gridSize;

		currentState = states[i][j];
	}

	@Override
	public Option getOption(String o) {
		return options.get(o);
	}
	
	public State[][] getStates(){
		return states;
	}

	@Override
	public Enumeration<Option> getOptions() {
		return options.elements();
	}

	@Override
	public int howManyOptions() {
		return options.size();
	}

	@Override
	public void toggleActionFail() {
		if(opLearn){
			opLearn = false;
		}
		else{
			opLearn = true;
		}
	}
	
	@Override
	public void getQVals(double[][] qVals) {
		this.qValues = qVals;
	}
}