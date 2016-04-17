package com.challenges.vehicle_survey.analysis;

import java.util.Objects;
import java.util.function.Predicate;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleAnalysisCommandFactory<T extends VehiclePassRecord> {

	public VehicleAnalysisCommand<T> createCommand(AnalysisRequest analysisRequest) {
		
		Objects.requireNonNull( analysisRequest );
		if ( !this.validateRequest(analysisRequest) ) {
			
			throw new RuntimeException("The passed request is not valid");
		}
		
		VehicleAnalysisCommand<T> command = null;
		Predicate<T> filter = this.generateCommandFilter(analysisRequest);
		switch ( analysisRequest.getFeature() ) {
		case COUNT:
			command = new CountVehiclesCommand<>( filter );
			break;
		case DISTANCE:
			command = new VehicleDistancesCommand<>( filter );
			break;
		case SPEED:
			command = new VehicleSpeedCommand<>( filter );
			break;
		default:
			throw new RuntimeException("The passed feature " + analysisRequest.getFeature() + " is not supported");
		}
		
		return command;
	}

	private boolean validateRequest(AnalysisRequest request) {
		
		return Objects.nonNull( request.getDirection() ) && Objects.nonNull( request.getFeature() ) && request.getMinutes() > 0;
	}
	
	public Predicate<T> generateCommandFilter(AnalysisRequest request) {
		
		Predicate<T> filter = (p) -> p.getDirection() == request.getDirection();
		if ( request.getReadingDay() > 0 ) {
			
			filter = filter.and( (p) -> p.getReadingDay() == request.getReadingDay() );
		}
		return filter;
	}
}
