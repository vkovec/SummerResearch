package tools;

import java.util.Random;

public class Test{
	
	private static int[] q = {1,2,3,4,5};
	private static int[] qPrev = new int[5];
	
	public static int[] copyArray(int[] a){
		int[] copy = new int[a.length];
		
		for(int i = 0; i < a.length; i++){
			copy[i] = a[i];
		}
		return copy;
	}
	
	public static void test(){
		Random rand = new Random();
		
		int[] iniSet = new int[9];
		
		int goal = rand.nextInt(10);
		System.out.println("goal: " + goal);
		
		int k = 0;
		for(int i = 0; i < iniSet.length; i++){
			iniSet[i] = k;
			if(k == goal){
				i--;
			}
			k++;
		}
		
		for(int i = 0; i < iniSet.length; i++){
			System.out.println(iniSet[i]);
		}
	}
	
	public static void main(String[] args){
	/*	qPrev = copyArray(q);
		
		q[1] = 3;
		
		for(int i = 0; i < q.length; i++){
			System.out.print(q[i] + ", ");
		}
		System.out.println();
		for(int i = 0; i < q.length; i++){
			System.out.print(qPrev[i] + ", ");
		}*/
		
		test();
	}
}