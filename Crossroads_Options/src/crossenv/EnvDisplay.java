package crossenv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import agent.Agent;
import agent.InfTheoryLearning;
import agent.QLearning;

@SuppressWarnings("serial")
public class EnvDisplay extends JFrame{
	
	private Environment env;
	private int n;
	private int start;
	private int goal;
	private int prev = -1;
	
	private Agent agent;
	
	private JPanel[][] grid;
	private ArrayList<JLabel> labels;
	
	private DecimalFormat df;
	
	public EnvDisplay(int n, Agent a){
		df = new DecimalFormat("#.###");
		
		this.setTitle("Crossroads");
		
		this.n = n;
		
		env = new Environment(n);
		//env.initializeStates(n);
		
		agent = a;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void createDisplay(){
		displayEnv();
		
		//add button to set up agent and goal state
		JButton setUp = new JButton("Setup");
		//setUp.setPreferredSize(new Dimension(50,50));
		setUp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//clear previous start and goal
				labels.get(goal).setText("");
				labels.get(goal).setBackground(Color.WHITE);
				labels.get(start).setText("");
				labels.get(start).setBackground(Color.WHITE);
				
				goal = env.chooseGoal().getName();
				start = env.chooseStart().getName();
				
				labels.get(goal).setText("goal");
				labels.get(goal).setBackground(Color.GREEN);
				labels.get(start).setText("start");
				labels.get(start).setBackground(Color.BLUE);
			}
		});
		grid[0][0].add(setUp);
		
		//add button to run the agent, and display the results
		JButton trial = new JButton("Trial");
		trial.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//run the agent
				for(int i = 0; i < 1000; i++){
					//goal = env.chooseGoal().getName();
					start = env.chooseStart().getName();
					agent.setEnv(env, start, goal);
					//agent.learnTrial(1000);
					agent.learnTrial(1);
				}
				
				//agent.setEnv(env, start, goal);
				//agent.learnTrial(100);
				
				double[] vals = agent.getValues();
				for(int i = 0; i < vals.length; i++){
					if(i != goal){
						labels.get(i).setText("" + df.format(vals[i]));
					}
				}
			}
		});
		grid[1][0].add(trial);
		
		//for learning algorithms that use Q-values instead
		//just averaging the Q-values for each action
		JButton qtrial = new JButton("QTrial");
		qtrial.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//run the agent
				/*for(int i = 0; i < 1000; i++){
					//goal = env.chooseGoal().getName();
					start = env.chooseStart().getName();
					agent.setEnv(env, start, goal);
					agent.learnTrial(1);
				}*/
				
				agent.setEnv(env, start, goal);
				agent.learnTrial(1000);
				
				double[][] qVals = agent.getQValues();
				for(int i = 0; i < qVals.length; i++){
					if(i != goal){
						double val = 0;
						//average the values for each action
						for(int j = 0; j < 6; j++){
							if(qVals[i][j] > val){
								val = qVals[i][j];
							}
						}
						labels.get(i).setText("" + df.format(val));
					}
				}
			}
		});
		grid[2][0].add(qtrial);
		
		//display the policy found
		JButton policy = new JButton("Policy");
		policy.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] p = agent.getPolicy();
				
				for(int i = 0; i < p.length; i++){
					if(i != start && i != goal){
						labels.get(i).setText(p[i]);
					}
				}
			}
		});
		grid[3][0].add(policy);
		
		//step through what the agent does
		JButton step = new JButton("Step");
		step.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//run the agent
				agent.setEnv(env, start, goal);
				agent.learn(1);
				
				if(prev > -1){
					labels.get(prev).setText("");
				}
				prev = env.getCurrentState();
				labels.get(prev).setText("curr");
			}
		});
		grid[4][0].add(step);
	}
	
	//start with creating and displaying the environment
	public void displayEnv(){
		initializeGrid();
		
		//we are going to need 2*n-1 grid labels to represent the crossroads
		labels = new ArrayList<JLabel>();
		
		JLabel label;
		for(int i = 0; i < 2*n-1; i++){
			label = new JLabel();
			
			label.setOpaque(true);
			
			//add a border to the label
			Border border = BorderFactory.createLineBorder(Color.BLACK, 3);
			label.setBorder(border);
			
			label.setPreferredSize(new Dimension(70,70));
			labels.add(label);
		}
		
		//the first n should make a vertical line down the middle of the JFrame
		for(int i = 0; i < n; i++){
			grid[i][n/2].add(labels.get(i));
		}
		
		//the rest should make a horizontal line
		for(int i = n; i < 2*n-1; i++){
			if(i-n >= n/2){
				grid[n/2][i-n+1].add(labels.get(i));
			}
			else{
				grid[n/2][i-n].add(labels.get(i));
			}
		}
	}
	
	public void initializeGrid(){
		setLayout(new GridLayout(n,n));
		
		grid = new JPanel[n][n];
		
		for(int i = 0; i < n; i ++){
			for(int j = 0; j < n; j++){
				grid[i][j] = new JPanel();
				add(grid[i][j]);
			}
		}
	}
	
	public static void main(String[] args){
		EnvDisplay e = new EnvDisplay(11, new QLearning(11));
		e.createDisplay();
		e.pack();
		e.setVisible(true);
	}	
}