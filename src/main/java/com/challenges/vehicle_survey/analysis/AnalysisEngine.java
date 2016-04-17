package com.challenges.vehicle_survey.analysis;

import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class AnalysisEngine<T extends VehiclePassRecord> {
	
	private VehicleAnalysisCommandFactory<T> commandFactory;
	private AnalysisSummaryGenerator analysisSummaryGenerator;
	
	public AnalysisEngine(VehicleAnalysisCommandFactory<T> commandFactory, AnalysisSummaryGenerator analysisSummaryGenerator) {
		super();
		this.commandFactory = commandFactory;
		this.analysisSummaryGenerator = analysisSummaryGenerator;
	}

	public MapAnalysisResult<AnalysisResultEntry> runAnalysisReport(Stream<T> dataStream, AnalysisRequest request) {
		
		Objects.requireNonNull( dataStream );
		Objects.requireNonNull( request );
		
		VehicleAnalysisCommand<T> analysisCommand = this.commandFactory.createCommand(request);
		if ( analysisCommand == null ) {
			
			throw new RuntimeException("Failed to create analysis command using the passed request");
		}
		Map<Long, List<T>> map = dataStream.collect( Collectors.groupingBy( p -> this.calculateScaleKey(request, (T) p) ) );
		Map<Long, AnalysisResultEntry> analysisResultMap = new HashMap<>();
		for(Map.Entry<Long, List<T>> entry: map.entrySet()) {
			
			AnalysisResultEntry analysisResultEntry = analysisCommand.execute( entry.getValue().stream() );
			analysisResultMap.put(entry.getKey(),  analysisResultEntry);
		}
		
		return new MapAnalysisResult<>(this.generateAnalysisResultTitle(request), analysisResultMap, 
				this.analysisSummaryGenerator.summaryStatistics( analysisResultMap ));
	}
	
	private Long calculateScaleKey(AnalysisRequest request, T entry) {
		
		return (entry.getPassTimeStart().getLong(ChronoField.NANO_OF_DAY) / (request.getMinutes()*60*1000*1000000L) );
	}
	
	private String generateAnalysisResultTitle(AnalysisRequest request) {
		
		StringBuilder resultTitle = new StringBuilder();
		resultTitle.append(request.getFeature()).append(" - ").append( request.getDirection() ).append(" - ")
			.append( request.getMinutes() ).append(" Minutes - ").append( request.getReadingDay() < 0?"All days":request.getReadingDay() + " Day" );
		
		return resultTitle.toString();
	}
}
