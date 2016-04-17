package com.challenges.vehicle_survey.analysis;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

abstract class FilterableCommand<T extends VehiclePassRecord> implements VehicleAnalysisCommand<T>{
	
	protected Predicate<T> filter;
	
	protected FilterableCommand(Predicate<T> filter) {
		
		super();
		this.filter = filter;
	}

	@Override
	public final AnalysisResultEntry execute(Stream<T> vehiclePassRecords) {
		
		this.validateRecords(vehiclePassRecords);
		return this.doExecute( this.applyFilter(vehiclePassRecords) );
	}
	
	protected abstract AnalysisResultEntry doExecute(Stream<T> vehiclePassRecords);
	
	protected boolean validateRecords(Stream<T> vehiclePassRecords) {
		
		Objects.requireNonNull( vehiclePassRecords, "The passed stream cannot be null");
		return true;
	}
	
	protected Stream<T> applyFilter(Stream<T> vehiclePassRecords) {
		
		if ( this.filter != null ) {
			
			return vehiclePassRecords.filter(filter);
		}
		return vehiclePassRecords;
	}
}
