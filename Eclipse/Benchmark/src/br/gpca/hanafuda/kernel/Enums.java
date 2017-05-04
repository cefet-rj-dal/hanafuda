package br.gpca.hanafuda.kernel;

public class Enums {

    public enum GameStates {
        NotDefined,
        MatchHandTable,
        BuyFromDeck,
        MatchDeckTable,
        DiscardHandCard,
        DiscardDeckCard,
        LeaveOnTable,
    }

    public enum ErrorList {
        Success,
        WrongPlayer,
        WrongAction,
        CardNotFound,
        CardMismatch,
        ZeroSize,
        NullCard,
        NullAction,

    }

    public enum PlayerTypes {
    	Human,
        Greedy,
        RandomPlayer,
        ExpectiMinimaxPlayer;
    }

    public enum NodeType {
        Max,
        Min,
        MaxChance,
        MinChance,
        notSet;
    }

    public enum NodeState {
        Root,
        Match,
        Discard;
    }
}
