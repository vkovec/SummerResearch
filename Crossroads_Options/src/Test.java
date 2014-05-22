public class Test{
	
	private static double n = 10;
	
	private static double[] probs = {0.2, 0.2, 0.3, 0.1, 0.2};
	
	//trying something out with logs
	public static double logTest(){
		
		//log of Z
		double log0 = Math.log(probs[0]) + n;
		double logZ = 1;
		for(int i = 1; i < probs.length; i ++){
			logZ += Math.exp((Math.log(probs[i]) + n) - log0);
		}
		logZ = Math.log(logZ);
		logZ += log0;
		
		double num = Math.log(probs[1]) + n;
		
		return num - logZ;
	}
	
	public static void main(String[] args){
		double Z = 0;
		for(int i = 0; i < probs.length; i++){
			Z += probs[i]*Math.exp(n);
		}
		
		
		//System.out.println(Math.exp(n));
		System.out.println(probs[1]*Math.exp(n)/Z);
		
		double test = logTest();
		
		System.out.println(Math.exp(test));
		
	}

}