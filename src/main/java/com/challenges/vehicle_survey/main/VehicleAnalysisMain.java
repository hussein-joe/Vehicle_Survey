package com.challenges.vehicle_survey.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.challenges.vehicle_survey.analysis.AnalysisRequest;
import com.challenges.vehicle_survey.analysis.AnalysisRequest.AnalysisFeature;
import com.challenges.vehicle_survey.analysis.AnalysisResult;
import com.challenges.vehicle_survey.analysis.result_write.AnalysisResultFileWriter;
import com.challenges.vehicle_survey.analysis.result_write.AnalysisResultWriter;
import com.challenges.vehicle_survey.processor.RoadDirection;

public class VehicleAnalysisMain {

	public static void main(String[] args) {

		VehicleAnalysisMain analysisMain = new VehicleAnalysisMain();
		int[] periods = analysisMain.readRequiredPeriodForAnalysis();
		
		VehicleAnalysisFacade analysisFacade = new VehicleAnalysisFacade( analysisMain.getDataFilePath() );
		int readingDay = analysisMain.getReadingDayForAnalysis();
		RoadDirection direction = analysisMain.getRoadDirectionForAnalysis();
		
		for(int periodInMinute: periods) {	
			for(AnalysisFeature feature: AnalysisFeature.values()) {
				
				AnalysisRequest request = new AnalysisRequest(feature, direction, periodInMinute, readingDay);
				AnalysisResult<?> analysisResult = analysisFacade.runAnalysis(request);
				analysisMain.printAnalysisResult(analysisResult, request);
			}
		}
	}

	private void printAnalysisResult(AnalysisResult<?> analysisResult, AnalysisRequest analysisRequest) {
		
		Path analysisResultFilePath = this.getAnalysisResultFilePath();
		AnalysisResultWriter resultWriter = new AnalysisResultFileWriter( analysisResultFilePath,  analysisRequest);
		analysisResult.accept(resultWriter);
	}
	
	public Path getDataFilePath() {
		
		Path dataFilePath = Paths.get("src/main/resources", "data.txt");
		if ( Files.exists(dataFilePath, LinkOption.NOFOLLOW_LINKS) && Files.isReadable(dataFilePath) ) {
			
			return dataFilePath;
		}
		
		throw new RuntimeException("The data file does not exist, please make sure that the file copied to the path " + 
				dataFilePath.toAbsolutePath().toString());
	}
	
	public Path getAnalysisResultFilePath() {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy_HHmm");
		Path analysisResultFilePath = Paths.get("src/main/resources", "analysis_result" + "_" + dateFormatter.format( new Date() ) + ".txt");
		if ( !Files.exists(analysisResultFilePath, LinkOption.NOFOLLOW_LINKS) ) {
			
			try {
				Files.createFile(analysisResultFilePath);
			} catch (IOException e) {
				
				throw new RuntimeException(e);
			}
		}
		return analysisResultFilePath;
	}
	
	public int[] readRequiredPeriodForAnalysis() {
		
		return new int[] {60, 30, 20, 15};
	}
	
	public int getReadingDayForAnalysis() {
		
		return this.readFromConsole("Please enter the required reading day (-1 for all days)", Arrays.asList(-1, 1, 2, 3, 4, 5));
	}
	
	public RoadDirection getRoadDirectionForAnalysis() {
		
		int value = this.readFromConsole("Please enter the required direction(1=Northbound, 2=Southbound)", Arrays.asList(1, 2));
		return RoadDirection.values()[value-1];
	}
	
	
	private int readFromConsole(String message, List<Integer> possibleValues) {
		System.out.println(message);
		try {
			String value = System.console().readLine();
			int userValue = Integer.parseInt(value);
			if ( possibleValues.contains( userValue ) ) {
				return userValue;
			}
		} catch(Exception exp) {}
		System.out.println("The input is not correct");
		return this.getReadingDayForAnalysis();
	}
}
