package br.gpca.hanafuda.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.gpca.hanafuda.kernel.Action;
import br.gpca.hanafuda.kernel.Card;
import br.gpca.hanafuda.kernel.Constants;
import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.Enums.GameStates;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.Player;

public class MainActivity extends Activity {
    public static int playerStarted;

    public int imageOpponent;

    public void setImageOpp(int random) {
        this.imageOpponent = random;
    }

    public int getImageOpp() {
        return imageOpponent;
    }

    public static Constants.PlayerTypes oppType;
    public Constants.PlayerTypes type;

    public void setOpponent(Constants.PlayerTypes type) {
        this.type = type;
        oppType = type;
    }

    public Constants.PlayerTypes getOpponent() {
        return type;
    }

    public boolean advancedMode = false;

    public boolean isAdvancedMode() {
        return advancedMode;
    }

    public void setAdvancedMode(boolean modoAdvance) {
        advancedMode = modoAdvance;
    }

    public ImageView imageOpp;

    public ImageButton btback,btnext;

    public Button btstart;

    public int currentImage = 0;

    public int[] images = {R.drawable.chibimaker1, R.drawable.chibimaker2, R.drawable.chibimaker3,
            R.drawable.chibimaker4, R.drawable.chibimaker5, R.drawable.chibimaker6,
            R.drawable.chibimaker7, R.drawable.chibimaker8, R.drawable.chibimaker9, R.drawable.chibimaker10};

/*

        Load Start Screen

*/
    public void loadOpponentsScreen() {

        setContentView(R.layout.opponent);

        imageOpp = (ImageView) findViewById(R.id.imgopp);
        btback = (ImageButton) findViewById(R.id.btback);
        btnext = (ImageButton) findViewById(R.id.btnext);
        imageOpp.setImageResource(images[currentImage]);
        catchOpponent(currentImage);


        btstart = (Button) findViewById(R.id.btstartGame);
        CheckBox checkAdvance = (CheckBox) findViewById(R.id.checkBoxAdvance);

        //Buttons back and next image Action
        btback.setOnClickListener(new View.OnClickListener() {
            //Set image opponent
            public void onClick(View arg0) {
                currentImage--;
                currentImage = (currentImage + images.length) % images.length;

                imageOpp.setImageResource(images[currentImage]);
                catchOpponent(currentImage);
            }
        });

        btnext.setOnClickListener(new View.OnClickListener() {
            //Set image opponent
            public void onClick(View arg0) {
                currentImage++;
                currentImage = currentImage % images.length;

                imageOpp.setImageResource(images[currentImage]);
                catchOpponent(currentImage);
            }
        });

        //Checkbox Action
        checkAdvance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkAdvance checked?
                if (((CheckBox) v).isChecked()) {
                    setAdvancedMode(true);
                }

            }
        });

        //Start button Action
        btstart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                startGame();
            }
        });

    }

    //Start the game with the opponent choose
    public void startGame() {
        setContentView(R.layout.game);
        start(getOpponent(), isAdvancedMode());
    }


    //Variaveis usadas na construcao do jogo
    public final Handler h = new Handler();
    boolean check = false;
    int auxV;

    public static String winner;

    public static String typeGameOver;

    Card cardMesa, cardPlayer;

    //String alerta
    private String text;
    //Strings para converter inteiros.
    private String sp, sc;
    private TextView scoreplayer;
    private TextView scorecomputer;
    private ImageView image;
    private Toast toast;

    //List para envio das combos
    private ArrayList<Integer> playerCombos;
    private ArrayList<Integer> computerCombos;

    private boolean clickOn = false;
    private boolean cardPlayerOn = false;
    private boolean deckClick = false;
    private boolean zoneOn = false;
    private GameStates currentState = GameStates.NotDefined;

    final GameController game = new GameController();

