package tools;

//maybe want to change this to also store each state and reward for multi step actions
//(i.e. an array of state and reward instead of just one value)
//also need the actions for Q-learning (not sure how to take care of this yet)

public class Info{
	private State[] s;
	private Double[] reward;
	private int timeSteps;
	
	public Info(State[] state, Double[] r, int t){
		s = state;
		reward = r;
		timeSteps = t;
	}
	
	public State getState(){
		return s[s.length-1];
	}
	
	public double getReward(){
		//System.out.println(reward.length);
		return reward[reward.length-1];
	}
	
	public int getTimeSteps(){
		return timeSteps;
	}
	
	public State[] getStates(){
		return s;
	}
	
	/*public String[] getActions(){
		return actions;
	}*/
	
	public Double[] getRewards(){
		return reward;
	}
}