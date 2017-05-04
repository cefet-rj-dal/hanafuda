package br.gpca.hanafuda.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.gpca.hanafuda.kernel.Action;
import br.gpca.hanafuda.kernel.Card;
import br.gpca.hanafuda.kernel.CardPack;
import br.gpca.hanafuda.kernel.EarnedCards;
import br.gpca.hanafuda.kernel.Enums.ErrorList;
import br.gpca.hanafuda.kernel.Enums.GameStates;
import br.gpca.hanafuda.kernel.Enums.NodeState;
import br.gpca.hanafuda.kernel.Enums.NodeType;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.Hand;
import br.gpca.hanafuda.kernel.Player;
import br.gpca.hanafuda.kernel.Table;


class Node {

    private ArrayList<Node> childList = new ArrayList<Node>();
    private NodeType nodeType = NodeType.notSet;
    private float value = -1;
    private byte childs = 0;
    private int count = 1;
    private float totalScore = 0;
    private Node selectedNode = null;
    private static int totalNodes;
    private Card card1 = null;
    private Card card2 = null;
    private String path = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public NodeState getState() {
        if (card1 == null && card2 == null)
            return NodeState.Root;
        if (card1 != null && card2 == null)
            return NodeState.Discard;
        if (card1 != null && card2 != null)
            return NodeState.Match;
        throw new IllegalArgumentException("Unknown state");
    }

    public void append(String str) {
        path = path + str;
    }

    public int getCount() {
        return count;
    }

    public float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Node() {
        totalNodes++;
    }

    public void setTotalNodes(int totalNodes) {
        Node.totalNodes = totalNodes;
    }

