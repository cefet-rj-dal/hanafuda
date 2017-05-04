package br.gpca.hanafuda.kernel;

import java.util.ArrayList;
import java.util.List;

public class GlobalGameStatistics {
	private int turnChangesCount = 0;
	private int[] totalPlayerWins = new int[2];
	private int[] totalStatesCount = new int[6];
	private int[] playerWinsScoreDiff = new int[2];
	private int totalGames = 0;
	private int totalDraws = 0;
	
	public List<GameStatistics> gameStatistics = new ArrayList<GameStatistics>();
	
	public void addStatistic(GameStatistics gs){
		//Se empatar
		if (gs.getWinner() == -1)
			totalDraws++;
		else {
			totalPlayerWins[gs.getWinner()]++;
			playerWinsScoreDiff[gs.getWinner()] += gs.getScoreDiference();
		}
		
		for (int i = 0; i < 6; i++)
			this.totalStatesCount[i]+= gs.getStateCount(i);
		
		turnChangesCount+=gs.getTurns();
		gameStatistics.add(gs);
		totalGames++;
	}
	
	public int getTotalPlayerWins(int playerIndex) {
		return totalPlayerWins[playerIndex];
	}
	
	public int getDraws(){
		return totalDraws;
	}
	
	public int getTurnChangesCount(){
		return turnChangesCount;
	}
	
	public int getTotalGames(){
		return totalGames;
	}
	
	public int getScoreDifference(int playerIndex){
		return playerWinsScoreDiff[playerIndex];
	}
	
	public float getMeanScoreDifference(int playerIndex){
		return (float) playerWinsScoreDiff[playerIndex] / totalPlayerWins[0];
	}
	
	public float getPlayerWinsPercentage(int playerIndex){
		if (totalGames == 0) return 0;
		return (float) totalPlayerWins[playerIndex] / (totalGames - totalDraws);  
	}
	
	public float getMeanTurnChanges(){
		return (float) turnChangesCount /  totalGames;
	}
	
	public float getMeanStateCount(int stateIndex){
		return  ((float)totalStatesCount[stateIndex] / totalGames); 	
	}
	
	public float getDrawsPercentage(){
		return (float) totalDraws / totalGames;
	}
}
