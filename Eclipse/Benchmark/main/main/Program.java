package main;

import java.io.IOException;

import br.gpca.hanafuda.external.CSVModule;
import br.gpca.hanafuda.kernel.Enums.PlayerTypes;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.GlobalGameStatistics;


public class Program {

	/**
	 * @param args
	 */
	
	static int numberOfGames = 1;
	public static void main(String[] args) {
		PlayerTypes playerType1 = PlayerTypes.RandomPlayer;
		PlayerTypes playerType2 = PlayerTypes.ExpectiMinimaxPlayer;
		
		if (args != null && args.length == 2){
			if (args[0].equals("random"))
				playerType1 = PlayerTypes.RandomPlayer;
			else if (args[0].equals("greedy"))
				playerType1 = PlayerTypes.Greedy;
			else if (args[0].equals("expectiminimax"))
				playerType1 = PlayerTypes.ExpectiMinimaxPlayer;
		
			if (args[1].equals("random"))
				playerType2 = PlayerTypes.RandomPlayer;
			else if (args[1].equals("greedy"))
				playerType2 = PlayerTypes.Greedy;
			else if (args[1].equals("expectiminimax"))
				playerType2 = PlayerTypes.ExpectiMinimaxPlayer;
		}
		GlobalGameStatistics globalStatistics = new GlobalGameStatistics();
		//System.out.print("\nJogo (" + n + ")" );
		
		
		
		
		long tStart = System.currentTimeMillis();
		
		for (int n = 0; n < numberOfGames; n++){
		    GameController game = new GameController();
		    game.newGame(playerType1, playerType2);
		    globalStatistics.addStatistic(game.start());
		}
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		
		System.out.print("\n\nVitorias do Player(0): " + globalStatistics.getTotalPlayerWins(0) + "/" + numberOfGames);
		System.out.println(" (" + globalStatistics.getPlayerWinsPercentage(0) + ")");
		System.out.print("Vitorias do Player(1): " + globalStatistics.getTotalPlayerWins(1) + "/" + numberOfGames);
		System.out.println(" (" + globalStatistics.getPlayerWinsPercentage(1) + ")");
		System.out.print("Empates: " + globalStatistics.getDraws());
		System.out.println(" (" + globalStatistics.getDrawsPercentage() + ")");
		System.out.println("__________________________________________________");
		System.out.println("Diferença de score player(0): " + globalStatistics.getMeanScoreDifference(0));
		System.out.println("Diferença de score player(1): " + globalStatistics.getMeanScoreDifference(1));
		System.out.println("__________________________________________________");
		System.out.println("Troca média de turnos por partida: " + globalStatistics.getMeanTurnChanges());
		System.out.println("Tempo total: " + elapsedSeconds);
		for (int i = 1; i < 6; i++){
			System.out.println("Estado: " + i + " " + globalStatistics.getMeanStateCount(i));
		}
		
		/*try {
			CSVModule.ExportGlobalStatisticsToCsv(globalStatistics, playerType1.toString(), playerType2.toString());
		} catch (IOException exception){
			
		}*/
		
	}
}