package tools;

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
	
	public static void main(String[] args){
		qPrev = copyArray(q);
		
		q[1] = 3;
		
		for(int i = 0; i < q.length; i++){
			System.out.print(q[i] + ", ");
		}
		System.out.println();
		for(int i = 0; i < q.length; i++){
			System.out.print(qPrev[i] + ", ");
		}
	}
}