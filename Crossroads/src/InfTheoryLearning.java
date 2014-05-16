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
	
	//calculates the difference between two probability distributions
	private double getDistrDiff(double[][] d1, double[][] d2){
		double sum = 0;
		for(int x = 0; x < d1.length; x++){
			for(int a = 0; a < actions.length; a++){
				if(d1[x][a] != 0 && d2[x][a] != 0){
					sum += d1[x][a]*(Math.log(d1[x][a]/d2[x][a])/Math.log(2));
				}
				else if(d2[x][a] == 0){
					sum = Double.POSITIVE_INFINITY;
				}
			}
		}
		return sum;
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
		
		//just randomize to begin? or set to 0?
		//make p uniformly random
		for(int x = 0; x < p.length; x ++){
			for(int a = 0; a < actions.length; a++){
				for(int s = 0; s < p.length; s++){
					p[x][a][s] = 1.0/(p.length*actions.length*p.length);
				}
			}
		}
		
		int state;
		String action;
		Info result;
		float lambda = 10;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			
			//a)
				//how to do this calculation?
				//for now just use whatever was calculated in c)
				//update ps
				//this causes NaN (sPolicy is NaN for some reason)
				/*for(int j = 0; j < ps.length; j++){
					double sum = 0;
					for(int x = 0; x < ps.length; x++){
						for(int a = 0; a < 4; a++){
							sum += p[x][a][j]*sPolicy[x][a]*ps[x];
						}
					}
					ps[j] = sum;
				}*/
			
			
			//b)
			//randomizePolicy(qPolicy);
			q = sPolicy;
			
			//c)
				//what does it mean for the difference between q(j) and q(j+1) to be small?
					//maybe difference between the two probability distributions using D(qj, qj+1)?
			double[][] qPrev = new double[q.length][4];

			while(Math.abs(getDistrDiff(q, qPrev)) > 0.5){ //this may be completely wrong

				qPrev = q;

				//update pa
				for(int j = 0; j < 4; j++){
					double sum = 0;
					
					//for each state
					for(int k = 0; k < q.length; k++){
						sum += q[k][j]*ps[k];
					}
					pa[j] = sum;
					
					//because q is 0
					/*if(sum == 0){
						System.out.println("zero");
					}*/
				}
				
				//update ps
				for(int j = 0; j < ps.length; j++){
					double sum = 0;
					for(int x = 0; x < ps.length; x++){
						for(int a = 0; a < 4; a++){
							//infinity times 0 happens here
							//but how does ps become infinity??
							sum += p[x][a][j]*q[x][a]*ps[x];
							/*if(Double.isNaN(sum)){
								//System.out.println("q: " + q[x][a]);
								//System.out.println("ps: " + ps[x]);
								//System.out.println("p: " + p[x][a][j]);
								System.out.println("ps");
								return;
							}*/
							//if(Double.isInfinite(sum)){
								//q somehow ends up as infinity, which makes ps infinite
								//System.out.println("q: " + q[x][a]);
								//System.out.println("ps: " + ps[x]);
								//System.out.println("p: " + p[x][a][j]);
							//}
						}
					}
					ps[j] = sum;
 				}
				
				//update q
				//lambda = lambda/(i+1);
				double[][] D = new double[q.length][actions.length];
				double[] Z = new double[q.length];
				for(int x = 0; x < q.length; x++){
					for(int a = 0; a < actions.length; a++){
						
						//set D
						double sum = 0;
						for(int k = 0; k < D.length; k++){
							if(p[x][a][k] != 0 && ps[k] != 0){
								sum += p[x][a][k]*(Math.log(p[x][a][k]/ps[k])/Math.log(2));
								/*if(Double.isNaN(sum)){
									//System.out.println("p: " + p[x][a][k] + ", ps: " + ps[k]);
									System.out.println("D");
								}*/
							}
							else if(ps[k] == 0){
								sum = Double.POSITIVE_INFINITY;
								/*System.out.println("inf");
								return;*/
							}
						}
						D[x][a] = sum;
						
						//set Z
						sum = 0;
						for(int k = 0; k < actions.length; k++){
							sum += pa[k]*Math.exp((1/lambda)*(D[x][k] + 2*qValues[x][k]));
						}
						Z[x] = sum;
						
						//update q
						q[x][a] = (pa[a]*Math.exp((1/lambda)*(D[x][a] + 2*qValues[x][a])))/Z[x];
						//THE FIRST NaN (only when lambda gets smaller with time?)
						/*if(Double.isNaN(q[x][a])){
							System.out.println(pa[a]);
							//System.out.println("D: " + Math.exp(1/lambda*(D[x][a] + 3*qValues[x][a])));
							//System.out.println("q: " + (pa[a]*Math.exp(1/lambda*(D[x][a] + 3*qValues[x][a]))));
							return;
						}*/
						/*if(q[x][a] == 0){
							System.out.println("zer0: " + (D[x][a] + 3*qValues[x][a]));
							System.out.println(lambda);
							return; 
						}*/
					}
				}
				
				//p should get closer to 1 for going up from state 0 to state 0, because
				//this will always happen
				//only transition policy for the up action even changes
				//probability that we go up in general gets progressively worse, yet we still
				//have our policy be to go up...
				//pa just gets continuously worse for every action
				//System.out.println("q: " + q[0][0] + ", p: " + p[0][0][0] + ", pa: " + pa[0] + " ps: " + ps[0]);
				//printSum(pa); //should be 1
				//printSum(ps); //should be 1 as well
			}
			sPolicy = q;
			
			//d) choose action a (using pi) and obtain reward and next state
			action = selectAction(state);
			result = env.performAction(action); 
			
			//compute new estimate for probability distribution
			int s = result.getState().getName();
			for(int x = 0; x < p.length; x++){
				int a = getActionIndex(action);
				//the state the action actually led to
				if(x == s){
					p[state][a][s] += 0.1*(1 - p[state][a][s]);
				}
				//the states the action didn't lead to
				else{
					p[state][a][s] += 0.1*(0 - p[state][a][s]);
				}
			}
			
			//e)
			//take from Q-learning (for now, eventually want to do a full Q value evaluation)
			int index = getActionIndex(action);
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ 0.9*getMaxQ(result.getState().getName()) - qValues[state][index]);
			
			//dynamic programming attempt (doesn't appear to be working properly)
			/*qValues[state][index] = 0;
			for(int x = 0; x < qValues.length; x++){
				if(x == goalState){ //should be the states around the goal
					qValues[state][index] += p[state][index][x]*(1 + 0.9*getMaxQ(x));
				}
				else{
					qValues[state][index] += p[state][index][x]*(0 + 0.9*getMaxQ(x));
				}
			}*/
		}
	}
	
	
	private void printSum(double[] a){
		double sum = 0;
		for(int i = 0; i < a.length; i++){
			sum += a[i];
		}
		System.out.println(sum);
	}

}