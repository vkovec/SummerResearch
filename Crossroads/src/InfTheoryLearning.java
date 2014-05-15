public class InfTheoryLearning extends Agent{

	private void randomizePolicy(double[][] policy){
		for(int i = 0; i < policy.length; i++){
			for(int j = 0; j < 4; j++){
				policy[i][j] = 0.25;
			}
		}
	}
	
	//select an action using the probabilities specified in pi
	private String selectAction(int s){
		
		double[] helper = {sPolicy[s][0], sPolicy[s][0] + sPolicy[s][1],
				sPolicy[s][0] + sPolicy[s][1] + sPolicy[s][2]};
		
		double x = Math.random();
		
		if(x > helper[2]){
			return actions[3];
		}
		else if(x > helper[1]){
			return actions[2];
		}
		else if(x > helper[0]){
			return actions[1];
		}
		else{
			return actions[0];
		}
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
		//initialize the policy to a uniformly random policy
		randomizePolicy(sPolicy);
		
		//the q(a|x) policy
		double[][] qPolicy = new double[sPolicy.length][4];
		
		int state;
		String action;
		Info result;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			
			//a)
			
			//b)
			randomizePolicy(qPolicy);
			
			//c)
			
			//d) choose action a (using pi) and obtain reward and next state
			action = selectAction(state);
			result = env.performAction(action); 
			
			//e)
			//take from Q-learning
			int index = getActionIndex(action);
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ 0.9*getMaxQ(result.getState().getName()) - qValues[state][index]);
		}
		
	}

}