package com.filetool.MyArima;

public class TripleExponentialImpl {

	public static double[] forecast(double[] y, double alpha, double beta,
			double gamma, int period, int m, boolean debug) {

		if (y == null) {
			return null;
		}

		int seasons = y.length / period;
		double a0 = calculateInitialLevel(y, period);
		double b0 = calculateInitialTrend(y, period);
		double[] initialSeasonalIndices = calculateSeasonalIndices(y, period, seasons);

		if (debug) {
			System.out.println(String.format(
					"Total observations: %d, Seasons %d, Periods %d", y.length,
					seasons, period));
			System.out.println("Initial level value a0: " + a0);
			System.out.println("Initial trend value b0: " + b0);
			printArray("Seasonal Indices: ", initialSeasonalIndices);
		}

		double[] forecast = calculateHoltWinters(y, a0, b0, alpha, beta, gamma,
				initialSeasonalIndices, period, m, debug);

//		if (debug) {
//			printArray("Forecast", forecast);
//		}

		return forecast;
	}
	
	/**
	 * This method realizes the Holt-Winters equations.
	 * 
	 * @param y
	 * @param a0
	 * @param b0
	 * @param alpha
	 * @param beta
	 * @param gamma
	 * @param initialSeasonalIndices
	 * @param period
	 * @param m
	 * @param debug
	 * @return - Forecast for m periods.
	 */
	private static double[] calculateHoltWinters(double[] y, double a0, double b0, double alpha,
			double beta, double gamma, double[] initialSeasonalIndices, int period, int m, boolean debug) {
		
		double[] St = new double[y.length];
		double[] Bt = new double[y.length];
		double[] It = new double[y.length];
		double[] Ft = new double[y.length + m];
		
		//Initialize base values
		St[1] = a0;
		Bt[1] = b0;
		   
		for (int i = 0; i < period; i++) {
			It[i] = initialSeasonalIndices[i];
			System.out.println("It[i]"+It[i]);
		}
		
		Ft[m] = (St[0] + (m * Bt[0])) * It[0];//This is actually 0 since Bt[0] = 0
		Ft[m + 1] = (St[1] + (m * Bt[1])) * It[1];//Forecast starts from period + 2
		
		//Start calculations
		for (int i = 2; i < y.length; i++) {

			//Calculate overall smoothing
			if((i - period) >= 0) {
				St[i] = alpha * y[i] / It[i - period] + (1.0 - alpha) * (St[i - 1] + Bt[i - 1]);
			} else {
				St[i] = alpha * y[i] + (1.0 - alpha) * (St[i - 1] + Bt[i - 1]);
			}
			
			//Calculate trend smoothing
	        Bt[i] = gamma * (St[i] - St[i - 1]) + (1 - gamma) * Bt[i - 1];
	        
	        //Calculate seasonal smoothing
	        if((i - period) >= 0) {
	        	It[i] = beta * y[i] / St[i] + (1.0 - beta) * It[i - period];
	        }
	                                                      
            //Calculate forecast
	        if( (i - period+1+ (m-1) % period)>=0 ){
	        	Ft[i + m] = (St[i] + (m * Bt[i])) * It[i - period+1+ (m-1) % period];
	        }
	        
	        if(debug){
				System.out.println(String.format(
						"i = %d, y = %f, S = %f, Bt = %f, It = %f, F = %f", i,
						y[i], St[i], Bt[i], It[i], Ft[i]));
	        }
		}
		
		return Ft;
	}

	/**
	 * See: http://robjhyndman.com/researchtips/hw-initialization/
	 * 1st period's average can be taken. But y[0] works better.
	 * 
	 * @return - Initial Level value i.e. St[1]
	 */
	private static double calculateInitialLevel(double[] y, int period) {


 		double sum = 0;

		for (int i = 0; i < period; i++) {
			sum += y[i];
		}

		if(sum==0){
			return 1;
		}
		return sum / period;

//		return y[0];
	}
	
	/**
	 * See: http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc435.htm
	 * 
	 * @return - Initial trend - Bt[1]
	 */
	private static double calculateInitialTrend(double[] y, int period){
		
		double sum = 0;
		
		for (int i = 0; i < period; i++) {			
			sum += (y[period + i] - y[i]);
		}
		
		return sum / (period * period);
	}
	
	/**
	 * See: http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc435.htm
	 * 
	 * @return - Seasonal Indices.
	 */
	private static double[] calculateSeasonalIndices(double[] y, int period, int seasons){
						
		double[] seasonalAverage = new double[seasons];
		double[] seasonalIndices = new double[period];
		
		double[] averagedObservations = new double[y.length];
		
		for (int i = 0; i < seasons; i++) {
			for (int j = 0; j < period; j++) {
				seasonalAverage[i] += y[(i * period) + j];
			}
			seasonalAverage[i] /= period;
		}
		
		for (int i = 0; i < seasons; i++) {
			for (int j = 0; j < period; j++) {
				if(seasonalAverage[i]==0){
					averagedObservations[(i * period) + j]=1;
				}else{
					averagedObservations[(i * period) + j] = y[(i * period) + j] / seasonalAverage[i];
				}
			}			
		}
		
		for (int i = 0; i < period; i++) {
			for (int j = 0; j < seasons; j++) {
				seasonalIndices[i] += averagedObservations[(j * period) + i];
			}			
			seasonalIndices[i] /= seasons;
			if(seasonalIndices[i]==0){
				seasonalIndices[i]=0.1;
			}
		}
//		seasonalIndices=new double[]{1,1,1,1,1,1,1};
		return seasonalIndices;
	}
	
	/**
	 * Utility method to pring array values.
	 * 
	 * @param description
	 * @param data
	 */
	private static void printArray(String description, double[] data){
		
		System.out.println(String.format("******************* %s *********************", description));
		
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
		
		System.out.println(String.format("*****************************************************************", description));
	}
}
