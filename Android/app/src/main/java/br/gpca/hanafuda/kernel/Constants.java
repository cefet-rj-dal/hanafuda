package br.gpca.hanafuda.kernel;

public class Constants {

    public enum ErrorList {
        Success,
        WrongPlayer,
        WrongAction,
        CardMismatch,
        NullCard;
    }

    public enum PlayerTypes {
        Human,
        Greedy,
        RandomPlayer,
        ExpectiMinimaxPlayer,
        ShikoGreedyPlayer,
        NanatanGreedyPlayer,
        ShikoNanatanGreedyPlayer;
    }


}
