package com.challenges.vehicle_survey.common;

public class VehicleCommons {

	public static int calculateSpeed(long passStartTime, long passEndTime) {
		
		return calculateSpeed(passStartTime, passEndTime, 2.5f);
	}
	public static int calculateSpeed(long passStartTime, long passEndTime, float distanceInMeter) {
		
		if ( passStartTime < 0 || passEndTime < 0 || distanceInMeter < 0 ) {
			
			throw new RuntimeException("The passed parameters are not valid");
		}
		long duration = Math.abs(passEndTime - passStartTime);
		
		return Double.valueOf(Math.floor( (distanceInMeter * 3600000) / (duration * 1000) )).intValue();
	}
	

	public static long minutesToNanoSecond(int minutes) {
		
		return minutes * 60 * 1000000000;
	}
}
