import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Environment{
	
	//list of actions
	private String[] actions = {"up", "down", "left", "right"};

	//list of options
	private Hashtable<String, Option> options;
	
	//list of states
	private ArrayList<State> states;
	
	//current state
	private State currState;
	
	//(may not need these)
	//start state
	//private State startState;
	//goal state
	private State goalState = null;
	
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
	}
	
	//initialize the state space
	//n represents the length of the crossroads, and must be odd
	public void initializeStates(int n){
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
		
		Random rand = new Random();
		int n = (states.size()+1)/2;
		ArrayList<State> corners = new ArrayList<State>();
		corners.add(states.get(0));
		corners.add(states.get(n-1));
		corners.add(states.get(n));
		corners.add(states.get(states.size()-1));
		
		State s = corners.get(rand.nextInt(4));
		s.setReward(1);
		
		goalState = s;
		return s;
	}
	
	//chooses a random starting location
	public State chooseStart(){
		Random rand = new Random();
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
			//unsure whether this actually works
			Option op = options.get(o);
			
			//discount factor
			double gamma = 0.9;
			int i = 0;
			
			double reward = 0;
			Info inf;
			
			//this may change if the policy is no longer deterministic (but prob not)
			while(op.isExecutable(currState.getName()) && !op.terminate(currState.getName())){
				inf = performOption(op.getAction(currState.getName()));
				reward += Math.pow(gamma, i)*inf.getReward();
				i++;
			}
			
			return new Info(currState, reward, i);
		}
		
		//if the action succeeds
		if(getBernouilli(0.7) == 1){
			currState = s;
		}
		
		return new Info(currState, currState.getReward(), 1);
	}
	
	//need to learn the policies for the options
	//have to give manual subgoals to learn from and then set the policy
	//based on what was learned (only deterministic policies for now)
	public void learnOption(Option o, int n){

		Random rand = new Random();

		//this becomes very annoying and inefficient for finding the max Q-value
		ArrayList<Hashtable<Integer, Double>> q = new ArrayList<Hashtable<Integer, Double>>();
		for(int i = 0; i < actions.length; i++){
			q.add(new Hashtable<Integer, Double>());
		}
		
		double[][] qVals = new double[o.sizeI()][actions.length];
		
		//figure out how to give subgoals
		//maybe increase the reward at the subgoal states (for now only the center state)
		//and remove the reward at the actual goal (or just don't set an actual goal)
		states.get(n/2).setReward(1);
		
		//use the modified Q-learning algorithm and above method to
		//learn from the environment
		Hashtable<Integer, Double> temp;
		int s;
		String action;
		Info result;
		int index;
		int nextState;
		for(int i = 0; i < 100; i++){ //100 trials
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
				
				if(o.isExecutable(nextState)){
					qVals[s][index] = qVals[s][index] + 0.1*(result.getReward()
							+ 0.9*getMaxQ(nextState, qVals)
							- qVals[s][index]); 
				}
				else{
					//wait can't index s like this
					qVals[s][index] = qVals[s][index] + 0.1*(result.getReward()
							+ 0.9*result.getReward()
							- qVals[s][index]); 
				}
			}
		}
		
		//find a policy using the highest Q-values
		Hashtable<Integer, String> policy = new Hashtable<Integer, String>();
		
		for(int i = 0; i < qVals.length; i++){
			int act = -1;
			double val = 0;
			for(int j = 0; j < actions.length; j++){
				if(qVals[i][j] > val){
					val = qVals[i][j];
					act = j;
				}
			}
			policy.put(i, actions[act]);
		}
		//set the policy for the option
		o.setPolicy(policy);
		
		//set the reward for the subgoal states back to 0
		states.get(n/2).setReward(0);
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
	
	public void gotoState(int s){
		currState = states.get(s);
	}
}