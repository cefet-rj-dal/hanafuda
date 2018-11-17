package br.gpca.hanafuda.kernel;


public class Combo extends CardPack {
    private int ID;
    private int score;
    private int minNumCards;
    private String name;

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMinNumCards() {
        return minNumCards;
    }

    public void setMinNumCards(int minNumCards) {
        this.minNumCards = minNumCards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean matchCards(CardPack cards) {
        int qtd = 0;
        int cardSize = cards.getCardsSize();

        for (int i = 0; i < cardSize; i++)
            if (isCardPresent(cards.getCardByIndex(i))) qtd++;
        if (qtd >= minNumCards)
            return true;
        return false;
    }

}
