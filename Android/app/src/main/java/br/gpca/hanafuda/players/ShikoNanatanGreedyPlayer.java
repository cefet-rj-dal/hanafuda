package br.gpca.hanafuda.players;

import br.gpca.hanafuda.kernel.Card;
import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.Player;
import br.gpca.hanafuda.kernel.Table;

public class ShikoNanatanGreedyPlayer extends Player {

    public ShikoNanatanGreedyPlayer(GameController game) {
        super(game);
    }

    @Override
    public void move(Table table, Player otherPlayer) {
        // se houve combina��o, jogar as melhores cartas
        //parte 1 - combinar uma carta da m�o com uma carta da mesa.

        Card bestHandCard = null, bestTableCard = null;

        boolean handMatched = false, deckMatched = false;
        boolean thereIsAShikoPossibility = false, thereIsANanatanPossibility = false, isShikoFamilyCard = false, isNanatanFamilyCard = false;
        int bestScore = -1, handSize = hand.getCardsSize(), tableSize = table.getCardsSize();

        for (int i = 0; i < handSize; i++) { // para carta na mão
            Card handCard = hand.getCardByIndex(i); // get carta da mao
            System.out.print("familia: "+handCard.getFamily());
            System.out.print("id: "+handCard.getID());
            System.out.print("ken: "+handCard.getKen());
            for (int j = 0; j < tableSize; j++) { // para cada carta na mesa
                Card tableCard = table.getCardByIndex(j); // get carta da mesa

                if (game.canMatch(handCard, tableCard)) // se combinar,
                {
                    int tempScore;

                    earnCards(handCard, tableCard); // adicionar a carta da m�o e da mesa nas cartas adquiridas
                    handMatched = true; // marcar que foi feita uma combina��o

                    tempScore = getScore();
                    if (tempScore >= GameController.MAX_GAME_SCORE) // se acabar o jogo,
                        return; // essa � a melhor combina��o, terminar a jogada

                    if(Card.isAShikoPossibility(handCard) || Card.isAShikoPossibility(tableCard)){

                        if(!(Card.isTheBogeyman(handCard))){
                            isShikoFamilyCard = true;
                        } else {
                            isShikoFamilyCard = false;
                        }

                        if(thereIsAShikoPossibility && isShikoFamilyCard) {
                            if(tempScore > bestScore) {
                                bestHandCard = handCard;
                                bestTableCard = tableCard;
                                bestScore = tempScore;
                            } else if(Card.isTheBogeyman(bestHandCard)){
                                bestHandCard = handCard;
                                bestTableCard = tableCard;
                                bestScore = tempScore;
                            }
                        } else if(!thereIsAShikoPossibility){
                            bestHandCard = handCard;
                            bestTableCard = tableCard;
                            bestScore = tempScore;
                        }
                        thereIsAShikoPossibility = true;
                    } else if((Card.isANanatanPossibilty(handCard) || Card.isANanatanPossibilty(tableCard)) && !thereIsAShikoPossibility){

                        if (!(Card.isTheBogeyman(handCard))) {
                            isNanatanFamilyCard = true;
                        } else {
                            isNanatanFamilyCard  =false;
                        }

                        if(thereIsANanatanPossibility && isNanatanFamilyCard) {
                            if(tempScore > bestScore) {
                                bestHandCard = handCard;
                                bestTableCard = tableCard;
                                bestScore = tempScore;
                            } else if(Card.isTheBogeyman(bestHandCard)){
                                bestHandCard = handCard;
                                bestTableCard = tableCard;
                                bestScore = tempScore;
                            }
                        } else if(!thereIsANanatanPossibility){
                            bestHandCard = handCard;
                            bestTableCard = tableCard;
                            bestScore = tempScore;
                        }
                        thereIsANanatanPossibility = true;
                    }
                    else if (tempScore > bestScore && !thereIsAShikoPossibility && !thereIsANanatanPossibility) // se for a melhor combina��o at� agora
                    {
                        bestHandCard = handCard; // salvar carta da m�o
                        System.out.println("Melhor carta da mao: "+bestHandCard);
                        bestTableCard = tableCard; // salvar carta da mesa
                        System.out.println("Melhor carta da mesa: "+bestTableCard);
                        bestScore = tempScore; // salvar melhor pontua��o
                        System.out.println("Melhor pontuação: "+bestScore);
                    }

                    // desfazer a jogada

                    hand.insertCard(i, earnedCards.removeCard(handCard));
                    table.insertCard(j, earnedCards.removeCard(tableCard));
                }
            }
        }

        if (handMatched) // se houve combina��o, jogar as melhores cartas
        {
            earnCards(bestHandCard, bestTableCard);
        } else {
            int lowerImpact = 9999, tempScore1, tempScore2, tempImpact, newTempScore1, newTempScore2;
            bestHandCard = null;

            tempScore1 = getScore();
            tempScore2 = otherPlayer.getScore();

            for (int i = 0; i < handSize; i++) {
                Card handCard = hand.getCardByIndex(i);

                earnedCards.addCard(handCard);
                newTempScore1 = getScore();
                if (newTempScore1 >= GameController.MAX_GAME_SCORE) tempScore1 = 9998;

                otherPlayer.earnedCards.addCard(handCard);
                newTempScore2 = otherPlayer.getScore();
                if (newTempScore2 >= GameController.MAX_GAME_SCORE) tempScore2 = 9998;

                tempImpact = (newTempScore1 - tempScore1) + (newTempScore2 - tempScore2);

                if (tempImpact < lowerImpact) {
                    lowerImpact = tempImpact;
                    bestHandCard = handCard;
                }

                //Desfaz a jogada

                earnedCards.removeCard(handCard);
                otherPlayer.earnedCards.removeCard(handCard);
            }

            game.leaveOnTable(this, bestHandCard);
        }

        bestScore = -1;
        bestTableCard = null;
        thereIsAShikoPossibility = false;
        thereIsANanatanPossibility = false;
        

        // parte 2 - comprar do monte e combinar com uma carta da mesa.
        Card deckCard = null;

        if (game.buyFromDeck() == ErrorList.Success) {
            deckCard = game.getDeckCard(); // comprar do monte
        }

        tableSize = table.getCardsSize();

        for (int i = 0; i < tableSize; i++) {
            Card tableCard = table.getCardByIndex(i);
            if (game.canMatch(deckCard, tableCard)) // se combinar,
            {
                int tempScore;

                tableCard = table.removeCard(table.getCardByIndex(i)); // remover da mesa
                earnedCards.addCard(tableCard); // colocar nos pontos
                earnedCards.addCard(deckCard); // colocar carta comprada nos pontos

                deckMatched = true; // marcar que foi feita uma combina��o

                // se acabar o jogo, essa � a melhor combina��o, terminar a jogada
                tempScore = getScore();
                if (tempScore > GameController.MAX_GAME_SCORE) return;

                if(Card.isAShikoPossibility(deckCard) || Card.isAShikoPossibility(tableCard)){
                    if(tempScore > bestScore && thereIsAShikoPossibility) {
                        bestTableCard = tableCard;
                        bestScore = tempScore;
                    } else if(!thereIsAShikoPossibility){
                        bestTableCard = tableCard;
                        bestScore = tempScore;
                    }
                    thereIsAShikoPossibility = true;
                }
                else if((Card.isANanatanPossibilty(deckCard) || Card.isANanatanPossibilty(tableCard)) && !thereIsAShikoPossibility){
                    if(tempScore > bestScore && thereIsANanatanPossibility) {
                        bestTableCard = tableCard;
                        bestScore = tempScore;
                    } else if(!thereIsANanatanPossibility){
                        bestTableCard = tableCard;
                        bestScore = tempScore;
                    }
                    thereIsANanatanPossibility = true;
                }
                else if (tempScore > bestScore && !thereIsAShikoPossibility && !thereIsANanatanPossibility){
                    bestTableCard = tableCard;
                    bestScore = tempScore;
                }



                // desfazer a jogada

                table.insertCard(i, earnedCards.removeCard(tableCard));
                earnedCards.removeCard(deckCard);
            }
        }

        if (deckMatched) // se houve combina��o, jogar as melhores cartas
            earnCards(deckCard, bestTableCard);
        else // se n�o houve combina��o, deixar a carta na mesa
            game.leaveOnTable(this, deckCard);
    }

}
