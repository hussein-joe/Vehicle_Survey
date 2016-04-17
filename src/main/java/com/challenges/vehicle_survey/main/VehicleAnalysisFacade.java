package com.challenges.vehicle_survey.main;

import java.nio.file.Path;

import com.challenges.vehicle_survey.analysis.AnalysisEngine;
import com.challenges.vehicle_survey.analysis.AnalysisRequest;
import com.challenges.vehicle_survey.analysis.AnalysisResult;
import com.challenges.vehicle_survey.analysis.AnalysisSummaryGenerator;
import com.challenges.vehicle_survey.analysis.VehicleAnalysisCommandFactory;
import com.challenges.vehicle_survey.data_reader.InvalidRecordHandler;
import com.challenges.vehicle_survey.data_reader.SensorDataReader;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.data_reader.SensorDataReaderFactory;
import com.challenges.vehicle_survey.processor.MemoryVehiclePassCollector;
import com.challenges.vehicle_survey.processor.SensorDataProcessor;
import com.challenges.vehicle_survey.processor.VehiclePassCollector;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

public class VehicleAnalysisFacade {

	private SensorDataReaderFactory<SensorDataRecord> sensorDataReaderFactory;
	private SensorDataProcessor<SensorDataRecord, VehiclePassRecord> sensorDataProcessor;
	private AnalysisEngine<VehiclePassRecord> analysisEngine;
	private VehiclePassCollector<VehiclePassRecord> analysisDataCollector;
	
	public VehicleAnalysisFacade(Path filePath) {
		
		analysisDataCollector = new MemoryVehiclePassCollector<VehiclePassRecord>();
		VehiclePassRecordBuilder passRecordBuilder = new VehiclePassRecordBuilder();
		
		this.sensorDataReaderFactory = new SensorDataReaderFactory<>(new InvalidRecordHandler<>());
		this.sensorDataProcessor = new SensorDataProcessor<>(analysisDataCollector, passRecordBuilder);
		this.analysisEngine = new AnalysisEngine<>(new VehicleAnalysisCommandFactory<>(), new AnalysisSummaryGenerator());
		
		this.startReading(filePath);
	}
	
	private void startReading(Path filePath) {
		
		SensorDataReader<SensorDataRecord> sensorDataReader = sensorDataReaderFactory.getFileSensorDataReader(filePath);
		sensorDataProcessor.processSensorData(sensorDataReader);
	}
	
	public AnalysisResult<?> runAnalysis(AnalysisRequest request) {
		
		return analysisEngine.runAnalysisReport(analysisDataCollector.stream(), request);
	}
}
