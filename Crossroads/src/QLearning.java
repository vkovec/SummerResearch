import java.util.Random;

public class QLearning extends Agent{

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
	
	@Override
	public void learn(int steps) {
		for(int i = 0; i < steps; i++){
			//env.getBernouilli(0.7);
			//choose A from S using policy derived from Q
			//using e-greedy here
			//(with probability 0.7 we take best action)
			
		}
		
	}

}