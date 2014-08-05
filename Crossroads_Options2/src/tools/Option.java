package tools;
import java.util.Hashtable;
import java.util.Random;

public class Option{
	private String name;
	
	private int[] initiationSet;
	private Hashtable<Integer, String> policy; //deterministic
	
	private double[] beta;
	
	/**
	 * @param name name of the option (has to begin with o)
	 * @param n number of states in the environment
	 * @param ini the initiation set for the option
	 * @param pol the policy for the initiation set
	 */
	public Option(String name, int n, int[] ini, String[] pol){
		this.name = name;
		
		initiationSet = ini;
		
		policy = new Hashtable<Integer, String>();
		
		//set the policy
		for(int i = 0; i < initiationSet.length; i++){
			policy.put(initiationSet[i], pol[i]);
		}
		
		beta = new double[2*n-1];
		for(int i = 0; i < beta.length; i++){
			if(isExecutable(i)){
				//for now we want to continue execution until
				//we leave the initiation set
				beta[i] = 0;
			}
			else{
				beta[i] = 1;
			}
		}
	}
	
	public Option(String name, int n, int[] ini){
		this.name = name;
		
		initiationSet = ini;
		
		policy = new Hashtable<Integer, String>();
		
		beta = new double[2*n-1];
		for(int i = 0; i < beta.length; i++){
			if(isExecutable(i)){
				//for now we want to continue execution until
				//we leave the initiation set
				beta[i] = 0;
			}
			else{
				beta[i] = 1;
			}
		}
	}
	
	//we can execute an option if the current state is in the initiation set
	public boolean isExecutable(int s){
		/*for(State s1 : initiationSet){
			if(s1.getName() == s.getName()){
				return true;
			}
		}*/
		for(int i = 0; i < initiationSet.length; i++){
			if(initiationSet[i] == s){
				return true;
			}
		}
		return false;
	}
	
	//should we terminate the option at this state?
	public boolean terminate(int s){
		if(beta[s] == 1){
			return true;
		}
		return false;
	}
	
	//get the action for a state in the initiation set
	public String getAction(int s){
		return policy.get(s);
	}
	
	public String getName(){
		return name;
	}
	
	//change the policy to a new policy
	public void setPolicy(Hashtable<Integer, String> p){
		policy = p;
	}
	
	/**
	 * @return the initiation set
	 */
	public int[] getIni(){
		return initiationSet;
	}
	
	public int sizeI(){
		return initiationSet.length;
	}
	
	
	/**
	 * @return a random state from the initiation set
	 */
	public int getRandomState(){
		Random rand = new Random();
		return initiationSet[rand.nextInt(initiationSet.length)];
	}
}