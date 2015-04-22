package agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import tools.Info;
import tools.Option;
import tools.State;

public class InfTheoryLearning extends Agent{

	//should calculations be made at squares which are obstacles
	private boolean withObs = false;
	
	//the probability distribution for states and actions
	private double[][][] p = null;
	private double[][] q;
	//private double[][] ps;
	private int counter = 0;

	private double lambda = 3000.0;
	//private double alpha = 0.5;
	
	public InfTheoryLearning(int n){
		super(n);
		if(p == null){
			/*p = new double[sPolicy.length][actions.length][sPolicy.length];
			for(int x = 0; x < p.length; x ++){
				for(int a = 0; a < actions.length; a++){
					for(int s = 0; s < p.length; s++){
						p[x][a][s] = 1.0/p.length;
					}
				}
			}*/
			
			//initialize the policy to a uniformly random policy
			//randomizePolicy(sPolicy);
			
			//the q(a|x) policy
			q = new double[sPolicy.length][actions.length];
			
			ps = new double[timeSteps][sPolicy.length];

			if(isGrid){
				for(int j = 0; j < sPolicy.length; j++){
					ps[0][j] = 0;
				}
				//if(isEmpty){
					ps[0][startState]++;
				/*}
				//as now the initiation set is the entire left column
				else{
					for(int j = 0; j < 10; j++){
						ps[0][j*10]++;
					}
				}*/
			}
			else{
				//not modified for obstacles
				//because the crossroads environment doesn't have obstacles anyway
				for(int j = 0; j < sPolicy.length; j++){
					ps[0][j] = 1.0/sPolicy.length;
				}
			}
			/*for(int i = 0; i < timeSteps; i++){
				for(int j = 0; j < sPolicy.length; j++){
					ps[i][j] = 1.0/sPolicy.length;
				}
			}*/
		}
	}
	
	private void setP(){
		p = new double[sPolicy.length][actions.length][sPolicy.length];
		
		int count = 0;
		for(int i = 0; i < p.length; i++){
			if(!env.isObstacle(i)){
				count++;
			}
		}
		for(int x = 0; x < p.length; x ++){
			for(int a = 0; a < actions.length; a++){
				for(int s = 0; s < p.length; s++){
					if(!env.isObstacle(x) && !env.isObstacle(s)){
						p[x][a][s] = 1.0/count;
					}
				}
			}
		}
	}
	
	/**
	 * Randomizes the policy so that for every state there is a dominant action.
	 * @param pol
	 */
	public void randomPolicy(double[][] pol){

		Random rand = new Random();
		int choice;
		for(int i = 0; i < pol.length; i++){
			choice = rand.nextInt(pol[0].length);
			pol[i][choice] = rand.nextDouble()*(1-0.5) + 0.5;
			for(int j = 0; j < pol[0].length; j++){
				if(j != choice){
					pol[i][j] = (1-pol[i][choice])/(actions.length-1);
				}
			}
		}
	}
	
	/**
	 * @return the combination of the two inputed policies
	 */
	public double[][] combinePolicy(double[][] p1, double[][] p2 , double b){
		double[][] pol = new double[p1.length][p1[0].length];
		
		for(int i = 0; i < pol.length; i++){
			for(int j = 0; j < pol[0].length; j++){
				pol[i][j] = (1-b)*p1[i][j] + b*p2[i][j];
			}
		}
		return pol;
	}
	
	//figure out which options are available in state s
	private String[] getOptions(int s){
		
		ArrayList<String> acts = new ArrayList<String>();
		acts.add("up");
		acts.add("down");
		acts.add("left");
		acts.add("right");
		
		Enumeration<Option> options = env.getOptions();
		
		Option o;
		while(options.hasMoreElements()){
			o = options.nextElement();
			
			if(o.isExecutable(s)){
				acts.add(o.getName());
			}
		}
		
		return acts.toArray(new String[]{});
	}
	
