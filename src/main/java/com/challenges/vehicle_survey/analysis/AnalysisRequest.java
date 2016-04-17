package com.challenges.vehicle_survey.analysis;

import com.challenges.vehicle_survey.processor.RoadDirection;

public class AnalysisRequest {

	private AnalysisFeature feature;
	private int minutes;
	private int readingDay = -1;
	private RoadDirection direction;

	public AnalysisRequest(AnalysisFeature feature, RoadDirection direction,
			int minutes, int readingDay) {
		super();
		this.feature = feature;
		this.direction = direction;
		this.minutes = minutes;
		this.readingDay = readingDay;
	}
	public AnalysisRequest(AnalysisFeature feature, RoadDirection direction,
			int minutes) {
		super();
		this.feature = feature;
		this.direction = direction;
		this.minutes = minutes;
	}



	public AnalysisFeature getFeature() {
		return feature;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getReadingDay() {
		return readingDay;
	}
	
	public RoadDirection getDirection() {
		return direction;
	}
	public static enum AnalysisFeature {
		
		COUNT,
		SPEED,
		DISTANCE;
	}
}
