public class Info{
	private State s;
	private double reward;
	
	public Info(State state, double r){
		s = state;
		reward = r;
	}
	
	public State getState(){
		return s;
	}
	
	public double getReward(){
		return reward;
	}
}