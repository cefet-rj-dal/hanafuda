package br.gpca.hanafuda.kernel;

import java.util.ArrayList;

import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.Enums.GameStates;
import br.gpca.hanafuda.players.ExpectiMinimaxPlayer;
import br.gpca.hanafuda.players.GreedyPlayer;
import br.gpca.hanafuda.players.RandomPlayer;
import br.gpca.hanafuda.players.ShikoGreedyPlayer;
import br.gpca.hanafuda.players.NanatanGreedyPlayer;
import br.gpca.hanafuda.players.ShikoNanatanGreedyPlayer;


public class GameController {

    public static final int NUMFAMILIES = 12;
    public static final int NUMCARDS_TOTAL = 48;
    public static final int NUM_CARDS = 24;
    public static final int MINIMUM_CARDS = 8;

    public static final int BOGEYMAN = 40;
    public static final int MAX_GAME_SCORE = 600;

    private static final int[] cardsPoints = new int[]
            {
                    50, 0, 10, 0, // 0 - 3 Jan
                    50, 0, 10, 0, // 4 - 7 Fev
                    50, 0, 10, 0, // 8 -11 Mar
                    10, 0, 10, 0, // 12-15 Abr
                    10, 0, 10, 0, // 16-19 Mai
                    10, 0, 10, 0, // 20-23 Jun
                    10, 0, 10, 0, // 24-27 Jul
                    50, 0, 10, 0, // 28-31 Ago
                    10, 0, 10, 0, // 32-35 Set
                    10, 0, 10, 0, // 36-39 Out
                    50, 10, 10, 0, // 40-43 Nov
                    50, 0, 10, 0, // 44-47 Dec
            };

    private static final String[][] _combos = new String[][]
            {
                    // {Nome, pontuacao, numero minimo de cartas, cartas do combo[...], terminador}
                    {"Matsu-Kiri-Bozu", "150", "3", "0", "28", "44", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Oozan", "150", "3", "0", "4", "8", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Aotan", "100", "3", "22", "34", "38", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Acata", "100", "3", "14", "18", "26", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Cosan", "150", "3", "2", "6", "10", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Ino-Shika-Cho", "300", "3", "24", "36", "20", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Tepo", "100", "3", "8", "28", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Nizoro", "200", "4", "40", "41", "42", "43", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Hanami I-Pai", "100", "2", "8", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Tsukimi I-Pai", "100", "2", "28", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Matsu", "50", "4", "0", "1", "2", "3","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Uma", "50", "4", "4", "5", "6", "7","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Sakura", "50", "4", "8", "9", "10", "11","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Fuji", "50", "4", "12", "13", "14", "15","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Susuki", "50", "4", "28", "29", "30", "31","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Momiji", "50", "4", "36", "37", "38", "39","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Fam-Kiri", "50", "4", "44", "45", "46", "47","-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Nanata", "600", "7", "2", "6", "10", "14", "18", "22", "26", "34", "38", "42"},
                    {"Shiko", "600", "4", "0", "8", "28", "44", "-1", "-1", "-1", "-1", "-1", "-1"},
            };
    public static final int NUMCOMBOS = _combos.length;

    public static String getComboName(int i) {
        if ((i >= 0) && (i < _combos.length))
            return _combos[i][0];
        return "";
    }

    private static final String[] familyNames = new String[]
            {
                    "Matsu", "Uma", "Sakura", "Fuji", "Shoubu", "Botan", "Hagi", "Susuki", "Kiku", "Momiji", "Yanagi", "Kiri",
            };

    public CardPack cardpack = new CardPack();
    private ArrayList<Family> familyPack = new ArrayList<Family>();
    private ArrayList<Combo> comboPack = new ArrayList<Combo>();

    public Table table = new Table();
    public Deck deck = new Deck();

    private int oya = -1;
    //public int currentPlayer = -1;
    public Player currentPlayer = null;

    public Player[] player = new Player[2];

    public GameStates currentState = GameStates.NotDefined;
    private Card deckCard = null;//
    public String typeGameOver;//

