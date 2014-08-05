import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import crossenv.Environment;

import agent.InfTheoryLearning;

public class TestInfTheory
{
	private InfTheoryLearning test;
	
	@Before
	public void setUp(){
		test = new InfTheoryLearning(11);
	}
	
	@Test
	public void testRandomizePolicy(){
		double[][] pol = new double[10][6];
		
		double sum = 0;
		for(int i = 0; i < pol.length; i++){
			for(int j = 0; j < pol[0].length; j++){
				sum += pol[i][j];
			}
		}
		
		//System.out.println(sum);
		
		test.randomizePolicy(pol);
		
		sum = 0;
		for(int i = 0; i < pol.length; i++){
			for(int j = 0; j < pol[0].length; j++){
				sum += pol[i][j];
			}
		}
		
		//System.out.println(sum);
		
		//int s = (int) sum;
		//assertEquals(10, s);
		
		boolean result = (sum < 10.1 && sum > 9.9);
		assertEquals(true, result);
	}
	
	@Test
	public void testRandPolicy(){
		double[][] pol = new double[10][6];
		
		double sum = 0;
		for(int i = 0; i < pol.length; i++){
			for(int j = 0; j < pol[0].length; j++){
				sum += pol[i][j];
			}
		}
		
		//System.out.println(sum);
		
		test.randomPolicy(pol);
		
		sum = 0;
		for(int i = 0; i < pol.length; i++){
			for(int j = 0; j < pol[0].length; j++){
				sum += pol[i][j];
				//System.out.println(pol[i][j]);
			}
		}
		//int s = (int) sum;
		//assertEquals(10, s);
		
		boolean result = (sum < 10.1 && sum > 9.9);
		assertEquals(true, result);
	}
	
	@Test
	public void testCombinePolicy(){
		double[][] pol1 = new double[5][4];
		double[][] pol2 = new double[5][4];
		
		for(int i = 0; i < pol1.length; i++){
			double sum1 = 0;
			double sum2 = 0;
			for(int j = 0; j < pol1[0].length; j++){
				pol1[i][j] = Math.random();
				pol2[i][j] = Math.random();
				
				sum1 += pol1[i][j];
				sum2 += pol2[i][j];
			}
			for(int j = 0; j < pol1[0].length; j++){
				pol1[i][j] = pol1[i][j]/sum1;
				pol2[i][j] = pol2[i][j]/sum2;
			}
		}
		
		double[][] newPol = test.combinePolicy(pol1, pol2, 0.3);
		
		double sum = 0;
		for(int i = 0; i < newPol.length; i++){
			for(int j = 0; j < newPol[0].length; j++){
				sum += newPol[i][j];
			}
		}
		
		boolean result = 4.9 < sum && sum < 5.1;
		
		assertEquals(true, result);	
	}
	
	@Test
	public void testGetOptions(){
		
	}
	
	@Test
	public void testArrayCopy(){
		double[][] a = {{2, 3}, {5, 6}, {7,8}};
		
		double[][] copy = test.copyArray(a);
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				assertEquals((int)a[i][j], (int)copy[i][j]);
			}
		}
	}
	
	@Test
	public void testSelectAction(){
		test.setEnv(new Environment(11), 0, 0);
		
		double [][] pol = test.getSPolicy();
		
		System.out.println("policy");
		for(int i = 0; i < pol[0].length; i++){
			System.out.println(pol[0][i]);
		}
		System.out.println("------");
		
		String a;
		int percentUp = 0;
		int percentDown = 0;
		int percentLeft = 0;
		int percentRight = 0;
		for(int i = 0; i < 100; i++){
			a = test.selectAction(0);
			//System.out.println(a);
			if(a.equals("up")){
				percentUp++;
			}
			else if(a.equals("down")){
				percentDown++;
			}
			else if(a.equals("left")){
				percentLeft++;
			}
			else if(a.equals("right")){
				percentRight++;
			}
		}
		System.out.println(percentUp + ", " + percentDown + ", " + percentLeft + ", "
				+ percentRight);
		
	}
}
