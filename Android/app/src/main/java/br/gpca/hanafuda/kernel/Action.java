package br.gpca.hanafuda.kernel;

import br.gpca.hanafuda.kernel.Enums.ErrorList;

public class Action {
    private Card card1;
    private Card card2;

    public Action(Card card1, Card card2) {
        setCard1(card1);
        setCard2(card2);
    }

    public Card getCard1() {
        return card1;
    }

    private void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    private void setCard2(Card card2) {
        this.card2 = card2;
    }

    public void setAction(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public void setError(ErrorList errorType) {
        // TODO Auto-generated method stub

    }

    public br.gpca.hanafuda.kernel.Constants.ErrorList getErrorType() {
        // TODO Auto-generated method stub
        return null;
    }

}
