package agent;

import java.util.Random;

import tools.Info;
import tools.Option;
import tools.State;

public class QLearning extends Agent{

	public QLearning(int n) {
		super(n);
	}

	public String findBestAction(int s){
		//action that has given us the best Q values so far for this state
		String best = null;
		
		//check if all actions are the same
		int same = 0;
		double currBest = Integer.MIN_VALUE;
		double currVal;
		for(int i = 0; i < actions.length; i++){
			currVal = qValues[s][i];
			
			if(currVal > currBest){
				currBest = currVal;
				best = actions[i];
			}
			
			if(currVal == currBest){
				same++;
			}
		}
		if(same >= actions.length-1){
			Random rand = new Random();
			best = actions[rand.nextInt(actions.length)];
		}
		return best;
	}
	
	/*private int getActionIndex(String action){
		for(int i = 0; i < 4; i++){
			if(actions[i].equals(action)){
				return i;
			}
		}
		return -1;
	}*/
	
	private double getMaxQ(int state){
		double currBest = Integer.MIN_VALUE;
		
		double currVal;
		for(int i = 0; i < actions.length; i++){
			currVal = qValues[state][i];
			
			if(currVal > currBest){
				currBest = currVal;
			}
		}
		return currBest;
	}
	
	@Override
	public void learn(int steps) {

		int state;
		Info result;
		String action;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			if(state == goalState){
				return;
			}
			
			//choose A from S using policy derived from Q
			//using e-greedy here
			//(with probability 0.7 we take best action)
			action = findBestAction(state);
			//have to change this slightly so we don't randomly select 
			//the best action
			if(env.getBernouilli(0.7) != 1){
				Random rand = new Random();
				action = actions[rand.nextInt(actions.length)];
			}
			
			result = env.performOption(action);
			
			int index = getActionIndex(action);
			
			int timeSteps = result.getTimeSteps();
			
			if(timeSteps > 1){
				//do additional updates on the previous states
				State[] states = result.getStates();
				Double[] rewards = result.getRewards();
				
				int s;
				for(int x = 0; x < states.length-1; x++){
					s = states[x].getName();
					//THIS IS PROBABLY WRONG AS NEXT STATE SHOULD BE THE 
					//STATE WHERE THE OPTION ENDS, AND GAMMA SHOULD BE DISCOUNTED ACCORDING
					//TO HOW MANY TIMESTEPS HAVE PAST (I think)
					/*qValues[s][index] = qValues[s][index] + 0.1*(rewards[x]
							+ 0.9*getMaxQ(states[x+1].getName())
							- qValues[s][index]);*/
					
					//this could be wrong as well (especially the timestep part)
					qValues[s][index] = qValues[s][index] + 0.1*(rewards[x]
							+ Math.pow(0.9, timeSteps-(x+1))*getMaxQ(result.getState().getName())
							- qValues[s][index]);
				}
			}
			
			qValues[state][index] = qValues[state][index] + 0.1*(result.getReward()
				+ Math.pow(0.9, timeSteps)*getMaxQ(result.getState().getName())
				- qValues[state][index]);
		}
	}

}