package com.challenges.vehicle_survey.analysis;

import java.util.IntSummaryStatistics;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleSpeedCommand<T extends VehiclePassRecord> extends FilterableCommand<T> {

	public VehicleSpeedCommand(Predicate<T> filter) {
		super(filter);
	}

	public VehicleSpeedCommand() {
		super(null);
	}

	@Override
	protected AnalysisResultEntry doExecute(Stream<T> vehiclePassRecords) {
		
		IntSummaryStatistics summaryStatistics = vehiclePassRecords.mapToInt(p -> p.getVehicleSpeed()).summaryStatistics();
		return new AnalysisResultEntry( this.formatDistance(summaryStatistics.getAverage()) );
	}
}
