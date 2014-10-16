package tools;
import java.util.ArrayList;

public class State{
	private int name;
	private double reward;
	private boolean isObstacle;
	
	//up, down, left, right
	private ArrayList<State> neighbors;
	
	public State(int n, double r){
		isObstacle = false;
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
	
	public double getReward(){
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
	
	public boolean isObstacle(){
		return isObstacle;
	}
	
	public void setAsObstacle(boolean b){
		isObstacle = b;
	}
}