    public Node getChild(byte index) {
        return childList.get(index);
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public byte getChildsNumber() {
        return childs;
    }

    public void setChilds(byte childs) {
        this.childs = childs;
    }

    public Card getCard1() {
        return card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCards(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public Node createChild(NodeType nodeType, Card card1, Card card2) {

        Node newChildNode = new Node();
        newChildNode.card1 = card1;
        newChildNode.card2 = card2;
        newChildNode.nodeType = nodeType;
        totalNodes++;
        childList.add(newChildNode);
        return newChildNode;
    }

    public NodeType getType() {
        return nodeType;
    }

    public void setType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}

class Tree {
    private Node root = null;

    public Node createRoot(NodeType nodeType) {
        Node rootNode = new Node();
        root = rootNode;
        root.setTotalNodes(1);
        root.setType(nodeType);
        return rootNode;
    }

    public Node getRoot() {
        return root;
    }
}


public class ExpectiMinimaxPlayer extends Player {

    private Random rnd = new Random();
    private CardPack invisibleCards = null;
    //private WriteFile writer = new WriteFile("graph.dot", true);
    private static final byte initialDepth = 0;
    private static final byte maximumDepth = 3;

    public ExpectiMinimaxPlayer(GameController game) {
        super(game);
    }


    //@SuppressWarnings("incomplete-switch")
    @Override
    public void move(GameStates currentState, Table table, Player otherPlayer) {
    	Card deckCard = game.getDeckCard();
    	
        Action action;
        switch (currentState) {
            case MatchHandTable:
                action = convertNodeInAction(getBestNode(NodeType.Max, table, this, otherPlayer, maximumDepth));
                earnCards(action.getCard1(), action.getCard2());
                                
                //game.matchCards(this, action.getCard1(), action.getCard2());
                return;
            case MatchDeckTable:
            	
                Player p = new Player(game);
                this.earnedCards.cloneCards(p.earnedCards);
                p.hand.addCard(deckCard);
                action = convertNodeInAction(getBestNode(NodeType.Max, table, p, otherPlayer, maximumDepth));
                earnCards(action.getCard1(), action.getCard2());
                //game.matchCards(this, action.getCard1(), action.getCard2());
                return;
            case DiscardHandCard:
                action = convertNodeInAction(getBestNode(NodeType.Max, table, this, otherPlayer, (byte) 4));
                game.leaveOnTable(this, action.getCard1());
                //game.matchCards(this, action.getCard1(), action.getCard2());
                return;
            case DiscardDeckCard:
            	game.leaveDeckCardOnTable(deckCard);
            	return;
        }
        
        

         throw new IllegalArgumentException(ErrorList.NullAction.toString());
    }

    private Node getBestNode(NodeType nodeType, Table table,
                             Player ownPlayer, Player opponent, byte maximumDepth) {

        Table clonedTable = new Table();
        table.cloneCards(clonedTable);

        Player clonedPlayer = ownPlayer.clone();
        invisibleCards = getInvisibleCards(opponent.earnedCards, table);
        Player supposedOppPlayer = opponent.clone();
        Tree tree = new Tree();
        Node root = tree.createRoot(nodeType);

        invisibleCards.shuffleCards();
        supposedOppPlayer.hand.clear();
        supposedOppPlayer.hand = getBestOppHand(invisibleCards, table, supposedOppPlayer);


        Node miniMaxBestNode = expectiMiniMax(root, clonedPlayer,
                supposedOppPlayer, clonedTable, initialDepth, maximumDepth)
                .getSelectedNode();


        return miniMaxBestNode;
    }


    private Node MiniMax(Node node, Player player, Player oppPlayer,
                         Table table, byte depth, byte maximumDepth) {
        boolean gameOver = (node.getType() == NodeType.Max && player.hand
                .isEmpty())
                || (node.getType() == NodeType.Min && oppPlayer.hand.isEmpty());

        if (depth == maximumDepth || gameOver) {
            node.setValue((float) (player.getScore() - oppPlayer.getScore()));
            return node;
        }

        if (node.getType() == NodeType.Max)
            return Max(node, player, oppPlayer, table, depth, maximumDepth);

        if (node.getType() == NodeType.Min)
            return Min(node, player, oppPlayer, table, depth, maximumDepth);

        throw new IllegalArgumentException("Minimax retornando n� nulo");
    }

    private Node Max(Node node, Player player, Player oppPlayer, Table table,
                     byte depth, byte maximumDepth) {
        boolean matched = false;
        for (byte handCardIndex = 0; handCardIndex < player.hand.getCardsSize(); handCardIndex++) {
            Card handCard = player.hand.getCardByIndex(handCardIndex);
            for (byte tableCardIndex = 0; tableCardIndex < table.getCardsSize(); tableCardIndex++) {
                Card tableCard = table.getCardByIndex(tableCardIndex);
                if (game.canMatch(handCard, tableCard)) {
                    matched = true;
                    Node child = node.createChild(NodeType.Min, handCard,
                            tableCard);
                    node.setChilds((byte) (node.getChildsNumber() + 1));

                    Player clonedPlayer = player.clone();
                    Table clonedTable = new Table();
                    table.cloneCards(clonedTable);

                    // Remove as cartas da mesa e adiciona �s cartas ganhas
                    clonedPlayer.hand.moveCard(clonedPlayer.earnedCards,
                            handCard);
                    clonedTable.moveCard(clonedPlayer.earnedCards, tableCard);

                    // Chama a fun��o recursivamente

                    Node bestNode = MiniMax(child, clonedPlayer, oppPlayer,
                            clonedTable, (byte) (depth + 1), maximumDepth);

                    if (node.getValue() == -1) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    } else if (bestNode.getValue() > node.getValue()) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    }
                }
            }
        }

        // Se nenhuma carta da m�o combinar com a da mesa, descartar na mesa
        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < player.hand
                    .getCardsSize(); handCardIndex++) {
                Card handCard = player.hand.getCardByIndex(handCardIndex);

                Player clonedPlayer = player.clone();
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedPlayer.hand.moveCard(clonedTable, handCard);

                // cria um novo filho, define seu tipo, e define o n�vel que o
                // n� pertence

                Node child = node.createChild(NodeType.Min, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a fun��o recursivamente

                Node bestNode = MiniMax(child, clonedPlayer, oppPlayer,
                        clonedTable, (byte) (depth + 1), maximumDepth);

                if (node.getValue() == -1) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                } else if (bestNode.getValue() > node.getValue()) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                }
            }
        }
        // node.setSelectedNode(selectBestNodeAtRandom(node));

        return node;
    }

    private Node Min(Node node, Player player, Player oppPlayer, Table table,
                     byte depth, byte maximumDepth) {
        boolean matched = false;
        for (byte handCardIndex = 0; handCardIndex < oppPlayer.hand
                .getCardsSize(); handCardIndex++) {
            Card handCard = oppPlayer.hand.getCardByIndex(handCardIndex);
            for (byte tableCardIndex = 0; tableCardIndex < table.getCardsSize(); tableCardIndex++) {
                Card tableCard = table.getCardByIndex(tableCardIndex);
                if (game.canMatch(handCard, tableCard)) {
                    matched = true;
                    Node child = node.createChild(NodeType.Max, handCard,
                            tableCard);
                    node.setChilds((byte) (node.getChildsNumber() + 1));

                    Player clonedOppPlayer = oppPlayer.clone();
                    Table clonedTable = new Table();
                    table.cloneCards(clonedTable);

                    // Remove as cartas da mesa e adiciona �s cartas ganhas
                    clonedOppPlayer.hand.moveCard(clonedOppPlayer.earnedCards,
                            handCard);
                    clonedTable
                            .moveCard(clonedOppPlayer.earnedCards, tableCard);

                    // Chama a fun��o recursivamente

                    Node bestNode = MiniMax(child, player, clonedOppPlayer,
                            clonedTable, (byte) (depth + 1), maximumDepth);
                    if (node.getValue() == -1) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    } else if (bestNode.getValue() < node.getValue()) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    }
                }
            }
        }

        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < oppPlayer.hand
                    .getCardsSize(); handCardIndex++) {
                Player clonedOppPlayer = oppPlayer.clone();
                Card handCard = oppPlayer.hand.getCardByIndex(handCardIndex);
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedOppPlayer.hand.moveCard(clonedTable, handCard);

                Node child = node.createChild(NodeType.Max, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a fun��o recursivamente
                Node bestNode = MiniMax(child, player, clonedOppPlayer,
                        clonedTable, (byte) (depth + 1), maximumDepth);

                if (node.getValue() == -1) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                } else if (bestNode.getValue() < node.getValue()) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                }
            }
        }

        // node.setSelectedNode(selectBestNodeAtRandom(node));

        return node;
    }

    private Node expectiMiniMax(Node node, Player player, Player oppPlayer,
                                Table table, byte depth, byte maximumDepth) {
        boolean gameOver = (node.getType() == NodeType.Max && player.hand
                .isEmpty())
                || (node.getType() == NodeType.Min && oppPlayer.hand.isEmpty());

        if (depth == maximumDepth || gameOver) {
            node.setValue((short) (player.getScore() - oppPlayer.getScore()));
            return node;
        }

        if (node.getType() == NodeType.Max)
            return expectiMax(node, player, oppPlayer, table, depth,
                    maximumDepth);

        if (node.getType() == NodeType.Min)
            return expectiMin(node, player, oppPlayer, table, depth,
                    maximumDepth);

        throw new IllegalArgumentException("Minimax retornando n� nulo");
    }

    private Node expectiMax(Node node, Player player, Player oppPlayer,
                            Table table, byte depth, byte maximumDepth) {
        boolean matched = false;
        for (byte handCardIndex = 0; handCardIndex < player.hand.getCardsSize(); handCardIndex++) {
            Card handCard = player.hand.getCardByIndex(handCardIndex);
            for (byte tableCardIndex = 0; tableCardIndex < table.getCardsSize(); tableCardIndex++) {
                Card tableCard = table.getCardByIndex(tableCardIndex);
                if (game.canMatch(handCard, tableCard)) {
                    matched = true;
                    Node child = node.createChild(NodeType.Min, handCard,
                            tableCard);
                    node.setChilds((byte) (node.getChildsNumber() + 1));

                    Player clonedPlayer = player.clone();
                    Table clonedTable = new Table();
                    table.cloneCards(clonedTable);

                    // Remove as cartas da mesa e adiciona �s cartas ganhas
                    clonedPlayer.hand.moveCard(clonedPlayer.earnedCards,
                            handCard);
                    clonedTable.moveCard(clonedPlayer.earnedCards, tableCard);

                    // Chama a fun��o recursivamente

                    Node bestNode = expectiMiniMax(child, clonedPlayer,
                            oppPlayer, clonedTable, (byte) (depth + 1),
                            maximumDepth);

                    if (node.getValue() == -1) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    } else if (bestNode.getValue() > node.getValue()) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    }
                }
            }
        }

        // Se nenhuma carta da m�o combinar com a da mesa, descartar na mesa
        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < player.hand.getCardsSize(); handCardIndex++) {
                Card handCard = player.hand.getCardByIndex(handCardIndex);

                Player clonedPlayer = player.clone();
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedPlayer.hand.moveCard(clonedTable, handCard);

                // cria um novo filho, define seu tipo, e define o n�vel que o
                // n� pertence

                Node child = node.createChild(NodeType.Min, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a fun��o recursivamente

                Node bestNode = expectiMiniMax(child, clonedPlayer, oppPlayer,
                        clonedTable, (byte) (depth + 1), maximumDepth);

                if (node.getValue() == -1) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                } else if (bestNode.getValue() > node.getValue()) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                }
            }
        }
        // node.setSelectedNode(selectBestNodeAtRandom(node));

        return node;
    }

    private Node expectiMin(Node node, Player player, Player oppPlayer,
                            Table table, byte depth, byte maximumDepth) {
        boolean matched = false;
        for (byte handCardIndex = 0; handCardIndex < oppPlayer.hand
                .getCardsSize(); handCardIndex++) {
            Card handCard = oppPlayer.hand.getCardByIndex(handCardIndex);
            for (byte tableCardIndex = 0; tableCardIndex < table.getCardsSize(); tableCardIndex++) {
                Card tableCard = table.getCardByIndex(tableCardIndex);
                if (game.canMatch(handCard, tableCard)) {
                    matched = true;
                    Node child = node.createChild(NodeType.Max, handCard,
                            tableCard);
                    node.setChilds((byte) (node.getChildsNumber() + 1));

                    Player clonedOppPlayer = oppPlayer.clone();
                    Table clonedTable = new Table();
                    table.cloneCards(clonedTable);

                    // Remove as cartas da mesa e adiciona �s cartas ganhas
                    clonedOppPlayer.hand.moveCard(clonedOppPlayer.earnedCards,
                            handCard);
                    clonedTable
                            .moveCard(clonedOppPlayer.earnedCards, tableCard);

                    // Chama a fun��o recursivamente

                    Node bestNode = expectiMiniMax(child, player,
                            clonedOppPlayer, clonedTable, (byte) (depth + 1),
                            maximumDepth);

                    int matches = countMatches(invisibleCards, child.getCard2()
                            .getFamily());
                    float chance = matches
                            / (float) invisibleCards.getCardsSize();
                    bestNode.setValue(bestNode.getValue() * chance);

                    if (node.getSelectedNode() == null) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    } else if (bestNode.getValue() < node.getValue()) {
                        node.setValue(bestNode.getValue());
                        node.setSelectedNode(bestNode);
                    }
                }
            }
        }

        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < oppPlayer.hand
                    .getCardsSize(); handCardIndex++) {
                Player clonedOppPlayer = oppPlayer.clone();
                Card handCard = oppPlayer.hand.getCardByIndex(handCardIndex);
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedOppPlayer.hand.moveCard(clonedTable, handCard);

                Node child = node.createChild(NodeType.Max, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a fun��o recursivamente
                Node bestNode = expectiMiniMax(child, player, clonedOppPlayer,
                        clonedTable, (byte) (depth + 1), maximumDepth);

                int matches = countMatches(invisibleCards, child.getCard1()
                        .getFamily());
                float chance = matches / (float) invisibleCards.getCardsSize();
                bestNode.setValue(bestNode.getValue() * chance);

                if (node.getSelectedNode() == null) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                } else if (bestNode.getValue() < node.getValue()) {
                    node.setValue(bestNode.getValue());
                    node.setSelectedNode(bestNode);
                }
            }
        }

        // node.setSelectedNode(selectBestNodeAtRandom(node));

        return node;
    }

    @SuppressWarnings("unused")
    private Node selectBestNodeAtRandom(Node parent) {

        List<Node> nodesList = new ArrayList<Node>();

        for (int i = 0; i < parent.getChildsNumber(); i++) {
            if (parent.getChild((byte) i).getValue() == parent
                    .getSelectedNode().getValue())
                nodesList.add(parent.getChild((byte) i));
        }

        return nodesList.get(rnd.nextInt(nodesList.size()));
    }

    private int countMatches(CardPack cardpack, int family) {
        int count = 0;
        count = cardpack.countFamilyCards(family);
        if (cardpack.contains(game.cardpack.getCardByIndex((byte) 40)))
            count++;
        return count;
    }

    private Hand getBestOppHand(CardPack invisibleCards,
                                Table table, Player oppPlayer) {

        int vetAux[] = new int[invisibleCards.getCardsSize() * table.getCardsSize()];

        // Inicializa o vetor auxiliar
        for (int i = 0; i < vetAux.length; i++) {
            vetAux[i] = -1;
        }

        // Insere os scores no vetor vetAux pra cada combina��o com a mesa
        // Depois desfaz a altera��o
        for (int i = 0; i < invisibleCards.getCardsSize(); i++) {
            Card invisibleCard = invisibleCards.getCardByIndex((byte) i);
            for (int j = 0; j < table.getCardsSize(); j++) {
                Card tableCard = table.getCardByIndex((byte) j);
                if (game.canMatch(invisibleCard, tableCard)) {
                    oppPlayer.earnedCards.addCard(invisibleCard);
                    oppPlayer.earnedCards.addCard(tableCard);

                    int oppPlayerScore = oppPlayer.getScore();

                    vetAux[i] = oppPlayerScore;

                    oppPlayer.earnedCards.removeCard(invisibleCard);
                    oppPlayer.earnedCards.removeCard(tableCard);
                }
            }
        }

        invisibleCards.quickSort(vetAux, 0, invisibleCards.getCardsSize() - 1, false);
        Hand newHand = new Hand();

        for (int i = 0; i < vetAux.length && vetAux[i] != -1; i++) {
            newHand.addCard(invisibleCards.getCardByIndex((byte) i));
        }

        return newHand;
    }

    private Action convertNodeInAction(Node node) {
        return new Action(node.getCard1(), node.getCard2());
    }


    private CardPack getInvisibleCards(EarnedCards oppEarnedCards, Table table) {
        CardPack invisibleCards = new CardPack();

        for (Card card : game.cardpack.cards) {
            if (!(hand.contains(card) || earnedCards.contains(card)
                    || oppEarnedCards.contains(card) || table.contains(card)))
                invisibleCards.addCard(card);
        }

        return invisibleCards;
    }
}
