package br.gpca.hanafuda.kernel;


public class Family extends CardPack {
    public static final int CARDS_PER_FAMILY = 4;

    public int ID;
    public String name;

    public boolean matchCards(CardPack cards) {
        int qtd = 0;
        int cardSize = cards.getCardsSize();

        for (int i = 0; i < cardSize; i++)
            if (isCardPresent(cards.getCardByIndex(i))) qtd++;
        if (qtd == CARDS_PER_FAMILY)
            return true;
        return false;
    }
}
