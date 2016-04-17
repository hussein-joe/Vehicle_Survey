package com.challenges.vehicle_survey.analysis;

import java.math.BigDecimal;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public interface VehicleAnalysisCommand<O extends VehiclePassRecord> {

	AnalysisResultEntry execute(Stream<O> vehiclePassRecords);
	
	public default double formatDistance(double averageDistance) {
		
		BigDecimal value = new BigDecimal(averageDistance);
		return value.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
