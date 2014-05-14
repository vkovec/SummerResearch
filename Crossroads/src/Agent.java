public abstract class Agent{
	protected Environment env;
	
	protected double[] values = null;
	protected int startState;
	protected int goalState;
	
	protected String[] actions = {"up", "down", "left", "right"};
	
	//values
	//Q-values (?)
	//policy
	
	public void setEnv(Environment e, int start, int goal, int n){
		env = e;
		startState = start;
		goalState = goal;
		if(values == null){
			values = new double[2*n-1];
		}
	}
	
	public double[] getValues(){
		return values;
	}
	
	//public abstract double[] learn(State start, State goal, double[] initialVals, int steps);
	public abstract void learn(int steps);
	
	public void learnTrial(int eps){
		for(int i = 0; i < eps; i++){
			learn(100);
			env.gotoState(startState);
		}
	}
}