/*

        Game Screen

*/
    public void start(Constants.PlayerTypes type, final boolean advancedMode) {
        game.newGame(Constants.PlayerTypes.Human, type);

        Player human = game.player[0];
        final Player auxP = human;
        Player computer;
        final Player auxC;
        computer = game.player[1];
        auxC = computer;

    //Montagem Inicial
        //Opponent Image
        image = (ImageView) findViewById(R.id.androidAI);
        opponentImage(getImageOpp());

        //player score
        scoreplayer = (TextView) findViewById(R.id.scoreplayer);
        sp = new Integer(0).toString();
        scoreplayer.setText(sp);

        //computer score
        scorecomputer = (TextView) findViewById(R.id.scorecomputer);
        sc = new Integer(0).toString();
        scorecomputer.setText(sc);

        //Topo Deck
        final Card aux2Topo = game.deck.getPeek();
        final ArrayList<Card> auxTopo = new ArrayList<Card>();
        auxTopo.add(aux2Topo);
        final ArrayAdapter adTopo = new CustomAdapter(this, R.layout.image_adapter, auxTopo);

        //Cartas ganhas computador
        final ArrayAdapter adEarnedComputerKo = new CustomAdapter(this, R.layout.image_adapter, computer.earnedCards.getCardPack());
        final GridView lvEarnedComputerKo;
        lvEarnedComputerKo = (GridView) findViewById(R.id.ko);
        final ArrayAdapter adEarnedComputerTan = new CustomAdapter(this, R.layout.image_adapter, computer.earnedCards.getCardPack());
        final GridView lvEarnedComputerTan;
        lvEarnedComputerTan = (GridView) findViewById(R.id.tan);
        final ArrayAdapter adEarnedComputerTane = new CustomAdapter(this, R.layout.image_adapter, computer.earnedCards.getCardPack());
        final GridView lvEarnedComputerTane;
        lvEarnedComputerTane = (GridView) findViewById(R.id.tane);
        final ArrayAdapter adEarnedComputerKasu = new CustomAdapter(this, R.layout.image_adapter, computer.earnedCards.getCardPack());
        final GridView lvEarnedComputerKasu;
        lvEarnedComputerKasu = (GridView) findViewById(R.id.kasu);

        //Cartas ganhas jogador 
        final ArrayAdapter adEarnedPlayerKo = new CustomAdapter(this, R.layout.image_adapter, human.earnedCards.getCardPack());
        final GridView lvEarnedPlayerKo;
        lvEarnedPlayerKo = (GridView) findViewById(R.id.kop);
        final ArrayAdapter adEarnedPlayerTan = new CustomAdapter(this, R.layout.image_adapter, human.earnedCards.getCardPack());
        final GridView lvEarnedPlayerTan;
        lvEarnedPlayerTan = (GridView) findViewById(R.id.tanp);
        final ArrayAdapter adEarnedPlayerTane = new CustomAdapter(this, R.layout.image_adapter, human.earnedCards.getCardPack());
        final GridView lvEarnedPlayerTane;
        lvEarnedPlayerTane = (GridView) findViewById(R.id.tanep);
        final ArrayAdapter adEarnedPlayerKasu = new CustomAdapter(this, R.layout.image_adapter, human.earnedCards.getCardPack());
        final GridView lvEarnedPlayerKasu;
        lvEarnedPlayerKasu = (GridView) findViewById(R.id.kasup);

        //Cartas do Jogador
        final ArrayAdapter ad2 = new CustomAdapter(this, R.layout.image_adapter, game.player[0].hand.getCardPack());
        final GridView lv2 = (GridView) findViewById(R.id.player);
        lv2.setAdapter(ad2);

        //Zona de Descarte
        final Button zone = (Button) findViewById(R.id.descartzone);

        //Cartas deck
        final ArrayAdapter ad4 = new CustomAdapterDeck(this, R.layout.image_adapter, auxTopo);
        final GridView lv4 = (GridView) findViewById(R.id.deck);
        lv4.setAdapter(ad4);

        //Cartas do computador
        final ArrayAdapter ad3 = new CustomAdapterDeck(this, R.layout.image_adapter, game.player[1].hand.getCardPack());
        final GridView lv3 = (GridView) findViewById(R.id.computer);
        lv3.setAdapter(ad3);

        //Cartas da mesa
        final ArrayAdapter ad = new CustomAdapter(this, R.layout.image_adapter, game.table.getCardPack());
        final GridView lv = (GridView) findViewById(R.id.table);
        lv.setAdapter(ad);

        //Cartas da Mesa acao
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                cardMesa = (Card) lv.getItemAtPosition(position);
                if (game.currentPlayer.ID == auxP.ID && game.canMatchHandTable(auxP) && currentState == GameStates.MatchHandTable
                        && cardPlayerOn) {
                    cardPlayerOn = false;
                    functionHandTable(game, auxP, cardPlayer, cardMesa, ad2, lv2, lv, ad, auxC,
                            adEarnedPlayerKo, lvEarnedPlayerKo, adEarnedPlayerTan, lvEarnedPlayerTan,
                            adEarnedPlayerTane, lvEarnedPlayerTane, adEarnedPlayerKasu, lvEarnedPlayerKasu);
                } else if (game.currentPlayer.ID == auxP.ID && currentState == GameStates.MatchDeckTable && deckClick) {
                    deckClick = false;
                    functionDeckTable(game, auxP, auxC, ad,
                            lv, ad4, lv4, game.getDeckCard(), cardMesa, aux2Topo,
                            auxTopo, adTopo, ad3, lv3,
                            adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                            adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu,
                            adEarnedPlayerKo, lvEarnedPlayerKo, adEarnedPlayerTan, lvEarnedPlayerTan,
                            adEarnedPlayerTane, lvEarnedPlayerTane, adEarnedPlayerKasu, lvEarnedPlayerKasu);

                } else if (!cardPlayerOn && currentState == GameStates.MatchHandTable && !advancedMode) {
                    toast = Toast.makeText(getApplicationContext(), "Pick a card from hand first.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                }

            }
        });

        //Cartas do Jogador Acao
        lv2.setOnItemClickListener(new OnItemClickListener() {
            /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @SuppressLint("NewApi")*/
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                cardPlayer = (Card) lv2.getItemAtPosition(position);
                if(currentState == GameStates.MatchHandTable){
                    cardPlayerOn = true;
                }
                zoneOn = true;
                if (game.currentPlayer.ID == auxP.ID && currentState == GameStates.LeaveOnTable) {
                    game.leaveOnTable(auxP, cardPlayer);
                    lv2.setAdapter(ad2);
                    lv2.invalidate();

                    lv.setAdapter(ad);
                    lv.invalidate();
                    if(!advancedMode) {
                        toast = Toast.makeText(getApplicationContext(), "Pick card from deck.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    currentState = GameStates.MatchDeckTable;
                    clickOn = true;
                }
            }
        });

        //Zona de Descarte Action
        zone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if (currentState == GameStates.MatchHandTable && advancedMode && cardPlayer != null && zoneOn) {
                    game.leaveOnTable(auxP, cardPlayer);
                    //Atualizar mao jogador
                    lv2.setAdapter(ad2);
                    lv2.invalidate();

                    //Atualizar Mesa
                    lv.setAdapter(ad);
                    lv.invalidate();

                    currentState = GameStates.MatchDeckTable;
                    clickOn = true;
                }
                zoneOn = false;
            }
        });

        //Deck acao
        lv4.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //deckCard = (Card) lv4.getItemAtPosition(position);
                if (currentState == GameStates.MatchDeckTable && clickOn) {
                    //setDeckCard(game.deck.removeCard());
                    game.buyFromDeck();
                    auxTopo.clear();
                    auxTopo.add(game.getDeckCard());
                    lv4.setAdapter(adTopo);
                    lv4.invalidate();
                    // Se pode combinar com a mesa espera o jogador se n�o da o alerta e move para a mesa
                    if (game.canMatchDeckTable(game.getDeckCard())) {
                        deckClick = true;
                        if (!advancedMode) {
                            toast = Toast.makeText(getApplicationContext(), "Try to match this card with table.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                        }
                    } else {
                        functionDeckTable(game, auxP, auxC, ad,
                                lv, ad4, lv4, game.getDeckCard(), cardMesa, aux2Topo,
                                auxTopo, adTopo, ad3, lv3,
                                adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                                adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu,
                                adEarnedPlayerKo, lvEarnedPlayerKo, adEarnedPlayerTan, lvEarnedPlayerTan,
                                adEarnedPlayerTane, lvEarnedPlayerTane, adEarnedPlayerKasu, lvEarnedPlayerKasu);

                    }
                    clickOn = false;
                }
                //deckCard = game.getDeckCard();

            }
        });
    //Fim da Montagem Inicial

        //Informa de qual jogador eh o turno inicial e aciona a funcao de circleTurns
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (game.currentPlayer.ID == auxC.ID) {
            text = this.getString(R.string.turncomputer);
            playerStarted = 0;
        } else {
            text = this.getString(R.string.turnplayer);
            playerStarted = 1;
        }
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        circleTurns(game, auxC, auxP, aux2Topo, auxTopo,
                                adTopo, lv4, lv, ad, lv3, ad3, auxC, auxP,
                                adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                                adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        // You Can Customise your Message here
        TextView messageView = (TextView) alert.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }
    //Fim da Interface


    //Ciclo de turnos
    public void circleTurns(final GameController game, Player computer, Player human, final Card aux2Topo, final ArrayList<Card> auxTopo,
                            final ArrayAdapter adTopo, final GridView lv4, final GridView lv, final ArrayAdapter ad, final GridView lv3, final ArrayAdapter ad3, final Player auxC, final Player auxP,
                            final ArrayAdapter adEarnedComputerKo, final GridView lvEarnedComputerKo, final ArrayAdapter adEarnedComputerTan, final GridView lvEarnedComputerTan,
                            final ArrayAdapter adEarnedComputerTane, final GridView lvEarnedComputerTane, final ArrayAdapter adEarnedComputerKasu, final GridView lvEarnedComputerKasu) {

        if (game.currentPlayer.ID == auxC.ID) {
            toast = Toast.makeText(getApplicationContext(), "Computer move", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn(game, auxC, auxP, aux2Topo, auxTopo,
                            adTopo, lv4, lv, ad, lv3, ad3,
                            adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                            adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu);
                }
            }, 5000);

        } else {
            toast = Toast.makeText(getApplicationContext(), "Player move", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
            check = game.canMatchHandTable(human);
            if (!check) {
                if (!advancedMode) {
                    toast = Toast.makeText(getApplicationContext(), "No combination available. Choose a card to discard.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                }
                currentState = GameStates.LeaveOnTable;
            } else {
                currentState = GameStates.MatchHandTable;
            }
        }
    }


    //Funcoes Mesa
    //Funcao1 - combinar mao e mesa
    public void functionHandTable(GameController game, Player human, Card cardPlayer, Card cardMesa, ArrayAdapter ad2, GridView lv2, GridView lv,
                                  ArrayAdapter ad, Player computer,
                                  ArrayAdapter adEarnedPlayerKo, GridView lvEarnedPlayerKo, ArrayAdapter adEarnedPlayerTan, GridView lvEarnedPlayerTan, ArrayAdapter adEarnedPlayerTane, GridView lvEarnedPlayerTane, ArrayAdapter adEarnedPlayerKasu, GridView lvEarnedPlayerKasu) {

        if (game.matchCards(human, cardPlayer, cardMesa) == Constants.ErrorList.Success) {
            lv2.setAdapter(ad2);
            lv2.invalidate();

            lv.setAdapter(ad);
            lv.invalidate();

            //Cartas adquiridas Jogador
            updateEarnedPlayer(human, adEarnedPlayerKo, lvEarnedPlayerKo, adEarnedPlayerTan, lvEarnedPlayerTan, adEarnedPlayerTane, lvEarnedPlayerTane,
                    adEarnedPlayerKasu, lvEarnedPlayerKasu);

            sp = new Integer(human.getScore()).toString();
            scoreplayer.setText(sp);

            if (game.checkVictory(human, game)) {
                openshowWinnerScreen(game, human, computer);


            } else {
                currentState = GameStates.MatchDeckTable;
                clickOn = true;
                if(!advancedMode) {
                    toast = Toast.makeText(getApplicationContext(), "Pick from deck.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                }
            }
        } else {
            if (!advancedMode) {
                toast = Toast.makeText(getApplicationContext(), "Cards do not match.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
            }
        }

    }

    //Funcao2 - combinar deck e mesa
    public void functionDeckTable(GameController game, Player human, Player computer, ArrayAdapter ad,
                                  GridView lv, ArrayAdapter ad4, GridView lv4, Card deckCard, Card cardMesa, Card aux2Topo,
                                  ArrayList<Card> auxTopo, ArrayAdapter adTopo, ArrayAdapter ad3, GridView lv3,
                                  ArrayAdapter adEarnedComputerKo, GridView lvEarnedComputerKo, ArrayAdapter adEarnedComputerTan, GridView lvEarnedComputerTan, ArrayAdapter adEarnedComputerTane, GridView lvEarnedComputerTane, ArrayAdapter adEarnedComputerKasu, GridView lvEarnedComputerKasu,
                                  ArrayAdapter adEarnedPlayerKo, GridView lvEarnedPlayerKo, ArrayAdapter adEarnedPlayerTan, GridView lvEarnedPlayerTan, ArrayAdapter adEarnedPlayerTane, GridView lvEarnedPlayerTane, ArrayAdapter adEarnedPlayerKasu, GridView lvEarnedPlayerKasu) {
        if (game.canMatchDeckTable(deckCard)) {
            if (game.matchCards(human, deckCard, cardMesa) == Constants.ErrorList.Success) {
                lv.setAdapter(ad);
                lv.invalidate();

                //Cartas adquiridas Jogador
                updateEarnedPlayer(human, adEarnedPlayerKo, lvEarnedPlayerKo, adEarnedPlayerTan, lvEarnedPlayerTan, adEarnedPlayerTane, lvEarnedPlayerTane,
                        adEarnedPlayerKasu, lvEarnedPlayerKasu);

                sp = new Integer(human.getScore()).toString();
                scoreplayer.setText(sp);

                if (game.checkVictory(human, game)) {
                    openshowWinnerScreen(game, human, computer);

                } else {
                    auxTopo.clear();
                    auxTopo.add(game.deck.getPeek());
                    lv4.setAdapter(ad4);
                    lv4.invalidate();

                    //check Game Over e passar turno
                    if (game.gameOver(computer)) {
                        openshowWinnerScreen(game, human, computer);


                    } else {
                        game.changeTurn();
                        circleTurns(game, computer, human, aux2Topo, auxTopo,
                                adTopo, lv4, lv, ad, lv3, ad3, computer, human,
                                adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                                adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu);
                    }
                }
            } else {
                if (!advancedMode) {
                    toast = Toast.makeText(getApplicationContext(), "Cards do not match.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                }
                deckClick = true;
            }

        } else {
            currentState = GameStates.LeaveOnTable;
            if (!advancedMode) {
                toast = Toast.makeText(getApplicationContext(), "No combination available. Card was discarded on table.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
            }
            game.leaveOnTable(human, deckCard);
            auxTopo.clear();
            aux2Topo = game.deck.getPeek();
            auxTopo.add(aux2Topo);
            adTopo = new CustomAdapterDeck(this, R.layout.image_adapter, auxTopo);
            lv4.setAdapter(adTopo);
            lv4.invalidate();

            lv.setAdapter(ad);
            lv.invalidate();

    //check Game Over e passar turno
            if (game.gameOver(computer)) {
                openshowWinnerScreen(game, human, computer);
            } else {
                game.changeTurn();
                circleTurns(game, computer, human, aux2Topo, auxTopo,
                        adTopo, lv4, lv, ad, lv3, ad3, computer, human,
                        adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                        adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu);
            }
        }
    }

    //Funcao Computador Turno
    public void computerTurn(GameController game, Player computer, Player human, Card aux2Topo, ArrayList<Card> auxTopo,
                             ArrayAdapter adTopo, GridView lv4, GridView lv, ArrayAdapter ad, GridView lv3, ArrayAdapter ad3,
                             ArrayAdapter adEarnedComputerKo, GridView lvEarnedComputerKo, ArrayAdapter adEarnedComputerTan, GridView lvEarnedComputerTan,
                             ArrayAdapter adEarnedComputerTane, GridView lvEarnedComputerTane, ArrayAdapter adEarnedComputerKasu, GridView lvEarnedComputerKasu) {

        if (computer.type == Constants.PlayerTypes.ExpectiMinimaxPlayer) {
            Action action = null;
            ErrorList errorType = null;

            game.currentState = GameStates.NotDefined;

            for (int i = 0; i < 3; i++) {
                game.nextState();

                switch (game.currentState) {

                    case MatchHandTable:
                        action = computer.action(game.currentState, null, game.table, game.getOpponentPlayer());
                        errorType = game.matchHandTable(action.getCard1(), action.getCard2());
                        break;
                    case BuyFromDeck:
                        errorType = game.buyFromDeck();
                        action = new Action(game.getDeckCard(), null);
                        break;
                    case MatchDeckTable:
                        action = computer.action(game.currentState, game.getDeckCard(), game.table, game.getOpponentPlayer());
                        errorType = game.matchDeckTable(action.getCard1(), action.getCard2());
                        break;
                    case DiscardHandCard:
                        action = computer.action(game.currentState, null, game.table, game.getOpponentPlayer());
                        errorType = game.leaveHandCardOnTable(action.getCard1());
                        break;
                    case DiscardDeckCard:
                        action = new Action(game.getDeckCard(), null);
                        game.leaveDeckCardOnTable(action.getCard1());
                        break;
                }


            }

        } else {
            computer.move(game.table, human);
        }

        //Cartas adquiridas pelo computador
        updateEarnedPlayer(computer, adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan, adEarnedComputerTane, lvEarnedComputerTane,
                adEarnedComputerKasu, lvEarnedComputerKasu);


        //Atualizar Mesa
        lv.setAdapter(ad);
        lv.invalidate();

        //Atualizar Mao Computador
        lv3.setAdapter(ad3);
        lv3.invalidate();

        //Pontuacao do Computador
        sc = new Integer(computer.getScore()).toString();
        scorecomputer.setText(sc);

        if (game.checkVictory(computer, game)) {
            openshowWinnerScreen(game, human, computer);
        } else {

    //check Game Over e passar turno
            if (game.gameOver(human)) {
                openshowWinnerScreen(game, human, computer);

            } else {

                game.changeTurn();
                circleTurns(game, computer, human, aux2Topo, auxTopo,
                        adTopo, lv4, lv, ad, lv3, ad3, computer, human,
                        adEarnedComputerKo, lvEarnedComputerKo, adEarnedComputerTan, lvEarnedComputerTan,
                        adEarnedComputerTane, lvEarnedComputerTane, adEarnedComputerKasu, lvEarnedComputerKasu);
            }
        }
    }


    //Funcao distribuir no layout as cartas ganhas
    public ArrayList<Card> layoutEarnedCards(ArrayList<Card> objects, int ken, int div) {
        Card card;
        ArrayList<Card> arrayCardsTan = new ArrayList<Card>();
        ArrayList<Card> arrayCardsKo = new ArrayList<Card>();
        ArrayList<Card> arrayCardsTane = new ArrayList<Card>();
        ArrayList<Card> arrayCards = new ArrayList<Card>();
        for (int i = 0; i < objects.size(); i++) {
            card = objects.get(i);
            if (ken == 10 && card.ken == ken) {
                if (card.ID % 2 == 0 && card.ID % 4 != 0 && card.ID != 30 && card.ID != 46) {
                    arrayCardsTan.add(card);
                } else {
                    arrayCardsTane.add(card);
                }
            }
            if (ken == 50 && card.ken == ken) {
                arrayCardsKo.add(card);
            }
            if (ken == 0 && card.ken == ken) {
                arrayCards.add(card);
            }
        }
        if (div == 2) {
            return arrayCardsTan;
        }
        if (div == 5) {
            return arrayCardsKo;
        }
        if (div == 0) {
            return arrayCards;
        } else {
            return arrayCardsTane;
        }
    }

    //Imagem do oponente
    public void opponentImage(int imageOpp) {
        switch (imageOpp) {
            case 0:
                image.setImageResource(R.drawable.chibieyes1);
                break;
            case 1:
                image.setImageResource(R.drawable.chibieyes2);
                break;
            case 2:
                image.setImageResource(R.drawable.chibieyes3);
                break;
            case 3:
                image.setImageResource(R.drawable.chibieyes4);
                break;
            case 4:
                image.setImageResource(R.drawable.chibieyes5);
                break;
            case 5:
                image.setImageResource(R.drawable.chibieyes6);
                break;
            case 6:
                image.setImageResource(R.drawable.chibieyes7);
                break;
            case 7:
                image.setImageResource(R.drawable.chibieyes8);
                break;
            case 8:
                image.setImageResource(R.drawable.chibieyes9);
                break;
            case 9:
                image.setImageResource(R.drawable.chibieyes10);
                break;
        }
    }

    //LoadScreen - Imagem do oponente e set Algoritmo
    public void catchOpponent(int currentImage){
        switch (currentImage){
            case 0:
                setOpponent(Constants.PlayerTypes.RandomPlayer);
                setImageOpp(0);
                break;
            case 1:
                setOpponent(Constants.PlayerTypes.Greedy);
                setImageOpp(1);
                break;
            case 2:
                setOpponent(Constants.PlayerTypes.ExpectiMinimaxPlayer);
                setImageOpp(2);
                break;
            case 3:
                setOpponent(Constants.PlayerTypes.ShikoGreedyPlayer);
                setImageOpp(3);
                break;
            case 4:
                setOpponent(Constants.PlayerTypes.NanatanGreedyPlayer);
                setImageOpp(4);
                break;
            case 5:
                setOpponent(Constants.PlayerTypes.ShikoNanatanGreedyPlayer);
                setImageOpp(5);
                break;
            case 6:
                setOpponent(Constants.PlayerTypes.ShikoNanatanGreedyPlayer);
                setImageOpp(6);
                break;
            case 7:
                setOpponent(Constants.PlayerTypes.ShikoNanatanGreedyPlayer);
                setImageOpp(7);
                break;
            case 8:
                setOpponent(Constants.PlayerTypes.ShikoNanatanGreedyPlayer);
                setImageOpp(8);
                break;
            case 9:
                setOpponent(Constants.PlayerTypes.ShikoNanatanGreedyPlayer);
                setImageOpp(9);
                break;
        }
    }

    //Open WinnerScreen
    public void openshowWinnerScreen(GameController game, Player human, Player computer) {
        typeGameOver = game.getTypeGameOver();
        toast = Toast.makeText(getApplicationContext(), "GAME OVER - " + typeGameOver, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
        //Vitoria de quem ganhou Nova tela
        //Destruir a activity
        Boolean draw = game.getDraw(human,computer);
        if(draw){
            winner = "Draw";
        }
        else {
            auxV = game.getWinner(human, computer);
            if (auxV == computer.ID) {
                winner = "Computer";
            } else {
                winner = "Player";
            }
        }

        int ran = getImageOpp();
        sp = new Integer(human.getScore()).toString();
        sc = new Integer(computer.getScore()).toString();
        playerCombos = human.getCombos();
        computerCombos = computer.getCombos();

        final Intent intent = new Intent(this, ShowWinner.class);
        Bundle params = new Bundle();
        params.putString("scorep", sp);
        params.putString("scorec", sc);
        params.putString("winner", winner);
        params.putInt("randomImageSW", ran);
        params.putIntegerArrayList("pcombos", playerCombos);
        params.putIntegerArrayList("ccombos", computerCombos);
        intent.putExtras(params);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

    //atualizar cartas ganhas do jogador
    public void updateEarnedPlayer(Player player, ArrayAdapter adEarnedPlayerKo, GridView lvEarnedPlayerKo, ArrayAdapter adEarnedPlayerTan, GridView lvEarnedPlayerTan,
                                   ArrayAdapter adEarnedPlayerTane, GridView lvEarnedPlayerTane, ArrayAdapter adEarnedPlayerKasu, GridView lvEarnedPlayerKasu) {

        adEarnedPlayerKo = new CustomAdapterEarnedCards(this, R.layout.image_adapter, layoutEarnedCards(player.earnedCards.getCardPack(), 50, 5));
        lvEarnedPlayerKo.setAdapter(adEarnedPlayerKo);
        lvEarnedPlayerKo.invalidate();
        adEarnedPlayerTan = new CustomAdapterEarnedCards(this, R.layout.image_adapter, layoutEarnedCards(player.earnedCards.getCardPack(), 10, 2));
        lvEarnedPlayerTan.setAdapter(adEarnedPlayerTan);
        lvEarnedPlayerTan.invalidate();
        adEarnedPlayerTane = new CustomAdapterEarnedCards(this, R.layout.image_adapter, layoutEarnedCards(player.earnedCards.getCardPack(), 10, 1));
        lvEarnedPlayerTane.setAdapter(adEarnedPlayerTane);
        lvEarnedPlayerTane.invalidate();
        adEarnedPlayerKasu = new CustomAdapterEarnedCards(this, R.layout.image_adapter, layoutEarnedCards(player.earnedCards.getCardPack(), 0, 0));
        lvEarnedPlayerKasu.setAdapter(adEarnedPlayerKasu);
        lvEarnedPlayerKasu.invalidate();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opponent);
        loadOpponentsScreen();
    }

}