    //Metodos

    public Table getTable() {//
        return table;
    }

    public Card getDeckCard() {//
        return deckCard;
    }

    private void setDeckCard(Card deckCard) {//
        this.deckCard = deckCard;
    }

    public void setTypeGameOver(String typeGameOver) {
        this.typeGameOver = typeGameOver;
    }

    public String getTypeGameOver() {//
        return typeGameOver;
    }


    public GameController() {
        populateCardPack();
        populateFamilyPack();
        populateComboPack();
    }

    public void newGame(Constants.PlayerTypes playerType1, Constants.PlayerTypes playerType2) {
        createPlayers(playerType1, playerType2, null);

        do {
            deck.clear();
            table.clear();
            player[0].hand.clear();
            player[1].hand.clear();
            cardpack.cloneCards(deck);

            deck.shuffleCards();

            dealCards(player[0], player[1]);


        } while (!isCorrectShuffle(player[0], player[1]));

        determineOya(player[0], player[1]);
        //setCurrentPlayer(oya);
        setOya(player[0], player[1]);

    }

    private boolean hasSpecialCombos(Player p, GameController game) {

        for (int i = 0; i < NUMCOMBOS; i++) {
            Combo combo = game.getComboByIndex(i);
            if (combo.matchCards(p.earnedCards) && combo.getScore() == MAX_GAME_SCORE) {
                if (combo.getName().compareTo("Nanata") == 0) {
                    setTypeGameOver("Nanata");
                } else {
                    setTypeGameOver("Shiko");
                }
                return true;
            }
        }
        return false;
    }


    private void populateCardPack() {
        for (int i = 0; i < GameController.NUMCARDS_TOTAL; i++) {
            Card card = new Card();
            card.ID = i;
            card.family = i / 4;
            card.ken = cardsPoints[i];
            cardpack.addCard(card);
        }
    }

    private void populateComboPack() {
        for (int i = 0; i < NUMCOMBOS; i++) {
            Combo combo = new Combo();

            combo.setID(i);
            combo.setName(_combos[i][0]);
            combo.setScore(Integer.parseInt(_combos[i][1]));
            combo.setMinNumCards(Integer.parseInt(_combos[i][2]));

            for (int j = 3; (j < 13) && (_combos[i][j] != "-1"); j++)
                combo.addCard(cardpack.getCardByIndex(Integer.parseInt((_combos[i][j]))));
            comboPack.add(combo);
        }
    }

    private void populateFamilyPack() {
        for (int i = 0; i < NUMFAMILIES; i++) {
            Family family = new Family();
            family.ID = i;
            family.name = familyNames[i];

            for (int j = 0; j < 4; j++)
                family.addCard(cardpack.getCardByIndex(j + family.ID * 4));
            familyPack.add(family);
        }
    }

    private void createPlayers(Constants.PlayerTypes playerType1, Constants.PlayerTypes playerType2, GameController game) {
        switch (playerType1) {
            case Human:
                player[0] = new Player(this);
                player[0].ID = 0;
                player[0].type = playerType1;
                player[0].game = this;
                player[0].name = "Player";
                System.out.println("\nID: "+player[0].ID);
                System.out.println("type: "+player[0].type);
                System.out.println("game: "+player[0].game);
                System.out.println("name: "+player[0].name);
                break;
        }

        switch (playerType2) {
            case Greedy:
                player[1] = new GreedyPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Greedy";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;
            case RandomPlayer:
                player[1] = new RandomPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Random";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;
            case ExpectiMinimaxPlayer:
                player[1] = new ExpectiMinimaxPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Expecti-Minimax";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;
            case ShikoGreedyPlayer:
                player[1] = new ShikoGreedyPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Shiko-Greedy";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;
            case NanatanGreedyPlayer:
                player[1] = new NanatanGreedyPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Nanatan-Greedy";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;

            case ShikoNanatanGreedyPlayer:
                player[1] = new ShikoNanatanGreedyPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Shiko-Nanatan-Greedy";
                System.out.println("\nID: "+player[1].ID);
                System.out.println("type: "+player[1].type);
                System.out.println("game: "+player[1].game);
                System.out.println("name: "+player[1].name);
                break;
        }
    }

