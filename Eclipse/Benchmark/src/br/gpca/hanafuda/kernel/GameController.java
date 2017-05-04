package br.gpca.hanafuda.kernel;

import java.util.ArrayList;

import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.Enums.GameStates;
import br.gpca.hanafuda.kernel.Enums.PlayerTypes;
import br.gpca.hanafuda.players.ExpectiMinimaxPlayer;
import br.gpca.hanafuda.players.GreedyPlayer;
import br.gpca.hanafuda.players.RandomPlayer;


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

    private static final String[][] combos = new String[][]
            {
                    // {Nome, pontuacao, numero minimo de cartas, cartas do combo[...], terminador}
                    {"Matsu-Kiri-Bozu", "150", "3", "0", "28", "44", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Oozan", "100", "3", "0", "4", "8", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Aotan", "200", "3", "22", "34", "38", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Acata", "200", "3", "14", "18", "26", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Cosan", "150", "3", "2", "6", "10", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Ino-Shika-Cho", "300", "3", "24", "36", "20", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Tepo", "100", "3", "8", "28", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Nizoro", "300", "4", "40", "41", "42", "43", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Hanami I-Pai", "100", "2", "8", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Tsukimi I-Pai", "100", "2", "28", "32", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1"},
                    {"Nanata", "600", "7", "2", "6", "10", "14", "18", "22", "26", "34", "38", "42"},
                    {"Shiko", "600", "4", "0", "8", "28", "44", "-1", "-1", "-1", "-1", "-1", "-1"},
            };
    public static final int NUMCOMBOS = combos.length;

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

    public void newGame(PlayerTypes playerType1, PlayerTypes playerType2) {
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
            combo.setName(combos[i][0]);
            combo.setScore(Integer.parseInt(combos[i][1]));
            combo.setMinNumCards(Integer.parseInt(combos[i][2]));

            for (int j = 3; (j < 13) && (combos[i][j] != "-1"); j++)
                combo.addCard(cardpack.getCardByIndex(Integer.parseInt((combos[i][j]))));
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

    private void createPlayers(PlayerTypes playerType1, PlayerTypes playerType2, GameController game) {
        switch (playerType1) {
            case Human:
                player[0] = new Player(this);
                player[0].ID = 0;
                player[0].type = playerType1;
                player[0].game = this;
                player[0].name = "Player";
                break;
            case RandomPlayer:
                player[0] = new RandomPlayer(this);
                player[0].ID = 0;
                player[0].type = playerType1;
                player[0].name = "Aleatorio";
                break;
            case Greedy:
                player[0] = new GreedyPlayer(this);
                player[0].ID = 0;
                player[0].type = playerType1;
                player[0].name = "Guloso";
                break;
            case ExpectiMinimaxPlayer:
                player[0] = new ExpectiMinimaxPlayer(this);
                player[0].ID = 0;
                player[0].type = playerType1;
                player[0].name = "Expectiminimax";
                break;
            
        }

        switch (playerType2) {
            case Greedy:
                player[1] = new GreedyPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Greedy";
                break;
            case RandomPlayer:
                player[1] = new RandomPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Random";
                break;
            case ExpectiMinimaxPlayer:
                player[1] = new ExpectiMinimaxPlayer(this);
                player[1].ID = 1;
                player[1].type = playerType2;
                player[1].game = this;
                player[1].name = "Expecti-Minimax";
                break;
        }
    }

    private boolean isCorrectShuffle(Player player1, Player player2) {
        if (table.countSameFamilyCards(3)) return false;

        return true;
    }

    public ErrorList matchCards(Player player, Card handCard, Card tableCard)//
    {
        if (handCard == null || tableCard == null) return ErrorList.NullCard;
        if (player.ID != currentPlayer.ID) return ErrorList.WrongPlayer;
        if (!canMatch(handCard, tableCard)) return ErrorList.CardMismatch;

        player.hand.moveCard(player.earnedCards, handCard);
        table.moveCard(player.earnedCards, tableCard);

        return ErrorList.Success;
    }

    public int getWinner(Player player1, Player player2) {
        return (player1.getScore() > player2.getScore()) ? player1.ID : player2.ID;
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

    public Family getFamilyByIndex(int index) {
        return familyPack.get(index);
    }

    private void dealCards(Player player1, Player player2) {
        deck.transferCardsTo(player1.hand, Hand.NUMCARDS);
        deck.transferCardsTo(player2.hand, Hand.NUMCARDS);
        deck.transferCardsTo(table, Table.NUMCARDS);
    }

    private void determineOya(Player player1, Player player2) {
        oya = player1.ID;
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

    public void reset() {
        for (int i = 0; i < 2; i++) {
            player[i].hand.clear();
            player[i].earnedCards.clear();
        }

        table.clear();
        deck.clear();
        oya = -1;
        currentPlayer = null;//-1
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

    public Action getAction() {
        Action action = null;
        ErrorList errorType;
        switch (currentState) {
            case MatchHandTable:
                action = currentPlayer.action(currentState, null, table, getOpponentPlayer());
                //errorType = matchHandTable(action.getCard1(), action.getCard2());
                //action.setError(errorType);
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
        currentPlayer = player1;
    }

    public void changeTurn() {
        byte nextID = (byte) ((currentPlayer.ID + 1) % 2);
        currentPlayer = player[nextID];
    }

    public GameStates nextState() {

        switch (currentState) {
            case MatchHandTable:
            case DiscardHandCard:
            	buyFromDeck();
                setCurrentState(GameStates.BuyFromDeck);
                if (canMatchDeckTable(deckCard))
                    setCurrentState(GameStates.MatchDeckTable);
                else
                    setCurrentState(GameStates.DiscardDeckCard);
                break;
            /*case BuyFromDeck: //Comentado provisoriamente para integracao entre algoritmos
                if (canMatchDeckTable(deckCard))
                    setCurrentState(GameStates.MatchDeckTable);
                else
                    setCurrentState(GameStates.DiscardDeckCard);
                break;*/
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
    
    
    
	public GameStatistics start(){
		
		GameStatistics gameStatistics = new GameStatistics();
		nextState();
		
		while (!gameOver(currentPlayer)){
			//ci.printTurnMessages();
	    	for (int i = 0; (i < 2) && (!gameOver(currentPlayer)); i++){
	    		//Action action = null;
	    		//do {
	    			//ci.printActionMessages();
	    			currentPlayer.move(currentState, table, getOpponentPlayer());
	    			//ci.printPlayerAction(action);
	    			
	    		//} while (action.getErrorType() != ErrorList.Success);
	    		
	    		
	    		gameStatistics.incState(currentState.ordinal());
	    		nextState();
			}
	    	//ci.printEndTurnMessages();
	    	gameStatistics.incTurnCount();
	    	changeTurn();
	    	
		}
		gameStatistics.setPlayerScore(player[0].getScore(), 0);
		gameStatistics.setPlayerScore(player[1].getScore(), 1);
				
		
		//ci.printWinner();
		
		return gameStatistics;
	}
}
