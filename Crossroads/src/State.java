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
	
	public State performAction(String action){
		if(action.equals("up")){
			return neighbors.get(0);
		}
		else if(action.equals("down")){
			return neighbors.get(1);
		}
		else if(action.equals("left")){
			return neighbors.get(2);
		}
		else{
			return neighbors.get(3);
		}
	}
	
	/*@Override
	public State clone(){
		try{
			State clone = (State) super.clone();
			
			
			return clone;
		}
		catch(CloneNotSupportedException e){
			return null;
		}
	}*/
}