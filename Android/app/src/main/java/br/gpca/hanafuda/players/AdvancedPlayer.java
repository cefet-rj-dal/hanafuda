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
import br.gpca.hanafuda.kernel.Enums.NodeType;
import br.gpca.hanafuda.kernel.GameController;
import br.gpca.hanafuda.kernel.Hand;
import br.gpca.hanafuda.kernel.Player;
import br.gpca.hanafuda.kernel.Table;

public class AdvancedPlayer extends Player {

    private Random rnd = new Random();
    private CardPack invisibleCards = null;
    //private WriteFile writer = new WriteFile("graph.dot", true);
    private static final byte initialDepth = 0;
    private static final byte maximumDepth = 2;

    public AdvancedPlayer(GameController game) {
        super(game);
    }


    @SuppressWarnings("incomplete-switch")
    @Override
    public Action action(GameStates currentState, Card deckCard, Table table, Player opponent) {

        Action action;
        switch (currentState) {
            case MatchHandTable:
                action = convertNodeInAction(getBestNode(NodeType.Max,
                        table, this, opponent, maximumDepth));

                game.matchCards(this, action.getCard1(), action.getCard2());
            case MatchDeckTable:
                Player p = new Player(game);
                this.earnedCards.cloneCards(p.earnedCards);
                p.hand.addCard(deckCard);
                action = convertNodeInAction(getBestNode(NodeType.Max,
                        table, p, opponent, maximumDepth));
                game.matchCards(this, action.getCard1(), action.getCard2());
            case DiscardHandCard:
                action = convertNodeInAction(getBestNode(NodeType.Max,
                        table, this, opponent, (byte) 4));
                game.matchCards(this, action.getCard1(), action.getCard2());
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

        throw new IllegalArgumentException("Minimax returning null #1");
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

                    // Remove as cartas da mesa e adiciona as cartas ganhas
                    clonedPlayer.hand.moveCard(clonedPlayer.earnedCards,
                            handCard);
                    clonedTable.moveCard(clonedPlayer.earnedCards, tableCard);

                    // Chama a funcao recursivamente

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

        // Se nenhuma carta da mao combinar com a da mesa, descartar na mesa
        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < player.hand
                    .getCardsSize(); handCardIndex++) {
                Card handCard = player.hand.getCardByIndex(handCardIndex);

                Player clonedPlayer = player.clone();
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedPlayer.hand.moveCard(clonedTable, handCard);

                // cria um novo filho, define seu tipo, e define o nivel que o
                // no pertence

                Node child = node.createChild(NodeType.Min, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a funcao recursivamente

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

                    // Remove as cartas da mesa e adiciona as cartas ganhas
                    clonedOppPlayer.hand.moveCard(clonedOppPlayer.earnedCards,
                            handCard);
                    clonedTable
                            .moveCard(clonedOppPlayer.earnedCards, tableCard);

                    // Chama a funcao recursivamente

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

                // Chama a funcao recursivamente
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

        throw new IllegalArgumentException("Minimax returning null #2");
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

                    // Remove as cartas da mesa e adiciona as cartas ganhas
                    clonedPlayer.hand.moveCard(clonedPlayer.earnedCards,
                            handCard);
                    clonedTable.moveCard(clonedPlayer.earnedCards, tableCard);

                    // Chama a funcao recursivamente

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

        // Se nenhuma carta da mao combinar com a da mesa, descartar na mesa
        if (!matched) {
            for (byte handCardIndex = 0; handCardIndex < player.hand
                    .getCardsSize(); handCardIndex++) {
                Card handCard = player.hand.getCardByIndex(handCardIndex);

                Player clonedPlayer = player.clone();
                Table clonedTable = new Table();
                table.cloneCards(clonedTable);

                clonedPlayer.hand.moveCard(clonedTable, handCard);

                // cria um novo filho, define seu tipo, e define o nivel que o
                // no pertence

                Node child = node.createChild(NodeType.Min, handCard, null);
                node.setChilds((byte) (node.getChildsNumber() + 1));

                // Chama a funcao recursivamente

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

                    // Remove as cartas da mesa e adiciona as cartas ganhas
                    clonedOppPlayer.hand.moveCard(clonedOppPlayer.earnedCards,
                            handCard);
                    clonedTable
                            .moveCard(clonedOppPlayer.earnedCards, tableCard);

                    // Chama a funcao recursivamente

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

                // Chama a funcao recursivamente
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

        // Insere os scores no vetor vetAux pra cada combinacao com a mesa
        // Depois desfaz a alteracao
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