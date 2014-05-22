import java.util.Random;

public class TDLearning extends Agent{

	public TDLearning(int n) {
		super(n);
	}

	@Override
	public void learn(int steps) {
		//need to input a policy to be evaluated...
		//just use a random policy for now
		Random rand = new Random();
		
		int state;
		String action;
		Info result;
		for(int i = 0; i < steps; i++){
			state = env.getCurrentState();
			if(state == goalState){
				return;
			}
			
			action = actions[rand.nextInt(actions.length)];
			
			//System.out.println(action);
			
			result = env.performOption(action);
			values[state] = values[state] + 0.1*(result.getReward() 
					+ 0.9*values[result.getState().getName()] - values[state]);
			
			//System.out.println("State: " + state + " Value: " + values[state]);
		}
	}
}