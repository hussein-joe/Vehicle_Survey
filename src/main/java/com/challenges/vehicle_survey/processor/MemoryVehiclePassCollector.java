package com.challenges.vehicle_survey.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MemoryVehiclePassCollector<T extends VehiclePassRecord> extends VehiclePassCollector<T>{

	private List<T> collectedDataList = new ArrayList<>();

	@Override
	protected void addRecord(T t) {
		
		this.collectedDataList.add(t);
	}

	@Override
	public Stream<T> stream() {
		
		return this.collectedDataList.stream();
	}
}
