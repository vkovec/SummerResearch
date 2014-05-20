public class InfTheoryLearning extends Agent{

	//the probability distribution for states and actions
	private double[][][] p = null;
	private double[][] q;
	private double[][] ps; //this has to be based on time

	private double lambda = 100;
	private double alpha = 5;
	
	public InfTheoryLearning(int n){
		super(n);
		if(p == null){
			p = new double[sPolicy.length][4][sPolicy.length];
			for(int x = 0; x < p.length; x ++){
				for(int a = 0; a < actions.length; a++){
					for(int s = 0; s < p.length; s++){
						p[x][a][s] = 1.0/p.length;
					}
				}
			}
			//initializeP();
			
			//initialize the policy to a uniformly random policy
			randomizePolicy(sPolicy);
			
			//the q(a|x) policy
			q = new double[sPolicy.length][4];
			
			//assuming 100 time steps
			ps = new double[100][sPolicy.length];
			
			for(int i = 0; i < 100; i++){
				for(int j = 0; j < sPolicy.length; j++){
					ps[i][j] = 1.0/sPolicy.length;
				}
			}
		}
	}
	
	//initialize the probability distribution over
	//state and action combinations
	private void initializeP(){
		int n = (sPolicy.length+1)/2;
		
		//vertical strip starting from north		
		//State center = null;
		int center = n/2;
		for(int i = 0; i < n; i++){
			//temp = states.get(i);
			if(i == 0){
				//temp.setNeighbors(temp, states.get(i+1), temp, temp);
				p[i][0][i] = 1;
				p[i][1][i] = 0.3;
				p[i][1][i+1] = 0.7;
				p[i][2][i] = 1;
				p[i][3][i] = 1;
			}
			else if(i == n-1){
				//temp.setNeighbors(states.get(i-1), temp, temp, temp);
				p[i][0][i-1] = 0.7;
				p[i][0][i] = 0.3;
				p[i][1][i] = 1;
				p[i][2][i] = 1;
				p[i][3][i] = 1;
			}
			//center
			else if(i == n/2){
				//temp.setNeighbors(states.get(i-1), states.get(i+1),
				//		states.get(n + n/2 -1), states.get(n + n/2));
				//center = temp;
				p[i][0][i-1] = 0.7;
				p[i][0][i] = 0.3;
				p[i][1][i+1] = 0.7;
				p[i][1][i] = 0.3;
				p[i][2][n+(n/2)-1] = 0.7;
				p[i][2][i] = 0.3;
				p[i][3][n+n/2] = 0.7;
				p[i][3][i] = 0.3;
			}
			else{
				//temp.setNeighbors(states.get(i-1), states.get(i+1), temp, temp);
				p[i][0][i-1] = 0.7;
				p[i][0][i] = 0.3;
				p[i][1][i+1] = 0.7;
				p[i][1][i] = 0.3;
				p[i][2][i] = 1;
				p[i][3][i] = 1;
			}
		}
				
		//horizontal strip starting from left
		for(int i = n; i < 2*n-1; i ++){
			//temp = states.get(i);
			if(i == n){
			//	temp.setNeighbors(temp, temp, temp, states.get(i+1));
				p[i][0][i] = 1;
				p[i][1][i] = 1;
				p[i][2][i] = 1;
				p[i][3][i] = 0.3;
				p[i][3][i+1] = 0.7;
				
			}
			else if(i == 2*n-2){
			//	temp.setNeighbors(temp, temp, states.get(i -1), temp);
				p[i][0][i] = 1;
				p[i][1][i] = 1;
				p[i][2][i-1] = 0.7;
				p[i][2][i] = 0.3;
				p[i][3][i] = 1;
			}
			//one before the center
			else if(i == n + n/2 - 1){
				//temp.setNeighbors(temp, temp, states.get(i -1), center);
				p[i][0][i] = 1;
				p[i][1][i] = 1;
				p[i][2][i-1] = 0.7;
				p[i][2][i] = 0.3;
				p[i][3][center] = 0.7;
				p[i][3][i] = 0.3;
			}
			//one after the center
			else if(i == n + n/2){
			//	temp.setNeighbors(temp, temp, center, states.get(i+1));
				p[i][0][i] = 1;
				p[i][1][i] = 1;
				
				p[i][2][center] = 0.7;
				p[i][2][i] = 0.3;
				p[i][3][i+1] = 0.7;
				p[i][3][i] = 0.3;
			}
			else{
			//	temp.setNeighbors(temp, temp, states.get(i -1), states.get(i+1));
				p[i][0][i] = 1;
				p[i][1][i] = 1;
				
				p[i][2][i-1] = 0.7;
				p[i][2][i] = 0.3;
				p[i][3][i+1] = 0.7;
				p[i][3][i] = 0.3;
			}
		}
	}
	
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
	
	//calculate distribution difference by summing the absolute values of the differences
	//diff should be < 0.1
	private double getDistrDiff(double[][] d1, double[][] d2){
		double sum = 0;
		for(int x = 0; x < d1.length; x++){
			for(int a = 0; a < actions.length; a++){
				sum += Math.abs(d1[x][a] - d2[x][a]);
			}
		}
		return sum;
	}
	
	//calculates the difference between two probability distributions
	/*private double getDistrDiff(double[][] d1, double[][] d2){
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
	}*/
	
	@Override
	public void learn(int steps) {
		
		double[] pj = new double[sPolicy.length];
		
		double[] pa = new double[4];
		
		int state;
		String action;
		Info result;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			if(state == goalState){
				return;
			}
			
			//a)
				//how to do this calculation?
				//calculate the new probability of visiting each state
				//using the current state we are at
				//(i.e. increase the probability that we end up at the state we are at right now
				//at time step t, and decrease the others)
			
			//add 1 to the current state estimate
			ps[i][state] += 1;
			
			for(int x = 0; x < q.length; x++){
				ps[i][x] = ps[i][x]/2;
			}
			
			//b)
			
			//just set q to some random policy
			randomizePolicy(q);
			
			//c)
				//what does it mean for the difference between q(j) and q(j+1) to be small? 0.1
					//maybe difference between the two probability distributions using D(qj, qj+1)?
			double[][] qPrev = new double[q.length][4];

			//sometimes gets stuck in this loop
			while(Math.abs(getDistrDiff(q, qPrev)) > 0.1){ 
				qPrev = q;

				//update pa (this is ok)
				for(int j = 0; j < 4; j++){
					double sum = 0;
					
					//for each state
					for(int k = 0; k < q.length; k++){
						sum += q[k][j]*ps[i][k];
					}
					pa[j] = sum;
				}
				
				//update pj
				for(int j = 0; j < pj.length; j++){
					double sum = 0;
					for(int x = 0; x < pj.length; x++){
						for(int a = 0; a < 4; a++){
							sum += p[x][a][j]*q[x][a]*ps[i][x];
						}
					}
					pj[j] = sum;
 				}
				
				//update q
				if(lambda > 0.5){
					lambda = 10.0/(i+1);
				}

				//this is okay
				double[][] D = new double[q.length][actions.length];
				double[] Z = new double[q.length];
				
				for(int x = 0; x < q.length; x++){
					for(int a = 0; a < actions.length; a++){
						
						//set D
						//use pj or ps? pj doesn't seem to get used if we don't use it here
						double sum = 0;
						for(int k = 0; k < D.length; k++){
							if(p[x][a][k] != 0 && pj[k] != 0){
								sum += p[x][a][k]*(Math.log(p[x][a][k]/pj[k])/Math.log(2));
							}
							else if(pj[k] == 0){
								sum = Double.POSITIVE_INFINITY;
							}
						}
						D[x][a] = sum;
					}
				}
				
				for(int x = 0; x < q.length; x++){
					for(int a = 0; a < actions.length; a++){
						//set Z
						double sum = 0;
						for(int k = 0; k < actions.length; k++){
							sum += pa[k]*Math.exp((1/lambda)*(D[x][k] + alpha*qValues[x][k]));
						}
						Z[x] = sum;
						
						double exp = Math.exp((1/lambda)*(D[x][a] + alpha*qValues[x][a]));
						
						if(Double.isInfinite(Z[x]) && Double.isInfinite(exp)){
							q[x][a] = 1;
						}
						else{
							q[x][a] = (pa[a]*exp)/Z[x];
						}
						//System.out.println("q: " + q[x][a] + ", Z: " + Z[x] + ", other: " +
						//		Math.exp((1/lambda)*(D[x][a] + alpha*qValues[x][a])) 
						//		+ ", lambda: " + lambda);
					}
				}
				
				/*for(int x = 0; x < q.length; x++){
					for(int a = 0; a < actions.length; a++){
						
						//set D
						double sum = 0;
						for(int k = 0; k < D.length; k++){
							if(p[x][a][k] != 0 && ps[i][k] != 0){
								sum += p[x][a][k]*(Math.log(p[x][a][k]/ps[i][k])/Math.log(2));
							}
							else if(ps[i][k] == 0){
								sum = Double.POSITIVE_INFINITY;
							}
						}
						D[x][a] = sum;
						
						//set Z
						sum = 0;
						for(int k = 0; k < actions.length; k++){
							sum += pa[k]*Math.exp((1/lambda)*(D[x][k] + alpha*qValues[x][k]));
						}
						Z[x] = sum;
						
						q[x][a] = (pa[a]*Math.exp((1/lambda)*(D[x][a] + alpha*qValues[x][a])))/Z[x];
					}
				}*/
				
				//p should get closer to 1 for going up from state 0 to state 0, because
				//this will always happen
				//System.out.println("q: " + q[0][0] + ", p: " + p[0][0][0] + ", pa: " + pa[0] + " ps: " + ps[i][0]);
			}
			sPolicy = q;
			
			//d) choose action a (using pi) and obtain reward and next state
			action = selectAction(state);
			result = env.performAction(action); 
			
			//compute new estimate for probability distribution
			//this is probably wrong
			int s = result.getState().getName();
			for(int x = 0; x < p.length; x++){
				int a = getActionIndex(action);
				//the state the action actually led to
				if(x == s){
					p[state][a][x] += 0.3*(1 - p[state][a][x]);
					if(p[state][a][x] > 1){
						p[state][a][x] = 1;
					}
				}
				//the states the action didn't lead to
				else{
					p[state][a][x] += 0.3*(0 - p[state][a][x]);
					
					if(p[state][a][x] < 0){
						p[state][a][x] = 0;
					}
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
		
		//printP();
	}
	
	
	private void printSum(double[] a){
		double sum = 0;
		for(int i = 0; i < a.length; i++){
			sum += a[i];
		}
		System.out.println(sum);
	}
	
	public void printP(){
		for(int x = 0; x < p.length; x ++){
			for(int a = 0; a < actions.length; a++){
				for(int s = 0; s < p.length; s++){
					System.out.println("State " + x + " to " + s + " with action " + actions[a]+" : " + p[x][a][s]);
				}
			}
		}
	}

}