package com.challenges.vehicle_survey.analysis;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class CountVehiclesCommand<T extends VehiclePassRecord> extends FilterableCommand<T> {
	
	public CountVehiclesCommand(Predicate<T> filter) {
		super(filter);
	}
	
	public CountVehiclesCommand() {
		this(null);
	}

	@Override
	public AnalysisResultEntry doExecute(Stream<T> vehiclePassRecords) {
		
		return new AnalysisResultEntry( vehiclePassRecords.count() );
		
	}
}
