package com.challenges.vehicle_survey.analysis;

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleDistancesCommand<T extends VehiclePassRecord> extends FilterableCommand<T> {

	public VehicleDistancesCommand(Predicate<T> filter) {
		
		super(filter);
	}

	public VehicleDistancesCommand() {
		
		this(null);
	}

	@Override
	protected AnalysisResultEntry doExecute(Stream<T> vehiclePassRecords) {
		
		List<T> vehiclePassRecordList = vehiclePassRecords.collect( Collectors.toList() );
		return new AnalysisResultEntry( this.formatDistance(this.calculateAverageDistance(vehiclePassRecordList)) );
	}
	
	private double calculateAverageDistance(List<T> vehiclePassRecordList) {

		if ( vehiclePassRecordList.size() < 2 ) {
			
			return 0;
		}
		
		T firstVehiclePassRecord = vehiclePassRecordList.get(0);
		T lastVehiclePassRecord = vehiclePassRecordList.get( vehiclePassRecordList.size() - 1 );
		int countVehicles = vehiclePassRecordList.size();
		
		long duration = this.calculateVehiclePassDuration(firstVehiclePassRecord, lastVehiclePassRecord);
		double averageDuration = duration / Double.valueOf(countVehicles);
		return (averageDuration/(3600000.0)) * (60 * 1000);
	}
	
	private long calculateVehiclePassDuration(T firstVehiclePass, T lastVehiclePass) {
		
		long duration = lastVehiclePass.getPassTimeStart().getLong(ChronoField.NANO_OF_DAY) - firstVehiclePass.getPassTimeStart().getLong(ChronoField.NANO_OF_DAY);
		return duration / 1000000;
	}
	
	/*private static class VehicleDistanceCalculator<T extends VehiclePassRecord> {
		
		private T vehicle1PassRecord;
		
		public float calculateDistance(T vehiclePassRecord) {
			
			if ( vehicle1PassRecord == null ) {
				
				return -1;
			}
			
			vehicle1PassRecord.get
		}
	}*/
}
