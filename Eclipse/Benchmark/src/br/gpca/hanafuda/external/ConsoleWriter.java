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
		System.out.print("Cartas da m�o: ");
        game.getCurrentPlayer().hand.printCards();
	}
	
	public void printEndTurnMessages()
	{
		System.out.print("\nCartas adquiridas: ");
    	game.getCurrentPlayer().earnedCards.printCards();
    	
    	System.out.println("\nPontua��o: " + game.getCurrentPlayer().getScore());
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
			System.out.println("\nCombina��o escolhida: " + action.getCard1().getID() + "|" + action.getCard2().getID());
	}

	public void printActionMessages(){		
		switch (game.getCurrentState())
		{
			case MatchHandTable:
				System.out.print("\nA��o: Combinar m�o com mesa: ");
				System.out.print("\nEntre com a carta da m�o e da mesa: ");
				break;
			case BuyFromDeck:
				System.out.print("\nA��o: Carta comprada do Monte: ");
				break;
			case MatchDeckTable:
				System.out.print("\nA��o: Combinar monte com mesa: ");
				System.out.print("\nEntre com a carta da mesa: ");
				break;
			case DiscardHandCard:
				System.out.print("\nA��o: Descartar carta da m�o: ");
				System.out.print("\nEntre com a carta da m�o: ");
				break;
			case DiscardDeckCard:
				System.out.print("\nA��o: Descartar carta do monte: ");
				break;
			case NotDefined:
				System.out.print("\nErro! Estado do jogo n�o definido. Saindo...");
				return;
		}
		
	}*/

}
