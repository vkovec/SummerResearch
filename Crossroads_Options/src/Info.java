public class Info{
	private State s;
	private int reward;
	
	public Info(State state, int r){
		s = state;
		reward = r;
	}
	
	public State getState(){
		return s;
	}
	
	public int getReward(){
		return reward;
	}
}