	//select an action using the probabilities specified in pi
	public String selectAction(int s){
		
		//reduces the number of possible actions for the state
		String[] actions = getOptions(s);
		
		/*if(actions.length > 4){
			System.out.println(actions.length);
		}*/
		
		double[] helper = new double[actions.length-1]; //length 3
		
		for(int i = 0; i < helper.length; i++){
			/*for(int j = 0; j <= i; j++){
				helper[i] += sPolicy[s][j];
			}*/
			if(i > 0){
				helper[i] = helper[i-1] + sPolicy[s][getActionIndex(actions[i])]; 
			}
			else{
				helper[i] = sPolicy[s][getActionIndex(actions[i])];
			}
		}
		
		//		{sPolicy[s][0], sPolicy[s][0] + sPolicy[s][1],
		//	sPolicy[s][0] + sPolicy[s][1] + sPolicy[s][2]};		
				
		double x = Math.random();
		
		for(int i = helper.length-1; i >= 0; i--){
			if(x > helper[i]){
				return actions[i+1];
			}
		}
		return actions[0];
		
		/*if(x > helper[2]){
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
		}*/
	}
	
	private double getMaxQ(int state){
		double currBest = Integer.MIN_VALUE;
		
		double currVal;
		for(int i = 0; i < actions.length; i++){
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
	
	//learn using deterministic annealing
	/*@Override
	public void learn(int steps){
		//by convergence until the policy doesn't change...
		double[][] prevPol = new double[sPolicy.length][actions.length];
		while(getDistrDiff(sPolicy, prevPol) > 0){
			prevPol = copyArray(sPolicy);
			detAnnlearn(steps);
			env.gotoState(startState);
		}
		
		if(lambda > 0.1){
			lambda = 1/(Math.log(counter+10));
		}
		counter++;
	}*/
	
	@Override
	public void preLearn(int steps){
		
		if(p == null){
			setP();
			//initialize the policy to a uniformly random policy
			randomizePolicy(sPolicy);
		}
		
		Random rand = new Random();
		
		int state;
		String action;
		Info result;
		for(int t = 0; t < steps; t++){
			state = env.getCurrentState();
			
			action = actions[rand.nextInt(actions.length)];
		
			result = env.performOption(action); 
		
			//compute new estimate for probability distribution
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
		}
	}
	
	//TRYING TO IGNORE OBSTACLES
	@Override
	public void learn(int steps) {

		//lambda = 1000.0;
		if(p == null){
			//trueModel();
			setP();
			//initialize the policy to a uniformly random policy
			randomizePolicy(sPolicy);
		}
		
		double[][] D = new double[q.length][actions.length];
		
		double[] pj = new double[sPolicy.length];
		
		double[] pa = new double[actions.length];
		
		int state;
		String action;
		Info result;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			
			stateCount[state]++;
			
			/*if(state == goalState){
				break;
			}*/
			
			//a)
				//calculate the new probability of visiting each state
				//using the current state we are at
				//(i.e. increase the probability that we end up at the state we are at right now
				//at time step t, and decrease the others)
			
			//add 1 to the current state estimate (seems too simple though)
			//maybe use the policy or the probability distribution, or both
			ps[i][state] += 1;
			
			for(int x = 0; x < q.length; x++){
				ps[i][x] = ps[i][x]/2;
			}
			
			//b)
			
			//just set q to some random policy
			//(1-b)*sPolicy + b*random (b = 0.3)
			
			//randomizePolicy(q);
			//q = combinePolicy(sPolicy, q, 0.1);
			q = copyArray(sPolicy);
			
			//c)
				//what does it mean for the difference between q(j) and q(j+1) to be small? 0.1
			double[][] qPrev = new double[q.length][actions.length];
			
			double distDiff = Math.abs(getDistrDiff(q, qPrev));
			double prevDistDiff = distDiff+1;
			
			//also stop if the difference between the two distributions starts increasing
			//or stays the same (infinite loop otherwise)
			while(distDiff > 0.5){ 
				//distribution differences are too high (can only find local optimum anyway)
				if(distDiff >= prevDistDiff){
					break;
				}
				
				qPrev = copyArray(q);

				//update pa (this is ok)
				//paWriter.println("");
				
				for(int j = 0; j < actions.length; j++){
					double sum = 0;
					
					//for each state
					for(int k = 0; k < q.length; k++){
						sum += q[k][j]*ps[i][k];
					}
					pa[j] = sum;
					//paWriter.print(pa[j] + ", ");
				}
				
				//update pj
				//pjWriter.println("");
				for(int j = 0; j < pj.length; j++){
					
					if(!withObs && env.isObstacle(j)){
						continue;
					}
					
					double sum = 0;
					for(int x = 0; x < pj.length; x++){
						
						if(!withObs && env.isObstacle(x)){
							continue;
						}
						
						for(int a = 0; a < actions.length; a++){
							sum += p[x][a][j]*q[x][a]*ps[i][x];

						}
					}
					pj[j] = sum;
 				}
				
				//update q
				//D = new double[q.length][actions.length];
				double[] Z = new double[q.length];
				
				//DWriter.println("");
				
				int x = state;
				
				String[] acts = getOptions(x);
				
				//for(int x = 0; x < q.length; x++){
					
					if(!withObs && env.isObstacle(x)){
						continue;
					}
					
					double norm = 0;
					for(int a = 0; a < actions.length; a++){
						
						//if the current action is an option that is not in the list
						if(actions[a].charAt(0) == 'o' && !contains(acts, actions[a])){
							continue;
						}
						
						//set D
						double sum = 0;
						for(int k = 0; k < D.length; k++){
							
							if(!withObs && env.isObstacle(k)){
								continue;
							}
							
							if(p[x][a][k] != 0 && pj[k] != 0){
								sum += p[x][a][k]*(Math.log(p[x][a][k]/pj[k])/Math.log(2));
							}
							else if(p[x][a][k] == 0 && pj[k] == 0){
								sum += 0;
							}
							else if(pj[k] == 0){
								sum = Double.POSITIVE_INFINITY;
							}
						}
						D[x][a] = sum;
						norm += sum;
					}
					for(int a = 0; a < actions.length; a++){
						if(norm == 0){
							break;
						}
						D[x][a] = D[x][a]/norm;
					}
				//}
				
				//for(int x = 0; x < q.length; x++){
					
					//set Z
					double temp;
					double log0;
					if(withPa){
						log0 = Math.log(pa[0]) + ((1/lambda)*(D[x][0] + alpha*qValues[x][0]));
					}
					else{
						log0 = ((1/lambda)*(D[x][0] + alpha*qValues[x][0]));
					}
					double logZ = 1;
					for(int k = 1; k < actions.length; k++){
						
						//if the current action is an option that is not in the list
						if(actions[k].charAt(0) == 'o' && !contains(acts, actions[k])){
							continue;
						}
						
						temp = ((1/lambda)*(D[x][k] + alpha*qValues[x][k]));
						
						if(withPa){
							logZ += Math.exp((Math.log(pa[k]) + temp) - log0);
						}
						else{
							logZ += Math.exp((temp) - log0);
						}
					}
					logZ = Math.log(logZ);
					logZ += log0;
					Z[x] = logZ;
					
					for(int a = 0; a < actions.length; a++){
						
						//if the current action is an option that is not in the list
						if(actions[a].charAt(0) == 'o' && !contains(acts, actions[a])){
							q[x][a] = 0;
						}
						else{
							if(!Double.isInfinite(Z[x])){
								double exp = (1/lambda)*(D[x][a] + alpha*qValues[x][a]);
						
								if(withPa){
									q[x][a] = (Math.log(pa[a]) + exp) - Z[x];
								}
								else{
									q[x][a] = (exp) - Z[x];
								}	
								q[x][a] = Math.exp(q[x][a]);
							}
						}
						
						if(Double.isNaN(q[x][a])){
							System.out.println("D: " + D[x][a] + ", x: " + x);
							return;
						}
						
					}
				//}

				prevDistDiff = distDiff;
				distDiff = getDistrDiff(q,qPrev);
			}
			sPolicy = copyArray(q);
			
			//d) choose action a (using pi) and obtain reward and next state
			action = selectAction(state);
			
			result = env.performOption(action); 
			
			//if the action is an option we want to increase the state visitation for all
			//states visited within the option as well
			if(action.charAt(0) == 'o'){
				visitOption(result);
			}
			
			
			//compute new estimate for probability distribution
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
			
			int index = getActionIndex(action);
			
			//to see how often options are taken in general
			/*if(action.charAt(0) == 'o'){
				System.out.println("state: " + state + ", o: " + action);
			}*/
			
			//want to count how many times each action gets taken in the state
			//if(state == stat){
				actionDist[index]++;
			//}
			//
			
			int timeSteps = result.getTimeSteps();
			if(timeSteps > 1){
				//do additional updates on the previous states
				State[] states = result.getStates();
				Double[] rewards = result.getRewards();
				
				int st;
				for(int x = 0; x < states.length-1; x++){
					st = states[x].getName();
					qValues[st][index] = qValues[st][index] + 0.1*(rewards[x]
							+ Math.pow(0.9, timeSteps-(x+1))*getMaxQ(result.getState().getName())
							- qValues[st][index]);
				}
			}
			if(timeSteps > 0){
				//if this is a primitive action we want to penalize it
				if(isEmpty && action.charAt(0) != 'o'){
					qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
							+ Math.pow(0.9, result.getTimeSteps())*getMaxQ(result.getState().getName())
							- qValues[state][index]);
					qValues[state][index] = qValues[state][index]/1.1;
				}
				else{
					qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ Math.pow(0.9, result.getTimeSteps())*getMaxQ(result.getState().getName())
					- qValues[state][index]);
				}
			}
			
			//take from Q-learning (for now, eventually want to do a full Q value evaluation)
			/*int index = getActionIndex(action);
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
					+ Math.pow(0.9, result.getTimeSteps())*getMaxQ(result.getState().getName()) 
					- qValues[state][index]);*/

			
			//if probability of option being taken goes below 0.001 at every state where it can be taken
			//we remove this option completely and renormalize all policies that can take it
			if (!isEmpty) {
				//pjWriter.println("");
				for (int a = 0; a < actions.length; a++) {
					if (actions[a].charAt(0) == 'o') {
						Option o = env.getOption(actions[a]);
	/*					
						boolean isTaken = false;

						//pjWriter.println("\n" + actions[a] + " - ");
						int[] ini = o.getIni();
						for (int j = 0; j < ini.length; j++) {
							if (sPolicy[ini[j]][a] > 0.001) {
								isTaken = true;
							}
							//print the probability of taking that action for every state
							//to a file
							//pjWriter.print("(" + ini[j] + ", " + sPolicy[ini[j]][a] + "); ");
						}

						//if the option gets taken somewhere by a probability greater than 0.01, then
						//we leave it alone
						if (isTaken || ini.length < 1) {
							continue;
						}
						
						//remove the option from all policies
						double norm;
						for (int j = 0; j < ini.length; j++) {
							sPolicy[ini[j]][a] = 0;

							norm = 0.0;
							for (int k = 0; k < actions.length; k++) {
								norm += sPolicy[ini[j]][k];
							}

							for (int k = 0; k < actions.length; k++) {
								sPolicy[ini[j]][k] = sPolicy[ini[j]][k] / norm;
							}
						}
						
						//set all Q-values in it's initiation set to 0
						for(int j = 0; j < ini.length; j++){
							//for(int ac = 0; ac < actions.length; ac++){
							//	qValues[ini[j]][ac] = 0;
							//}
							qValues[ini[j]][a] = 0;
						}
						
						//remove the option completely so it cannot be taken again (just make its initiation set empty?)
						o.setIni(new int[0]);

						//the option that was removed
						paWriter.println("Option removed: " + o.getName()
								+ ", at trial: " + counter + ", with rank: "+ optionRank[a-4]);
						System.out.println("Option removed: " + o.getName() + ", at trial: " + counter
								+ ", with rank: "+ optionRank[a-4]);
	*/					
						/*
						 * FOR CONTINUALLY ADDING OPTIONS:
						 * 	- remove this option completely from the list and replace it with
						 * 		a new random option
						 *  - change the actions array to have the name of the new option replace
						 *  	the name of the old option
						 *  - renormalize the policy for the options old initiation set, and give a
						 *  	random policy to the states of the options new initation set
						 *  	(to give this new option a fair chance)
						 *  - options are rated according to how long they last and how many
						 *  	states take them at each time step. The rating is printed out when
						 *  	the option is removed. To help know which options are good.
						 */
						if(keepAddingOptions){
							Option newOp = env.replaceOption(o.getName());
							actions[a] = newOp.getName(); //name of new option replaces name of old option
							optionRank[a-4] = 0;
							
							//give a random policy to states in the new initiation set
							//uniform for now
							double prob = 1.0/(actions.length);
							int[] newIni = newOp.getIni();
							for(int k = 0; k < newIni.length; k++){
								for(int ac = 0; ac < actions.length; ac++){
									sPolicy[newIni[k]][ac] = prob;
									//qValues[newIni[k]][ac] = 0; //reinitialize Q-values as well
								}
								qValues[newIni[k]][a] = 0; //reinitialize Q-values as well
							}
							
							System.out.println("Option " + newOp.getName() + " added at trial: " + counter);
						}
					}
				}
			}
			//if this is the empty environment then we want to remove states from the initiation set of the
			//large random option
			else{
				for (int a = 0; a < actions.length; a++) {
					if (actions[a].charAt(0) == 'o') {
						
						Option o = env.getOption(actions[a]);;

						int[] ini = new int[o.getIni().length];
						System.arraycopy(o.getIni(), 0, ini, 0, ini.length);
						
						for (int j = 0; j < ini.length; j++) {
							if (sPolicy[ini[j]][a] < 0.0001) {
								//remove this state from the initiation set and the policy
								int[] oldIni = o.getIni();
								int[] newIni = new int[oldIni.length-1];
								
								Hashtable<Integer, String> pol = o.getPolicy();
								
								int t = 0;
								for(int k = 0; k < newIni.length; k++){
									if(oldIni[t] == ini[j]){
										k--;
									}
									else{
										newIni[k] = oldIni[t];
									}
									t++;
								}
								
								pol.remove(ini[j]);
								
								o.setIni(newIni);
								o.setPolicy(pol);
								
								System.out.println("Removed state: " + ini[j]);
								paWriter.println("Removed state: " + ini[j] + ", at trial " + counter);
							}
						}
					}
				}
			}	
		}
		
