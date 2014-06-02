package gridenv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import tools.IEnvironment;
import tools.Info;
import tools.Option;
import tools.State;

/**
 * @author vkovec
 *	A 10 x 10 grid world with one start, one goal, and several obstacles (which may be arbitrarily placed)
 */
public class GridEnvironment implements IEnvironment{
	//the grid could be a 2D array of states (simpler to manage)
	private State[][] states;
	private int gridSize;
	
	//list of options
	private Hashtable<String, Option> options;
	
	private State currentState = null;
	
	//n is the grid size
	public GridEnvironment(int n){
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
		
		currentState = states[0][0];
		
		//set the reward at the goal
		states[gridSize-1][gridSize-1].setReward(1);
		
		setObstacles();
		initializeOptions();
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
		
		//two different options, one for getting to the entrance of each corridor
		int[] ini = new int[9];
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
		
		int size = (gridSize*gridSize - 1)/2;
		
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
		
		options.put("oright", new Option("oright", size, ini, pol));
	}
	
	//initializes the obstacles in the environment
	//change this to change how the environment looks
	public void setObstacles(){
		
		//going row by row
		for(int j = 3; j < 8; j++){
			states[2][j].setAsObstacle(true);
		}
		
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
		states[7][7].setAsObstacle(true);
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
			//this may change if the policy is no longer deterministic (but prob not)
			while(op.isExecutable(currentState.getName()) && !op.terminate(currentState.getName())){
				act = op.getAction(currentState.getName());

				inf = performOption(act);
				
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
				return new Info(new State[]{currentState}, new Double[]{0.0}, 1);
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
		
		
		//if the action succeeds
		if(getBernouilli(0.7) == 1){
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
}