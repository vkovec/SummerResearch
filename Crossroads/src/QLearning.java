import java.util.Random;

public class QLearning extends Agent{

	public QLearning(int n) {
		super(n);
	}

	public String findBestAction(int s){
		//action that has given us the best Q values so far for this state
		String best = null;
		
		//check if all actions are the same
		int same = 0;
		double currBest = Integer.MIN_VALUE;
		double currVal;
		for(int i = 0; i < 4; i++){
			currVal = qValues[s][i];
			
			if(currVal > currBest){
				currBest = currVal;
				best = actions[i];
			}
			
			if(currVal == currBest){
				same++;
			}
		}
		if(same >= 3){
			Random rand = new Random();
			best = actions[rand.nextInt(4)];
		}
		return best;
	}
	
	private int getActionIndex(String action){
		for(int i = 0; i < 4; i++){
			if(actions[i].equals(action)){
				return i;
			}
		}
		return -1;
	}
	
	private double getMaxQ(int state){
		double currBest = Integer.MIN_VALUE;
		
		double currVal;
		for(int i = 0; i < 4; i++){
			currVal = qValues[state][i];
			
			if(currVal > currBest){
				currBest = currVal;
			}
		}
		return currBest;
	}
	
	@Override
	public void learn(int steps) {
		int state;
		Info result;
		String action;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			if(state == goalState){
				return;
			}
			//choose A from S using policy derived from Q
			//using e-greedy here
			//(with probability 0.7 we take best action)
			action = findBestAction(state);
			//have to change this slightly so we don't randomly select 
			//the best action
			if(env.getBernouilli(0.7) != 1){
				Random rand = new Random();
				action = actions[rand.nextInt(4)];
			}
			
			result = env.performAction(action);
			
			int index = getActionIndex(action);
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ 0.9*getMaxQ(result.getState().getName()) - qValues[state][index]); 
		}
	}

}