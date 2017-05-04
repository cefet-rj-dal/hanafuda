package br.gpca.hanafuda.players;

import java.util.ArrayList;
import java.util.Random;

import br.gpca.hanafuda.kernel.Card;
import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.Player;
import br.gpca.hanafuda.kernel.Table;

public class RandomPlayer extends Player {

    public RandomPlayer(GameController game) {
        super(game);
    }

    @Override
    public void move(Table table, Player otherPlayer) {

        Random rand = new Random();
        // parte 1 - combinar uma carta da m�o com uma carta da mesa.
        Card tableCard, handCard;

        boolean handMatched = false;
        int handSize = hand.getCardsSize(), tableSize = table.getCardsSize();
        int randomCard;
        ArrayList<Card> arrayHandCard = new ArrayList<Card>();
        ArrayList<Card> arrayTableCard = new ArrayList<Card>();
        ArrayList<Card> arrayDeckCard = new ArrayList<Card>();

        //Verifica se existe combina��o poss�vel
        /*Se a primeira escolha aleatoria so for da m�o
         * retira o primeiro for e ao inves do valor i coloca o valor random das cartas da m�o no get da m�o
         * retire tbm o array HandCard
         * */
        for (int i = 0; (i < handSize) && (!handMatched); i++) {
            handCard = hand.getCardByIndex(i);
            for (int j = 0; (j < tableSize) && (!handMatched); j++) {
                tableCard = table.getCardByIndex(j);
                if (game.canMatch(handCard, tableCard)) {
                    handMatched = true;

                    arrayHandCard.add(handCard);
                    arrayTableCard.add(tableCard);

                }
            }
        }


        //Se n�o existe combina��o poss�vel, seleciona aleatoriamente uma carta da m�o para ser descartada na mesa

        if (!handMatched) {
            randomCard = rand.nextInt(handSize);
            Card card = hand.getCardByIndex(randomCard);
            game.leaveOnTable(this, card);
        } else {
            //Se existe combina��o poss�vel, escolhe aleatoriamente uma das combina��es possiveis
            /*O random vai ser do array TableCard e o valor de random ser� colocado no get do arrayTableCard
        	 * */
            randomCard = rand.nextInt(arrayHandCard.size());
            handCard = arrayHandCard.get(randomCard);
            tableCard = arrayTableCard.get(randomCard);
            earnCards(handCard, tableCard);
            arrayHandCard.clear();
            arrayTableCard.clear();
        }


        // parte 2 - comprar do monte e combinar com uma carta da mesa.

        boolean deckMatched = false;
        Card deckCard = null;

        if (game.buyFromDeck() == ErrorList.Success) {
            deckCard = game.getDeckCard(); // comprar do monte
        }

        tableSize = table.getCardsSize();

        for (int i = 0; i < tableSize; i++) {
            Card card = table.getCardByIndex(i);

            if (game.canMatch(deckCard, card)) {
                deckMatched = true;
                arrayDeckCard.add(card);
            }
        }


        // se n�o houve combina��o, deixar a carta na mesa

        if (!deckMatched) {
            game.leaveOnTable(this, deckCard);
        } else {
            //Se existe combina��o poss�vel, escolhe aleatoriamente uma das combina��es possiveis
            randomCard = rand.nextInt(arrayDeckCard.size());
            Card cardTable = arrayDeckCard.get(randomCard);
            earnCards(deckCard, cardTable);
            arrayDeckCard.clear();
        }
    }

}