    private boolean isCorrectShuffle(Player player1, Player player2) {
        if (table.countSameFamilyCards(3)) return false;

        return true;
    }

    public Constants.ErrorList matchCards(Player player, Card handCard, Card tableCard)//
    {
        if (handCard == null || tableCard == null) return Constants.ErrorList.NullCard;
        if (player.ID != currentPlayer.ID) return Constants.ErrorList.WrongPlayer;
        if (!canMatch(handCard, tableCard)) return Constants.ErrorList.CardMismatch;

        player.hand.moveCard(player.earnedCards, handCard);
        table.moveCard(player.earnedCards, tableCard);

        return Constants.ErrorList.Success;
    }

    public int getWinner(Player player1, Player player2) {
        return (player1.getScore() > player2.getScore()) ? player1.ID : player2.ID;
    }

    public Boolean getDraw(Player player1, Player player2){
        if(player1.getScore() == player2.getScore()){
            return true;
        }
        return false;
    }

    public boolean canMatchHandTable(Player player) {
        int handSize = player.hand.getCardsSize();
        int tableSize = table.getCardsSize();

        for (int i = 0; i < handSize; i++) {
            Card handCard = player.hand.getCardByIndex(i);

            for (int j = 0; j < tableSize; j++)
                if (canMatch(handCard, table.getCardByIndex(j)))
                    return true;
        }
        return false;
    }

    public boolean canMatchDeckTable(Card deckCard) {
        for (int i = 0; i < table.getCardsSize(); i++) {
            if (canMatch(deckCard, table.getCardByIndex(i)))
                return true;
        }

        return false;
    }

    private void setCurrentState(GameStates gameStates) {
        currentState = gameStates;
    } //

    public Combo getComboByIndex(int index) {
        return comboPack.get(index);
    }

    private void dealCards(Player player1, Player player2) {
        deck.transferCardsTo(player1.hand, Hand.NUMCARDS);
        deck.transferCardsTo(player2.hand, Hand.NUMCARDS);
        deck.transferCardsTo(table, Table.NUMCARDS);
    }

    private void determineOya(Player player1, Player player2) {
        //oya = player1.ID;
        Card card1 = deck.getCardByIndex(deck.getCardsSize() - 1);
        Card card2 = deck.getCardByIndex(deck.getCardsSize() - 2);

        oya = (card1.family < card2.family) ? player1.ID : player2.ID;
    }

    //Deprecated
    public void leaveOnTable(Player player, Card card) {
        if (player.hand.contains(card))
            player.hand.removeCard(card);
        table.addCard(card);
    }

    public Card removeFromTable(Card card) {
        return table.removeCard(card);
    }


    public boolean canMatch(Card card1, Card card2) {
        if (card1.family == card2.family)
            return true;
        if ((card1.ID == BOGEYMAN && card2.ken > 0))
            return true;
        if ((card2.ID == BOGEYMAN && card1.ken > 0))
            return true;
        return false;
    }

    public boolean checkVictory(Player player, GameController game) {
        if (hasSpecialCombos(player, game))
            return true;
        return false;
    }

    public boolean gameOver(Player player) {
        if (player.hand.getCardsSize() == 0) {
            setTypeGameOver("No cards at hand");
            return true;
        }
        if (deck.getCardsSize() <= GameController.MINIMUM_CARDS) {
            setTypeGameOver("Minimum number of cards at deck reached");
            return true;
        }
        return false;
    }

    public ErrorList matchHandTable(Card handCard, Card tableCard) {
        if (handCard == null && tableCard == null)
            return ErrorList.NullCard;

        if (handCard == null && tableCard != null)
            return ErrorList.NullCard;

        if (handCard != null && tableCard == null) {
            currentPlayer.hand.moveCard(table, handCard);
            return ErrorList.Success;
        }

        if (!canMatch(handCard, tableCard))
            return ErrorList.CardMismatch;

        currentPlayer.hand.moveCard(currentPlayer.earnedCards, handCard);
        table.moveCard(currentPlayer.earnedCards, tableCard);

        return ErrorList.Success;
    }

