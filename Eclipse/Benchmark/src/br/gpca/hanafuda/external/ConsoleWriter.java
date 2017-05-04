package br.gpca.hanafuda.external;

import br.gpca.hanafuda.kernel.Action;
import br.gpca.hanafuda.kernel.GameController;

public class ConsoleWriter
{
	/*private GameController game;
	
	public ConsoleWriter(GameController game){
		this.game = game;
	}
	
	public void printTurnMessages(){
		System.out.println("\nVez do jogador " + game.getCurrentPlayer().name);
		System.out.print("\nCartas da mesa: ");
        game.getTable().printCards();
		System.out.print("Cartas da mão: ");
        game.getCurrentPlayer().hand.printCards();
	}
	
	public void printEndTurnMessages()
	{
		System.out.print("\nCartas adquiridas: ");
    	game.getCurrentPlayer().earnedCards.printCards();
    	
    	System.out.println("\nPontuação: " + game.getCurrentPlayer().getScore());
    	System.out.println("-------------------------------------------------------");
	}
	
	public void printWinner(){
		
		if (game.getWinner() != null)
		{
			System.out.print("\nVencedor: " + game.getWinner().getName());
		 	System.out.print(" - Score:" + game.getWinner().getScore());
		}
		else
			System.out.print("\nEmpate: " + game.getCurrentPlayer().getScore());
	}
	
	public void printPlayerAction(Action action) {
		if (action.getCard2() == null)
			System.out.println("\nCarta escolhida: " + action.getCard1().getID());
		else
			System.out.println("\nCombinação escolhida: " + action.getCard1().getID() + "|" + action.getCard2().getID());
	}

	public void printActionMessages(){		
		switch (game.getCurrentState())
		{
			case MatchHandTable:
				System.out.print("\nAção: Combinar mão com mesa: ");
				System.out.print("\nEntre com a carta da mão e da mesa: ");
				break;
			case BuyFromDeck:
				System.out.print("\nAção: Carta comprada do Monte: ");
				break;
			case MatchDeckTable:
				System.out.print("\nAção: Combinar monte com mesa: ");
				System.out.print("\nEntre com a carta da mesa: ");
				break;
			case DiscardHandCard:
				System.out.print("\nAção: Descartar carta da mão: ");
				System.out.print("\nEntre com a carta da mão: ");
				break;
			case DiscardDeckCard:
				System.out.print("\nAção: Descartar carta do monte: ");
				break;
			case NotDefined:
				System.out.print("\nErro! Estado do jogo não definido. Saindo...");
				return;
		}
		
	}*/

}
