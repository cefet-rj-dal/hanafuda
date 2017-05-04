package br.gpca.hanafuda.kernel;

public class GameStatistics {
	
	private int turns = 0;
	private int playerScore[] = new int[2];
	private int statesCount[] = new int[6];
	
	public int getTurns() {
		return turns;
	}
	public void incTurnCount() {
		this.turns++;
	}
	public int getPlayerScore(int playerIndex) {
		return playerScore[playerIndex];
	}
	public void setPlayerScore(int playerScore, int playerIndex) {
		this.playerScore[playerIndex] = playerScore;
	}
	public int getStateCount(int stateIndex) {
		return statesCount[stateIndex];
	}
	public void incState(int stateIndex) {
		this.statesCount[stateIndex]++;
	}
	
	public int getWinner(){
		if (playerScore[0] > playerScore[1])
			return 0;
		else if (playerScore[0] < playerScore[1])
			return 1;
		else
			return -1;
	}
	
	public int getLooser(){
		if (playerScore[0] < playerScore[1])
			return 0;
		else if (playerScore[1] < playerScore[0])
			return 1;
		else
			return -1;
	}
	
	public int getScoreDiference(){
		if (getWinner() == 0)
			return playerScore[0] - playerScore[1];
		
		if (getWinner() == 1)
			return playerScore[1] - playerScore[0];
		
		return -1;
	}
}