		//print for each option the number of states that are still taking that option
		//also calculate ranking of each option
		pjWriter.println("");
		for (int a = 0; a < actions.length; a++) {
			if (actions[a].charAt(0) == 'o') {
				Option o = env.getOption(actions[a]);

				int count = 0;
				
				//pjWriter.println("\n" + actions[a] + " - ");
				int[] ini = o.getIni();
				for (int j = 0; j < ini.length; j++) {
					if (sPolicy[ini[j]][a] > 0.001) {
						//this option is taken in this state
						count++;
					}
				}
				pjWriter.print(count + ",");
				
				//calculate rank of option
				optionRank[a-4] = optionRank[a-4] + count;
			}
		}
		
		//replace the lowest ranking option only (every 300 trials)
		if(replaceLowestRanking && counter%300 == 0){
			double lrank = Double.POSITIVE_INFINITY;
			int index = -1;
			Option lowest;
			//find lowest ranking option
			for(int i = 0; i < optionRank.length; i++){
				if(optionRank[i] < lrank){
					lrank = optionRank[i];
					index = i+4;
				}
			}
			
			//remove option
			lowest = env.getOption(actions[index]);
			lowest.setIni(new int[0]);
			
			paWriter.println("Option removed: " + lowest.getName()
					+ ", at trial: " + counter + ", with rank: "+ optionRank[index-4]);
			System.out.println("Option removed: " + lowest.getName() + ", at trial: " + counter
					+ ", with rank: "+ optionRank[index-4]);
			
			//add new option
			Option newOp = env.replaceOption(lowest.getName());
			actions[index] = newOp.getName(); //name of new option replaces name of old option
			optionRank[index-4] = 0;
			
			//give a random policy to states in the new initiation set
			//uniform for now
			double prob = 1.0/(actions.length);
			int[] newIni = newOp.getIni();
			for(int k = 0; k < newIni.length; k++){
				for(int ac = 0; ac < actions.length; ac++){
					sPolicy[newIni[k]][ac] = prob;
				}
			}
			
			System.out.println("Option " + newOp.getName() + " added at trial: " + counter);
		}
		
