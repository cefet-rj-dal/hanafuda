package br.gpca.hanafuda.kernel;

import java.util.ArrayList;
import java.util.Random;


public class CardPack {
    public ArrayList<Card> cards = new ArrayList<Card>();


    public void addCard(Card card) {
        cards.add(card);
    }

    public void insertCard(int index, Card card) {
        cards.add(index, card);
    }

    public int getCardsSize() {
        return cards.size();
    }

    public Card getCardByID(int cardID) {
        for (Card card : cards) {
            if (cardID == card.ID)
                return card;
        }
        return null;
    }

    public boolean isCardPresent(Card card) {
        return cards.contains(card);
    }

    public Card getCardByIndex(int index) {
        return cards.get(index);
    }

    public void cloneCards(CardPack destin) {
        destin.cards.addAll(cards);
    }

    public void moveCard(CardPack destin, Card card) {
        destin.cards.add(removeCard(card));
    }

    public void clear() {
        cards.clear();
    }

    public void printCards() {
        for (Card card : cards) {
            System.out.print("{" + card.ID + "}, ");
        }
        System.out.println();
    }

    public boolean countSameFamilyCards(int cardsQuantity) {
        int[] cntFamily = new int[GameController.NUMFAMILIES];

        for (int i = 0; i < GameController.NUMFAMILIES; i++)
            cntFamily[i] = 0;

        for (Card card : cards)
            if (++cntFamily[card.family] >= cardsQuantity)
                return true;

        return false;
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    public Card removeCard(Card card) {
        cards.remove(card);
        return card;
    }

    public ArrayList<Card> getCardPack() {
        return cards;
    }

    /*----------------------------------------------------------------------*/
    public int countFamilyCards(int family) {
        int count = 0;

        for (Card card : cards) {
            if (card.getFamily() == family)
                count++;
        }

        return count;
    }

    public boolean isEmpty() {
        return (cards.isEmpty());
    }

    public void shuffleCards() {
        Random rand = new Random();

        int size = getCardsSize();

        int[] rnd = new int[size];

        for (int i = 0; i < size; i++)
            rnd[i] = rand.nextInt();

        quickSort(rnd, 0, size - 1, true);
    }

    public void quickSort(int[] vet, int start, int end, boolean sortAsc) {
        if (start < end) {
            int p = partition(vet, start, end, sortAsc);
            quickSort(vet, start, p - 1, sortAsc);
            quickSort(vet, p + 1, end, sortAsc);
        }
    }

    private int partition(int[] vet, int start, int end, boolean sortAsc) {
        int i, aux = start - 1, pivo = vet[end], temp;

        for (i = start; i <= end; i++) {
            if (sortAsc) {
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
            } else {
                if (vet[i] >= pivo) {
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
        }
        return aux;
    }
}
