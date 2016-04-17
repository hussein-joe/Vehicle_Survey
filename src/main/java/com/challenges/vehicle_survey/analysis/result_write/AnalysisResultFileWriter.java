package com.challenges.vehicle_survey.analysis.result_write;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.challenges.vehicle_survey.analysis.AnalysisRequest;
import com.challenges.vehicle_survey.analysis.AnalysisResultEntry;
import com.challenges.vehicle_survey.analysis.AnalysisSummary;
import com.challenges.vehicle_survey.analysis.MapAnalysisResult;

public class AnalysisResultFileWriter implements AnalysisResultWriter {

	private FileWriter fileWriter;
	private Path outputFilePath;
	private AnalysisRequest analysisRequest;
	
	public AnalysisResultFileWriter(Path outputFilePath, AnalysisRequest analysisRequest) {
		super();
		this.outputFilePath = outputFilePath;
		this.analysisRequest = analysisRequest;
		this.init();
	}

	private void init() {
		
		try {
			
			this.fileWriter = new FileWriter( this.outputFilePath.toFile(), true );
		} catch(IOException exp) {
			
			throw new RuntimeException(exp);
		}
	}
	
	@Override
	public <V extends AnalysisResultEntry> void write(MapAnalysisResult<V> result) {
		
		try {
			this.fileWriter.write( result.getResultTitle() );
			this.addNewLine();
			//result.getAnalysisResult().forEach( p -> this.writeResultMapEntry(p) );
			for(Map.Entry<Long, V> resultMapEntry: result.getAnalysisResult().entrySet()) {
				
				this.writeResultMapEntry(resultMapEntry);
				this.addNewLine();
			}
			
			this.addNewLine();
			this.writeAnalysisSummary( result.getAnalysisSummary() );
			this.addNewLine();
			this.fileWriter.write("****************************************************************");
			this.addNewLine();
			this.fileWriter.flush();
			
		} catch (IOException e) {
			
			throw new RuntimeException("Failed to write analysis result", e);
		}
	}

	private void addNewLine() throws IOException{
		
		this.fileWriter.write( System.lineSeparator() );
	}
	
	private <V extends AnalysisResultEntry> void writeResultMapEntry(Map.Entry<Long, V> resultMapEntry) throws IOException{
		
		this.fileWriter.write( ( this.getAnalysisResultKey( resultMapEntry.getKey() ) + " : " + 
				resultMapEntry.getValue().getResultEntry() ) );
	}
	
	private <V extends AnalysisResultEntry> void writeAnalysisSummary(AnalysisSummary<V> analysisSummary) throws IOException{
		
		this.fileWriter.write( "Analysis Summary [Max = (" + this.getAnalysisResultKey(analysisSummary.getMax().getRecordKey()) + ":" + 
				analysisSummary.getMax().getRecordValue().getResultEntry() + ") ]" );
		this.fileWriter.write(", [ Min = (" + this.getAnalysisResultKey( analysisSummary.getMin().getRecordKey()) + " : " + 
				analysisSummary.getMin().getRecordValue().getResultEntry() + ") ]");
	}
	
	private String getAnalysisResultKey(long analysisResultKey) {
		
		return String.valueOf(analysisRequest.getMinutes() * analysisResultKey);
	}
}
