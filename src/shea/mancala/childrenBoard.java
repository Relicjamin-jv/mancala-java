package shea.mancala;

public class childrenBoard {
    int move;
    boolean anotherMove;
    int[] board;

    childrenBoard(int move, boolean anotherMove, int[] board){
        this.move = move;
        this.anotherMove = anotherMove;
        this.board = board;
    }

}
