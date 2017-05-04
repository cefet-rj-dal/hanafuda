package br.gpca.hanafuda.kernel;

import br.gpca.hanafuda.kernel.Enums.GameStates;

public class Player {
    public int ID;
    public Constants.PlayerTypes type;
    public String name;
    public GameController game;
    public EarnedCards earnedCards = new EarnedCards();
    public Hand hand = new Hand();

    public Player(GameController game) {
        this.game = game;
    }

    public Player clone() {
        Player clonedPlayer = new Player(game);
        clonedPlayer.type = this.type;
        clonedPlayer.ID = this.ID;
        clonedPlayer.name = this.name;
        this.earnedCards.cloneCards(clonedPlayer.earnedCards);
        this.hand.cloneCards(clonedPlayer.hand);

        return clonedPlayer;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public Constants.PlayerTypes getType() {
        return type;
    }

    public void setType(Constants.PlayerTypes type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameController getGame() {
        return game;
    }

    public void setGame(GameController game) {
        this.game = game;
    }

    public EarnedCards getEarnedCards() {
        return earnedCards;
    }

    public void setEarnedCards(EarnedCards earnedCards) {
        this.earnedCards = earnedCards;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public int getScore() {
        int score = 0;

        int cardSize = earnedCards.getCardsSize();

        for (int i = 0; i < cardSize; i++) {
            Card card = earnedCards.getCardByIndex(i);
            score += card.ken;
        }

        for (int i = 0; i < GameController.NUMCOMBOS; i++) {
            Combo combo = game.getComboByIndex(i);
            Family family = game.getFamilyByIndex(i);

            if (combo.matchCards(earnedCards))
                score += combo.getScore();
            if (family.matchCards(earnedCards))
                score += 50;
            
            /*if (combo.matchCards(earnedCards))
                cardpak = combo.getCombo(earnedCards);*/
        }

        return score;
    }


    public void move(Table table, Player otherPlayer) {
         /*Action action = null;
         return action;*/
    }

    protected void earnCards(Card handCard, Card tableCard) {
        if (game.canMatch(handCard, tableCard)) {
            earnedCards.addCard(hand.removeCard(handCard));
            earnedCards.addCard(game.removeFromTable(tableCard));
        }
    }

    public Action action(GameStates gameStates, Card deck, Table table, Player otherPlayer) {
        Action action = null;
        return action;
    }
}


