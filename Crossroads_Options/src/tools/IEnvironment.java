package tools;

public interface IEnvironment{
	
	/**
	 * @return the current state the agent is at in the environment.
	 */
	public int getCurrentState();
	
	/**
	 * @param p the probability with which the experiment succeeds.
	 * @return whether or not the experiment succeeded.
	 */
	public int getBernouilli(double p);
	
	/**
	 * @param o the name of the option/action to perform
	 * @return the next state and reward
	 */
	public Info performOption(String o);
	
	/**
	 * @param s the state where the agent should be placed.
	 */
	public void gotoState(int s);
	
	/**
	 * @param o the name of the option to get
	 * @return the option
	 */
	public Option getOption(String o);
}