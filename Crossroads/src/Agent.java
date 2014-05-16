/**
 * @author vkovec
 *
 */
public abstract class Agent{
	protected Environment env;
	
	protected double[] values = null;
	protected int startState;
	protected int goalState;
	
	protected String[] actions = {"up", "down", "left", "right"};
	
	//the policy that we find after learning
	protected String[] policy = null;
	
	//for stochastic policies
	//(i.e. probability of taking action a in state s)
	protected double[][] sPolicy = null;
	
	//the Q-values that we find after learning
	protected double[][] qValues = null;
	
	public void setEnv(Environment e, int start, int goal, int n){
		env = e;
		startState = start;
		goalState = goal;
		if(values == null){
			values = new double[2*n-1];
		}
		if(qValues == null){
			qValues = new double[2*n-1][4];
		}
		if(policy == null){
			policy = new String[2*n-1];
		}
		if(sPolicy == null){
			sPolicy = new double[2*n-1][4];
		}
	}
	
	public double[] getValues(){
		return values;
	}
	
	public double[][] getQValues(){
		return qValues;
	}
	
	/**
	 * @param isQ whether or not we want to find the policy using Q-values
	 * @return the policy
	 */
	/*public String[] getPolicy(boolean isQ){
		if(!isQ){
			
		}
		return policy;
	}*/
	
	//public abstract double[] learn(State start, State goal, double[] initialVals, int steps);
	public abstract void learn(int steps);
	
	public void learnTrial(int eps){
		for(int i = 0; i < eps; i++){
			learn(100);
			env.gotoState(startState);
		}
		for(int i = 0; i < sPolicy.length; i++){
			for(int j = 0; j < actions.length; j++){
				System.out.println("State: " + i + ", Action " + actions[j] + ": " + sPolicy[i][j]);
			}
		}
	}
}