    public ErrorList leaveHandCardOnTable(Card handCard) {
        if (handCard == null) return ErrorList.NullCard;
        if (!currentPlayer.hand.contains(handCard)) return ErrorList.CardNotFound;
        currentPlayer.hand.moveCard(table, handCard);
        return ErrorList.Success;
    }

    public ErrorList leaveDeckCardOnTable(Card deckCard) {

        if (deckCard == null) return ErrorList.NullCard;
        table.addCard(deckCard);
        return ErrorList.Success;
    }

    public ErrorList buyFromDeck() {
        if (deck.isEmpty()) return ErrorList.ZeroSize;
        deckCard = deck.removeCard();
        setDeckCard(deckCard);
        return ErrorList.Success;
    }

    public Action getAction(GameStates currentState, Card deckCard, Table table,//move
                            Player opponent) {
        Action action = null;
        ErrorList errorType;
        switch (currentState) {
            case MatchHandTable:
                action = currentPlayer.action(currentState, null, table, getOpponentPlayer());
                errorType = matchHandTable(action.getCard1(), action.getCard2());
                action.setError(errorType);
                break;
            case BuyFromDeck:
                errorType = buyFromDeck();
                action = new Action(deckCard, null);
                action.setError(errorType);
                break;
            case MatchDeckTable:
                action = currentPlayer.action(currentState, deckCard, table, getOpponentPlayer());
                errorType = matchDeckTable(action.getCard1(), action.getCard2());
                break;
            case DiscardHandCard:
                action = currentPlayer.action(currentState, null, table, getOpponentPlayer());
                errorType = leaveHandCardOnTable(action.getCard1());
                break;
            case DiscardDeckCard:
                action = new Action(deckCard, null);
                leaveDeckCardOnTable(action.getCard1());

                break;
            case NotDefined:

        }

        return action;
    }

    public ErrorList matchDeckTable(Card deckCard, Card tableCard) {
        if (deckCard == null || tableCard == null) return ErrorList.NullCard;
        if (!canMatch(deckCard, tableCard)) return ErrorList.CardMismatch;

        currentPlayer.hand.moveCard(currentPlayer.earnedCards, deckCard);
        table.moveCard(currentPlayer.earnedCards, tableCard);

        return ErrorList.Success;
    }

    public Player getOpponentPlayer() {
        byte opponentID = (byte) ((currentPlayer.ID + 1) % 2);
        return player[opponentID];
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    private void setOya(Player player1, Player player2) {
        //currentPlayer = player1;
        if(oya == player1.ID) {
            currentPlayer = player1;
        }
        else{
            currentPlayer = player2;
        }
    }

    public void changeTurn() {
        byte nextID = (byte) ((currentPlayer.ID + 1) % 2);
        currentPlayer = player[nextID];
    }

    public GameStates nextState() {

        switch (currentState) {
            case MatchHandTable:
            case DiscardHandCard:
                setCurrentState(GameStates.BuyFromDeck);
                break;
            case BuyFromDeck:
                if (canMatchDeckTable(deckCard))
                    setCurrentState(GameStates.MatchDeckTable);
                else
                    setCurrentState(GameStates.DiscardDeckCard);
                break;
            case MatchDeckTable:
            case DiscardDeckCard:
                if (canMatchHandTable(getOpponentPlayer()))
                    setCurrentState(GameStates.MatchHandTable);
                else
                    setCurrentState(GameStates.DiscardHandCard);
                break;
            case NotDefined:
                if (canMatchHandTable(currentPlayer))
                    setCurrentState(GameStates.MatchHandTable);
                else
                    setCurrentState(GameStates.DiscardHandCard);
                break;
        }

        return currentState;

    }
}
