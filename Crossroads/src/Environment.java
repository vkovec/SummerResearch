import java.util.ArrayList;
import java.util.Random;

public class Environment{

	//list of states
	private ArrayList<State> states;
	
	//current state
	private State currState;
	
	//(may not need these)
	//start state
	//private State startState;
	//goal state
	private State goalState = null;
	
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
	
	//perform the action and return the new state and reward
	public Info performAction(String a){
		//actions succeed with probability 0.7
		if(getBernouilli(0.7) == 1){
			currState = currState.performAction(a);
			Info i = new Info(currState, currState.getReward());
			return i;
		}
		else{
			//assume we don't get any reward if we stay in the current state
			//(or should I treat it as having transitioned to the current state?)
			return new Info(currState, 0);
		}
	}
	
	public void gotoState(int s){
		currState = states.get(s);
	}
}