		//find and print the greedy policy with only top 4 options included
		if(!keepAddingOptions && counter%500 == 0){
			//need to verify that this works
			
			//determine top 4 options based on rank
			int currCount = 0; //how many slots are filled
			int currLowest = -1; //index of the current lowest ranked option in top4
			int[] top4 = {-1,-1,-1,-1}; //indices of top 4 options
			for(int i = 4; i < actions.length; i++){
				
				System.out.print(actions[i] + ": " + optionRank[i-4] + ", ");
				
				if(currCount < 4){
					top4[currCount] = i;
					currCount++;
					
					if(currLowest > -1){
						if(optionRank[i-4] < optionRank[currLowest-4]){
							currLowest = i;
						}
					}
					else{
						currLowest = i;
					}
				}
				
				//if we found an option ranked higher than the currently lowest ranked option
				//in top4, then replace currLowest with i
				else if(optionRank[currLowest-4] < optionRank[i-4]){
					for(int j = 0; j < top4.length; j++){
						if(top4[j] == currLowest){
							top4[j] = i;
							break;
						}
					}
					//find the new currLowest
					int min_index = top4[0];
					for(int j = 1; j < top4.length; j++){
						if(optionRank[top4[j]-4] < optionRank[min_index-4]){
							min_index = top4[j];
						}
					}
					currLowest = min_index;
				}
			}
			System.out.println();
			for(int t = 0; t < 4; t++){
				System.out.print(actions[top4[t]] + ", ");
			}
			System.out.println("Lowest: " + currLowest);
			
			String[] greedyPol = new String[sPolicy.length]; //greedy deterministic policy
			//for each state, which is not an obstacle, pick the action with the highest
			//Q value for the policy (ignoring options which are not in the top 4)
			for(int x = 0; x < sPolicy.length; x++){
				if(!env.isObstacle(x)){
					int bestAct = -1;
					double bestQ = -1;
					//check that this works
					qWriter.println("\n" + x);
					for(int a = 0; a < actions.length; a++){
						if(a < 4 /*|| Arrays.asList(top4).contains(a)*/){
							qWriter.print(actions[a] + ": " + qValues[x][a] + ", ");
							if(qValues[x][a] > bestQ){
								bestAct = a;
								bestQ = qValues[x][a];
							}
						}
						else{
							for(int t = 0; t < top4.length; t++){
								if(top4[t] == a){
									qWriter.print(actions[a] + ": " + qValues[x][a] + ", ");
									if(qValues[x][a] > bestQ){
										bestAct = a;
										bestQ = qValues[x][a];
									}
									break;
								}
							}
							
						}
					}
					greedyPol[x] = actions[bestAct]; //print instead of putting in array
				}
			}
			System.out.println();
			//print the greedyPol to a file (just use return file for now)
			for(int x = 0; x < greedyPol.length; x++){
				returnWriter.println(x + ":" + greedyPol[x]);
			}
			returnWriter.println();
			
		}
		
