package gridenv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import tools.Option;
import tools.State;

import agent.Agent;
import agent.InfTheoryLearning;
import agent.QLearning;
import agent.TDLearning;

@SuppressWarnings("serial")
public class GridEnvDisplay extends JFrame{
	
	private boolean isI = false;
	
	private GridEnvironment gridEnv;
	private int start;
	private int goal;
	//private int prev = -1;
	
	private Agent agent;
	
	private JPanel[][] grid;
	private ArrayList<JLabel> labels;
	
	private DecimalFormat df;
	
	public GridEnvDisplay(Agent a){
		agent = a;
		
		df = new DecimalFormat("#.###");
		
		this.setTitle("Grid Environment");
		
		gridEnv = new GridEnvironment(10, isI);
		
		if(!isI){
			//set the start and the goal states
			start = 0;
			goal = 99;	
		}
		else{
			start = 4;
			goal = 94;
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initializeGrid(){
		//first column is for buttons
		int n = gridEnv.getGridSize();
		
		GridLayout g = new GridLayout(n, n+1);
		g.setHgap(-1);
		g.setVgap(-1);
		setLayout(g);
		
		
		grid = new JPanel[n][n+1];
		
		for(int i = 0; i < n; i ++){
			for(int j = 0; j < n+1; j++){
				grid[i][j] = new JPanel();
				// grid[i][j].setPreferredSize(new Dimension(50,50));
				if(j != 0){
					Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
					grid[i][j].setBorder(border);
				}
				
				add(grid[i][j]);
			}
		}
	}
	
	private void displayEnv(){
		initializeGrid();
		
		int n = gridEnv.getGridSize();
		
		labels = new ArrayList<JLabel>();
		
		JLabel label;
		for(int i = 0; i < n*n; i++){
			label = new JLabel();
			
			label.setOpaque(true);
			
			label.setPreferredSize(new Dimension(70,70));
			labels.add(label);
		}
		
		State[][] states = gridEnv.getStates();
		int x = 0;
		for(int i = 0; i < n; i++){
			for(int j = 1; j < n+1; j++){
				grid[i][j].add(labels.get(x));
				if(states[i][j-1].isObstacle()){
					labels.get(x).setBackground(Color.black);
				}
				x++;
			}
		}
		
		if(!isI){
			//the interesting area
			/*for(int i = 53; i < 57; i++){
				labels.get(i).setBackground(Color.gray);
			}
			labels.get(65).setBackground(Color.gray);
			labels.get(66).setBackground(Color.gray);*/
		}
		else{
			labels.get(42).setBackground(Color.gray);
			labels.get(43).setBackground(Color.gray);
			labels.get(52).setBackground(Color.gray);
			labels.get(53).setBackground(Color.gray);
		}
		
		//for the start and goal states
		labels.get(start).setText("Start");
		labels.get(start).setBackground(Color.blue);
		labels.get(goal).setText("Goal");
		labels.get(goal).setBackground(Color.green);
		
		//displaying the random options
		Enumeration<Option> ops = gridEnv.getOptions();
		Option o;
		int[] ini;
		while(ops.hasMoreElements()){
			o = ops.nextElement();
			
			//only want to show the random ones
			if((!o.getName().equals("oright") && !isI) || (!o.getName().equals("old"))){
				ini = o.getIni();
				for(int i = 0; i < ini.length; i++){
					labels.get(ini[i]).setBackground(Color.lightGray);
					labels.get(ini[i]).setText(o.getName() + ": " + o.getAction(ini[i]));
				}
			}
		}
		
	}
	
	private void createDisplay() {
		displayEnv();
		
		//add button to run the agent, and display the results
		JButton trial = new JButton("Trial");
		trial.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
						
				agent.setEnv(gridEnv, start, goal);
				agent.learnTrial(1000);
						
				double[] vals = agent.getValues();
				for(int i = 0; i < vals.length; i++){
					if(i != goal){
						labels.get(i).setText("" + df.format(vals[i]));
					}
				}
			}
		});
		grid[0][0].add(trial);
		
		//for learning algorithms that use Q-values instead
		//just averaging the Q-values for each action
		JButton qtrial = new JButton("QTrial");
		qtrial.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				agent.setEnv(gridEnv, start, goal);
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
				
				int[] stateCount = agent.getStateCount();
				for(int i = 0; i < stateCount.length; i++){
					stateCount[i] = stateCount[i]/2;
					if(stateCount[i] > 255){
						stateCount[i] = 255;
					}
					if(!labels.get(i).getBackground().equals(Color.black)){
						labels.get(i).setBackground(new Color(255,0,0, stateCount[i]));
					}
				}
			}
		});
		grid[1][0].add(qtrial);
		
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
		grid[2][0].add(policy);
	}
	
	public static void main(String[] args){
		GridEnvDisplay e = new GridEnvDisplay(new InfTheoryLearning(10));
		
		e.createDisplay();
		e.pack();
		e.setVisible(true);
	}
}