package crossenv;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import tools.IEnvironment;
import tools.Info;
import tools.Option;
import tools.State;

public class Environment implements IEnvironment{
	
	//list of actions
	private String[] actions = {"up", "down", "left", "right"};

	//list of options
	private Hashtable<String, Option> options;
	
	//list of states
	private ArrayList<State> states;
	
	//current state
	private State currState;
	
	//goal state
	private State goalState = null;
	
	private Random rand;
	
	public void initializeOptions(int n){
		options = new Hashtable<String, Option>();
		
		int[] ini = new int[n/2];
		String[] pol = new String[n/2];
		
		for(int i = 0; i < ini.length; i++){
			ini[i] = i;
			pol[i] = "down";
		}
		
		options.put("oup", new Option("oup", n, ini, pol));
		
		ini = new int[n/2];
		pol = new String[n/2];
		
		for(int i = n/2+1; i < n; i++){
			ini[i-(n/2+1)] = i;
			pol[i-(n/2+1)] = "up";
		}
		
		options.put("odown", new Option("odown", n, ini, pol));
		
		Option o = createRandomOption(n);

		//options.put(o.getName(), o);
		
	//	o = createRandomOption(n);
	//	options.put(o.getName(), o);
		
	//	o = createRandomOption(n);
	//	options.put(o.getName(), o);
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
		
	//	Random rand = new Random();
		
		//assuming we won't be deleting any options after they have
		//been created
		String name = "o" + (options.size()+1);
		
		//want at least 3 states in the option and at most 5
		int[] iniSet = new int[rand.nextInt(3) + 3];
		
		//pick a random state to start the option
		State first = chooseStart();
		iniSet[0] = first.getName();
		System.out.println(iniSet[0]);
		
		//pick actions uniformly at random to move around the state space
		//and retrieve new states for the option
		//int prev = currState.getName();
		for(int i = 1; i < iniSet.length; i++){
			performOption(actions[rand.nextInt(4)]);
			
			/*while(prev == currState.getName()){
				performOption(actions[rand.nextInt(4)]);
			}
			prev = currState.getName();*/
			
			//to avoid having the same state more than once in the initiation set
			while(contains(iniSet, currState.getName())){
				performOption(actions[rand.nextInt(4)]);
			}
			
			iniSet[i] = currState.getName();
			System.out.println(iniSet[i]);
		}
		
		//one more time to get the goal state
		while(contains(iniSet, currState.getName())){
			performOption(actions[rand.nextInt(4)]);
		}
		System.out.println("goal: " + currState.getName());
		
		//String[] pol = new String[iniSet.length];
		
		Option opt = new Option(name, n, iniSet);
		
		learnOption(opt, currState.getName());
		
		return opt;
	}
	
	//initialize the state space
	//n represents the length of the crossroads, and must be odd
	//previously initializeStates()
	public Environment(int n){
		
		rand = new Random();
		
		states = new ArrayList<State>();
		
		//generate the states:
		for(int i = 0; i < 2*n -1; i++){
			states.add(new State(i, 0));
		}
		
		//vertical strip starting from north		
		State temp;
		State center = null;
		for(int i = 0; i < n; i++){
			temp = states.get(i);
			if(i == 0){
				temp.setNeighbors(temp, states.get(i+1), temp, temp);
			}
			else if(i == n-1){
				temp.setNeighbors(states.get(i-1), temp, temp, temp);
			}
			//center
			else if(i == n/2){
				temp.setNeighbors(states.get(i-1), states.get(i+1),
						states.get(n + n/2 -1), states.get(n + n/2));
				center = temp;
			}
			else{
				temp.setNeighbors(states.get(i-1), states.get(i+1), temp, temp);
			}
		}
		
		//horizontal strip starting from left
		for(int i = n; i < 2*n-1; i ++){
			temp = states.get(i);
			if(i == n){
				temp.setNeighbors(temp, temp, temp, states.get(i+1));
			}
			else if(i == 2*n-2){
				temp.setNeighbors(temp, temp, states.get(i -1), temp);
			}
			//one before the center
			else if(i == n + n/2 - 1){
				temp.setNeighbors(temp, temp, states.get(i -1), center);
			}
			//one after the center
			else if(i == n + n/2){
				temp.setNeighbors(temp, temp, center, states.get(i+1));
			}
			else{
				temp.setNeighbors(temp, temp, states.get(i -1), states.get(i+1));
			}
		}
		
		initializeOptions(n);
		//learn the options
	}
	
	//maybe return a copy instead
	public ArrayList<State> getStates(){
		return states;
	}
	
	public int getCurrentState(){
		return currState.getName();
	}
	
