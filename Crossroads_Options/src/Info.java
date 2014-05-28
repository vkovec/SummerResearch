public class Info{
	private State s;
	private double reward;
	private int timeSteps;
	
	public Info(State state, double r, int t){
		s = state;
		reward = r;
		timeSteps = t;
	}
	
	public State getState(){
		return s;
	}
	
	public double getReward(){
		return reward;
	}
	
	public int getTimeSteps(){
		return timeSteps;
	}
}