package com.challenges.vehicle_survey.processor;

import java.util.stream.Stream;

public abstract class VehiclePassCollector<T extends VehiclePassRecord> {

	public void accept(T t) {
	
		if ( t == null ) {
			
			throw new NullPointerException("The passed element cannot be null");
		}
		
		this.addRecord(t);
	}
	
	public abstract Stream<T> stream();
	
	protected abstract void addRecord(T t);
}
