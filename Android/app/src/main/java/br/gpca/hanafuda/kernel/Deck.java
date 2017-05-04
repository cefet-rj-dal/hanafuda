package br.gpca.hanafuda.kernel;

import java.util.Random;


public class Deck extends CardPack {


    public void shuffleCards() {
        Random rand = new Random();

        int[] rnd = new int[GameController.NUMCARDS_TOTAL];

        for (int i = 0; i < GameController.NUMCARDS_TOTAL; i++)
            rnd[i] = rand.nextInt();

        quickSort(rnd, 0, GameController.NUMCARDS_TOTAL - 1);
    }

    public Card getPeek() {
        return cards.get(getCardsSize() - 1);
    }

    public Card removeCard() // Sobrecarga do m�todo removeCard (Card card) do cardpack
    {
        Card card = cards.get(getCardsSize() - 1);
        cards.remove(card);
        return card;
    }

    public void transferCardsTo(CardPack destin, int numCards) {
        for (int i = 0; i < numCards; i++)
            destin.moveCard(destin, removeCard());
    }

    private void quickSort(int[] vet, int start, int end) {
        if (start < end) {
            int p = partition(vet, start, end);
            quickSort(vet, start, p - 1);
            quickSort(vet, p + 1, end);
        }
    }

    private int partition(int[] vet, int start, int end) {
        int i, aux = start - 1, pivo = vet[end], temp;

        for (i = start; i <= end; i++) {
            if (vet[i] <= pivo) {
                aux++;
                temp = vet[i];
                vet[i] = vet[aux];
                vet[aux] = temp;

                Card card1 = cards.get(i);
                Card card2 = cards.get(aux);
                cards.set(i, card2);
                cards.set(aux, card1);
            }
        }
        return aux;
    }
}
