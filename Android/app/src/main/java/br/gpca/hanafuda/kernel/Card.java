package br.gpca.hanafuda.kernel;

public class Card {
    public int ID;
    public int family;
    public int ken;

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public int getKen() {
        return ken;
    }

    public void setKen(int ken) {
        this.ken = ken;
    }

    public static boolean isAShikoPossibility(Card card){

        if(card.getID() == 0 || card.getID() == 8 || card.getID() == 28 || card.getID() == 44){
            return true;
        }

        return false;
    }

    public static boolean isANanatanPossibilty(Card card){

        if(card.getID() == 2 || card.getID() == 6 || card.getID() == 10 || card.getID() == 14
                || card.getID() == 18 || card.getID() == 22 || card.getID() == 26 || card.getID() == 34
                || card.getID() == 38 || card.getID() == 42){
            return true;
        }

        return false;
    }

    public static boolean isTheBogeyman(Card card){
        if(card.getID() == 40){
            return true;
        }

        return false;
    }
}

