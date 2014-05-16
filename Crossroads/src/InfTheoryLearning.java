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
		double[][] q = new double[sPolicy.length][4];
		
		double[] ps = new double[sPolicy.length];
		
		//initially just give all states equal probability
		for(int i = 0; i < ps.length; i++){
			ps[i] = 1.0/ps.length;
		}
		
		double[] pa = new double[4];
		
		//initially just give all actions equal probability
		for(int i = 0; i < pa.length; i++){
			pa[i] = 1.0/pa.length;
		}
		
		//probability matrix estimate
		//given the current state and action, what is the probability of the next state being s'
		double[][][] p = new double[sPolicy.length][4][sPolicy.length];
		
		int state;
		String action;
		Info result;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			
			//a)
				//how to do this calculation?
				//for now just use whatever was calculated in c)
			
			//b)
			//randomizePolicy(qPolicy);
			q = sPolicy;
			
			//compute new estimate for probability distribution
			
			//c)
				//what does it mean for the difference between q(j) and q(j+1) to be small?
					//maybe difference between the two probability distributions using D(qj, qj+1)?
			double[][] qNext = new double[q.length][4];
			while(q != qNext){ //this is completely wrong
				
				//update pa
				for(int j = 0; j < 4; j++){
					double sum = 0;
					
					//for each state
					for(int k = 0; k < q.length; k++){
						sum += q[k][j]*ps[k];
					}
					pa[j] = sum;
				}
				
				//update ps
				for(int j = 0; j < ps.length; j++){
					double sum = 0;
					for(int x = 0; x < ps.length; x++){
						for(int a = 0; a < 4; a++){
							sum += p[x][a][j]*q[x][a]*ps[x];
						}
					}
					ps[j] = sum;
 				}
				
				//update qNext
			}
			
			//d) choose action a (using pi) and obtain reward and next state
			action = selectAction(state);
			result = env.performAction(action); 
			
			//e)
			//take from Q-learning (for now, eventually want to do a full Q value evaluation)
			int index = getActionIndex(action);
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ 0.9*getMaxQ(result.getState().getName()) - qValues[state][index]);
		}
		
	}

}