		//print D values to a file
		DWriter.println("");
		for(int a = 0; a < actions.length; a++){
			//D[x][a]
			DWriter.print(D[stat][a] + ", ");
		}
		
		System.out.println("trial " + counter);
		lambda = 1/(Math.log(counter+1.001));
		//lambda = 1/(Math.log(counter+2));
		counter++;
		
		//want to know the rank of the remaining options
		if(counter == 2999){
			for(int i = 4; i < actions.length; i++){
				paWriter.println("Option left: " + actions[i] + ", with rank: "+ optionRank[i-4]);
			}
		}
	}
	
	//need to also account for options
	/**
	 * Finds the true model of the environment.
	 */
	private void trueModel(){
		p = new double[sPolicy.length][actions.length][sPolicy.length];
		
		//prevent actions from failing
		env.toggleActionFail();
		
		String[] acts = {"up", "down", "left", "right"};
		
		//go through every state
		for(int x = 0; x < 100; x++){
			if(env.isObstacle(x)){
				for(int s = 0; s < 100; s++){
					for(int a = 0; a < acts.length; a++){
						p[x][a][s] = 0;
						p[s][a][x] = 0;
					}
				}
			}
			
			else{
				Info result;
				int s;
				for(int a = 0; a < acts.length; a++){
					env.gotoState(x);
					result = env.performOption(acts[a]);
					s = result.getState().getName();
					p[x][a][s] = 0.7;
					for(int ac = 0; ac < acts.length; ac++){
						if(ac != a){
							p[x][ac][s] += 0.1;
						}
					}
				}
			}
		}
		
		//allow actions to fail again
		env.toggleActionFail();
	}
	
	private void visitOption(Info r) {
		
		//update each state that was visited except for the last one
		State[] s = r.getStates();
		
		for(int i = 0; i < s.length-1; i++){
			stateCount[s[i].getName()]++;
		}
	}

	/**
	 * Check whether array a contains string s
	 * @param a
	 * @param s
	 * @return
	 */
	public boolean contains(String[] a, String s){
		for(int i = 0; i < a.length; i++){
			if(a[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public void printSum(double[] a){
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