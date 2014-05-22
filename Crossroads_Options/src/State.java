import java.util.ArrayList;

public class State{
	private int name;
	private int reward;
	
	//up, down, left, right
	private ArrayList<State> neighbors;
	
	public State(int n, int r){
		name = n;
		reward = r;
		neighbors = new ArrayList<State>();
	}
	
	public void setNeighbors(State up, State down, State left, State right){
		neighbors.add(up);
		neighbors.add(down);
		neighbors.add(left);
		neighbors.add(right);
	}
	
	public int getReward(){
		return reward;
	}
	
	public void setReward(int r){
		reward = r;
	}
	
	public int getName(){
		return name;
	}

	public ArrayList<State> getNeighbors(){
		return neighbors;
	}
}