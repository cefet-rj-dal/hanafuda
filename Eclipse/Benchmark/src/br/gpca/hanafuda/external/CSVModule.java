package br.gpca.hanafuda.external;

import br.gpca.hanafuda.kernel.GameStatistics;
import br.gpca.hanafuda.kernel.GlobalGameStatistics;
import java.io.IOException;
import java.io.RandomAccessFile;


public class CSVModule {
	
	
	public static void ExportGlobalStatisticsToCsv(GlobalGameStatistics globalGS, String namePlayer1, String namePlayer2) throws IOException{
		RandomAccessFile file = new RandomAccessFile("resultados.csv", "rw");
		
		String ScorePlayer1 = "";
		String ScorePlayer2 = "";
		String csvFile = "";
		
		csvFile = csvFile.concat(namePlayer1);
		csvFile = csvFile.concat(";");
		csvFile = csvFile.concat(namePlayer2);
		csvFile = csvFile.concat("\n");
		
		for (GameStatistics gs : globalGS.gameStatistics){
			ScorePlayer1 = Integer.toString(gs.getPlayerScore(0));
			ScorePlayer2 = Integer.toString(gs.getPlayerScore(1));
			
			csvFile = csvFile.concat(ScorePlayer1);
			csvFile = csvFile.concat(";");
			csvFile = csvFile.concat(ScorePlayer2);
			csvFile = csvFile.concat("\n");
		}
		
		file.writeUTF(csvFile);
		file.close();
	}
	

}