	//sets the reward at the goal state to 1,
	//while all other states have a reward of 0
	public State chooseGoal(){
		if(goalState != null){
			goalState.setReward(0);
		}
		
		//Random rand = new Random();
		int n = (states.size()+1)/2;
		/*ArrayList<State> corners = new ArrayList<State>();
		corners.add(states.get(0));
		corners.add(states.get(n-1));
		corners.add(states.get(n));
		corners.add(states.get(states.size()-1));
		
		State s = corners.get(rand.nextInt(4));*/
		State s = states.get(states.size()-1);
		s.setReward(1);
		
		goalState = s;
		return s;
	}
	
	//chooses a random starting location
	public State chooseStart(){
		//Random rand = new Random();
		State s = states.get(rand.nextInt(states.size()));
		currState = s;
		return s;
	}
	
	public int getBernouilli(double p){
		int x = 0;
		if(Math.random() < p){
			x++;
		}
		return x;
	}
	
	public Info performOption(String o){
		ArrayList<State> n = currState.getNeighbors();
		
		State s = currState;
		
		if(o.equals("up")){
			s = n.get(0);
		}
		else if(o.equals("down")){
			s =  n.get(1);
		}
		else if(o.equals("left")){
			s = n.get(2);
		}
		else if(o.equals("right")){
			s = n.get(3);
		}
		else{
			Option op = options.get(o);
			
			//discount factor
			double gamma = 0.9;
			int t = 0;
			
			double reward = 0;
			Info inf;

			ArrayList<State> states = new ArrayList<State>();
			ArrayList<Double> rewards = new ArrayList<Double>();
			
			while(op.isExecutable(currState.getName()) && !op.terminate(currState.getName())){
				inf = performOption(op.getAction(currState.getName()));
				
				//store reward from t+1 onwards without discounting
				rewards.add(inf.getReward());
				
				reward += Math.pow(gamma, t)*inf.getReward();

				//not including the first state since we already know what it was
				states.add(inf.getState());
				t++;
			}
			
			//the option could not be executed at this state
			if(t == 0){
				return new Info(new State[]{currState}, new Double[]{0.0}, t);
			}
			
			//System.out.println("state size: " + states.size() + ", reward size: " + rewards.size());
			
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
			
			//return new Info(currState, reward, t);
		}
		
		//if the action succeeds
		if(getBernouilli(0.7) == 1){
			currState = s;
		}
		
		return new Info(new State[]{currState}, new Double[]{(double) currState.getReward()}, 1);
	}
	
	//NOT IMPORTANT FOR NOW
	//need to learn the policies for the options
	//have to give manual subgoals to learn from and then set the policy
	//based on what was learned 
	//n is the goal state
	public void learnOption(Option o, int n){

		//Random rand = new Random();

		int[] ini = o.getIni();
		Hashtable<Integer, Integer> stateToState = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> reverse = new Hashtable<Integer, Integer>();
		for(int i = 0; i < o.sizeI(); i++){
			stateToState.put(ini[i], i);
			reverse.put(i, ini[i]);
		}
		
		double[][] qVals = new double[o.sizeI()][actions.length];
		
		//figure out how to give subgoals
		//maybe increase the reward at the subgoal states (for now only the center state)
		//and remove the reward at the actual goal (or just don't set an actual goal)
		states.get(n).setReward(1);
		
		//use the modified Q-learning algorithm and above method to
		//learn from the environment
		int s;
		String action;
		Info result;
		int index;
		int nextState;
		for(int i = 0; i < 1000; i++){ //100 trials
			//set a start state
			s = o.getRandomState();
			currState = states.get(s);
			for(int j = 0; j < 100; j++){ //100 steps
				
				//if we are in a state where the option ends then do not continue
				if(o.terminate(currState.getName())){
					break;
				}
				//pick an action randomly (for now)
				action = actions[rand.nextInt(actions.length)];
				result = performOption(action);
				
				index = getActionIndex(action);
				nextState = result.getState().getName();
				
				int st = stateToState.get(s);
				if(o.isExecutable(nextState)){
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
				if(qVals[i][j] >= val){
					val = qVals[i][j];
					act = j;
				}
			}
			System.out.println(reverse.get(i) + ": " + actions[act] + ", val: " + val);
			policy.put(reverse.get(i), actions[act]);
		}
		//set the policy for the option
		o.setPolicy(policy);
		
		//set the reward for the subgoal states back to 0
		states.get(n).setReward(0);
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
		for(int i = 0; i < actions.length; i++){
			currVal = qVals[state][i];
			
			if(currVal > currBest){
				currBest = currVal;
			}
		}
		return currBest;
	}
	//
	
	public Option getOption(String o){
		return options.get(o);
	}
	
	public Enumeration<Option> getOptions(){
		return options.elements();
	}
	
	public int howManyOptions(){
		return options.size();
	}
	
	public void gotoState(int s){
		currState = states.get(s);
	}
}