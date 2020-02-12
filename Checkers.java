import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

// Checkers Board
public class Checkers {

    // class definitions
    static int maxJump;
    double maxVal;
    static double alpha;
    static double beta;
    char[][] board;
    char turn;
    int size;
    ArrayList<Piece> whitePieces;
    ArrayList<Piece> blackPieces;
    LinkedList<Checkers> childStates;

    // internl space class
    static class Space {
        Piece piece = null;
    }

    // internal location class
    static class Location {
        int x;
        int y;

        // location class constructor
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // returns true if locations are the same
        public boolean compareTo(Location a, Location b) {
            if (a.x == b.x && a.y == b.y) {
                return true;
            } else {
                return false;
            }
        }
    }

    // internal Piece class that keeps track of color, location, and if king
    static class Piece {
        char color;
        int y;
        int x;
        boolean king;

        // contructor for Piece
        public Piece(char color, int y, int x, boolean king) {
            this.color = color;
            this.y = y;
            this.x = x;
            this.king = king;
        }
    }

    // constructor that sets up board
    public Checkers(int size, char turn) {
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.board = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = ' ';
            }
        }
        boolean put_piece = false;
        for (int y = 0; y < size / 2 - 1; y++) {
            for (int x = 0; x < size; x++) {
                if (put_piece) {
                    this.board[y][x] = 'w';
                    whitePieces.add(new Piece('w', y, x, false));
                }
                put_piece = !put_piece;
            }
            put_piece = !put_piece;
        }
        put_piece = true;
        for (int y = size / 2 + 1; y < size; y++) {

            for (int x = 0; x < size; x++) {
                if (put_piece) {
                    this.board[y][x] = 'b';
                    blackPieces.add(new Piece('b', y, x, false));
                }
                put_piece = !put_piece;
            }
            put_piece = !put_piece;
        }
        this.turn = turn;
        this.size = size;
        this.childStates = new LinkedList<Checkers>();
    }

    // constructor
    public Checkers() {
    }

    // returns available pieces with given state and turn
    public static ArrayList<Piece> getPieces(Checkers state, char turn) {
        if (turn == 'b') {
            return state.blackPieces;
        } else {
            return state.whitePieces;
        }
    }

    // print methods to check the board states
    public void printBoard() {

        for (int i = 0; i < this.size; i++) {
            if (i == 0) {
                System.out.print("  ");
                for (int k = 0; k < this.size; k++) {
                    System.out.print(k + " ");
                }
                System.out.println();
            }
            for (int j = 0; j < this.size; j++) {
                if (j == 0) {
                    if (i == 0) {
                        System.out.print("A ");
                    } else if (i == 1) {
                        System.out.print("B ");
                    } else if (i == 2) {
                        System.out.print("C ");
                    } else if (i == 3) {
                        System.out.print("D ");
                    } else if (i == 4) {
                        System.out.print("E ");
                    } else if (i == 5) {
                        System.out.print("F ");
                    } else if (i == 6) {
                        System.out.print("G ");
                    } else if (i == 7) {
                        System.out.print("H ");
                    }
                }
                if (this.board[i][j] != ' ') {
                    System.out.print(this.board[i][j] + "|");
                } else
                    System.out.print(" |");
            }
            System.out.println();
        }
        System.out.println("------------");
    }

    // prints children with given state
    public void printChildren() {
        if (!this.childStates.isEmpty()) {
            for (int i = 0; i < this.childStates.size(); i++) {
                this.childStates.get(i).printChildren();
            }
            System.out.println(this.maxVal);
            this.printBoard();
        } else {
            System.out.println(this.maxVal);
            this.printBoard();
        }
    }

    // play game that will run until a terminal state is reached. Works by checking
    // to see if the board
    // is in a final state, if it isnt, we get the configuration of possible new
    // states. Then, on each of those
    // we call this recursive method again. Will print out the loser of the game
    // which is determined
    // by whoever has no available moves
    public Checkers playGame(int turnCount, Checkers currentState) {
        if (currentState.turn == 'b') {
            currentState.getStates(turnCount);
            // currentState.maxValue(currentState);
            for (int i = 0; i < currentState.childStates.size(); i++) {
                if (currentState.childStates.get(i).maxVal == currentState.maxVal) {
                    currentState = currentState.childStates.get(i);
                    break;
                }
            }
            return currentState;
        } else {
            System.out.println("That was an invalid move! Try again.");
            return currentState;
        }
    }

    // creates state space
    public void getStates(int turnCount) {
        if (turnCount <= 10) {
            if (!this.terminalTest()) {
                if (this.turn == 'b') {
                    this.getBlackMoves();
                } else {
                    this.getWhiteMoves();
                }
                turnCount++;
                for (int i = 0; i < this.childStates.size(); i++) {
                    this.childStates.get(i).getStates(turnCount);
                }
            } else {
                // System.out.println(this.turn + " loses!");
            }
        }
    }

    // method that iterates through the board and if it finds a black piece, will
    // conduct all the possible moves for that piece
    public void getBlackMoves() {
        int jumped = 0;
        ArrayList<Piece> pieces = this.blackPieces;
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).king) {
                Checkers start = this;
                int counter = 0;
                maxJump = 0;
                if (this.validKingJump(pieces.get(i).y, pieces.get(i).x)) {
                    jumped = 1;
                    this.kingJump(pieces.get(i).y, pieces.get(i).x, counter, start, true);
                }
            }
            if (!pieces.get(i).king) {
                Checkers start = this;
                int counter = 0;
                maxJump = 0;
                if (this.validJumpUp(pieces.get(i).y, pieces.get(i).x)) {
                    jumped = 1;
                    this.jumpUp(pieces.get(i).y, pieces.get(i).x, counter, start, false);
                }
            }
            if (!pieces.get(i).king && jumped == 0) {
                this.upMove(pieces.get(i).y, pieces.get(i).x);
            }
            if (pieces.get(i).king && jumped == 0) {
                this.kingMove(pieces.get(i).y, pieces.get(i).x);
            }
        }
    }

    // same as above, but for white pieces
    public void getWhiteMoves() {
        int jumped = 0;
        ArrayList<Piece> pieces = this.whitePieces;
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).king) {
                Checkers start = this;
                int counter = 0;
                maxJump = 0;
                if (this.validKingJump(pieces.get(i).y, pieces.get(i).x)) {
                    jumped = 1;
                    this.kingJump(pieces.get(i).y, pieces.get(i).x, counter, start, true);
                }
            }
            if (!pieces.get(i).king) {
                Checkers start = this;
                int counter = 0;
                maxJump = 0;
                if (this.validJumpDown(pieces.get(i).y, pieces.get(i).x)) {
                    jumped = 1;
                    this.jumpDown(pieces.get(i).y, pieces.get(i).x, counter, start, false);
                }
            }
            if (!pieces.get(i).king && jumped == 0) {
                this.downMove(pieces.get(i).y, pieces.get(i).x);
            }
            if (pieces.get(i).king && jumped == 0) {
                this.kingMove(pieces.get(i).y, pieces.get(i).x);
            }
        }
    }

    // method that iterates through the board and tests for valid moves. This uses
    // helper methods described below
    public boolean terminalTest() {
        ArrayList<Piece> pieces = null;
        if (this.turn == 'b') {
            pieces = this.blackPieces;
            for (Piece p : pieces) {
                if (p.king) {
                    if (this.validKingJump(p.y, p.x)) {
                        return false;
                    }
                    if (this.validKingMove(p.y, p.x)) {
                        return false;
                    }
                } else if (!p.king) {
                    if (this.validJumpUp(p.y, p.x)) {
                        return false;
                    }
                    if (this.validUpMove(p.y, p.x)) {
                        return false;
                    }
                }
            }
        } else {
            pieces = this.whitePieces;
            for (Piece p : pieces) {
                if (p.king) {
                    if (this.validKingJump(p.y, p.x)) {
                        return false;
                    }
                    if (this.validKingMove(p.y, p.x)) {
                        return false;
                    }
                } else if (!p.king) {
                    if (this.validJumpDown(p.y, p.x)) {
                        return false;
                    }
                    if (this.validDownMove(p.y, p.x)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // check if can capture any direction
    public boolean validKingJump(int i, int j) {
        if (this.validJumpDown(i, j) || this.validJumpUp(i, j)) {
            return true;
        }
        return false;
    }

    // checks if can capture north
    public boolean validJumpUp(int i, int j) {
        if (this.validJumpUpLeft(i, j) || this.validJumpUpRight(i, j)) {
            return true;
        }
        return false;
    }

    // checks if can capture south
    public boolean validJumpDown(int i, int j) {
        if (this.validJumpDownLeft(i, j) || this.validJumpDownRight(i, j)) {
            return true;
        }
        return false;
    }

    // checks if can capture northeast
    public boolean validJumpUpRight(int i, int j) {
        if (i - 2 >= 0 && j + 2 < this.size && this.board[i - 2][j + 2] == ' ' && this.board[i - 1][j + 1] != ' '
                && Character.toUpperCase(this.board[i][j]) != Character.toUpperCase(this.board[i - 1][j + 1])) {
            return true;
        }
        return false;
    }

    // checks if can capture northwest
    public boolean validJumpUpLeft(int i, int j) {
        if (i - 2 >= 0 && j - 2 >= 0) {
            if (this.board[i - 2][j - 2] == ' ' && this.board[i - 1][j - 1] != ' ') {
                if (Character.toUpperCase(this.board[i - 1][j - 1]) != Character.toUpperCase(this.board[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    // checks if can capture southwest
    public boolean validJumpDownLeft(int i, int j) {
        if (i + 2 < this.size && j - 2 >= 0) {
            if (this.board[i + 1][j - 1] != ' ' && this.board[i + 2][j - 2] == ' ') {
                if (Character.toUpperCase(this.board[i][j]) != Character.toUpperCase(this.board[i + 1][j - 1])) {
                    return true;
                }
            }
        }
        return false;
    }

    // checks if can capture southeast
    public boolean validJumpDownRight(int i, int j) {
        if (i + 2 < this.size && j + 2 < this.size) {
            if (this.board[i + 2][j + 2] == ' ' && this.board[i + 1][j + 1] != ' ' && this.board[i][j] != ' ') {
                if (Character.toUpperCase(this.board[i][j]) != Character.toUpperCase(this.board[i + 1][j + 1])) {
                    return true;
                }
            }
        }
        return false;
    }

    // checks up
    public boolean validUpMove(int i, int j) {
        if (this.upValidLeft(i, j) || this.upValidRight(i, j)) {
            return true;
        }
        return false;
    }

    // checks to see if theres a spot on the board thats open up and to the right
    public boolean upValidRight(int i, int j) {
        if (i - 1 >= 0 && j + 1 < this.size) {
            if (this.board[i - 1][j + 1] == ' ') {
                return true;
            }
        }
        return false;
    }

    // checks up and to the left for black
    public boolean upValidLeft(int i, int j) {
        if (i - 1 >= 0 && j - 1 >= 0) {
            if (this.board[i - 1][j - 1] == ' ') {
                return true;
            }
        }
        return false;
    }

    // checks down
    public boolean validDownMove(int i, int j) {
        if (this.downValidLeft(i, j) || this.downValidRight(i, j)) {
            return true;
        }
        return false;
    }

    // checks down and to the right for white
    public boolean downValidRight(int i, int j) {
        if (i + 1 < this.size && j + 1 < this.size) {
            if (this.board[i + 1][j + 1] == ' ') {
                return true;
            }
        }
        return false;
    }

    // checks down and to the left
    public boolean downValidLeft(int i, int j) {
        if (i + 1 < this.size && j - 1 >= 0) {
            if (this.board[i + 1][j - 1] == ' ') {
                return true;
            }
        }
        return false;
    }

    // checks if piece can move any direction
    public boolean validKingMove(int i, int j) {
        if (this.validDownMove(i, j) || this.validUpMove(i, j)) {
            return true;
        }
        return false;
    }

    // method to call both of blacks move at a given x y location
    public void upMove(int y, int x) {
        this.moveUpAndLeft(y, x);
        this.moveUpAndRight(y, x);
    }

    // method to call both of whites move at a location
    public void downMove(int y, int x) {
        this.moveDownAndLeft(y, x);
        this.moveDownAndRight(y, x);
    }

    // moves king in all directions
    public void kingMove(int y, int x) {
        this.moveDownAndLeft(y, x);
        this.moveDownAndRight(y, x);
        this.moveUpAndLeft(y, x);
        this.moveUpAndRight(y, x);
    }

    // updates location of pieces
    public void updatePieces(int oldY, int oldX, int newY, int newX) {
        if (this.turn == 'b') {
            this.movePiece(this.blackPieces, oldY, oldX, newY, newX);
        } else {
            this.movePiece(this.whitePieces, oldY, oldX, newY, newX);
        }
    }

    // removes pieces from arraylist
    public void removePieces(int y, int x) {
        if (this.turn == 'b') {
            this.deletePiece(this.whitePieces, y, x);
        } else {
            this.deletePiece(this.blackPieces, y, x);
        }
    }

    // updates location of piece with given location
    public void movePiece(ArrayList<Piece> pieces, int oldY, int oldX, int newY, int newX) {
        for (Piece p : pieces) {
            if (p.y == oldY && p.x == oldX) {
                p.y = newY;
                p.x = newX;
            }
        }
    }

    // removes piece from arraylist
    public void deletePiece(ArrayList<Piece> pieces, int y, int x) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).y == y && pieces.get(i).x == x) {
                pieces.remove(i);
            }
        }
    }

    // finds piece at given location
    public Piece findPiece(int y, int x) {
        if (this.turn == 'b') {
            return locateP(this.blackPieces, y, x);
        } else {
            return locateP(this.whitePieces, y, x);
        }
    }

    // returns piece at given location
    public Piece locateP(ArrayList<Piece> pieces, int y, int x) {
        for (Piece p : pieces) {
            if (p.y == y && p.x == x) {
                return p;
            }
        }
        return null;
    }

    // moves a piece up and to the right, and changes the turn
    public void moveUpAndRight(int y, int x) {
        if (upValidRight(y, x)) {
            Checkers nextState = copyState();
            nextState.board[y - 1][x + 1] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.updatePieces(y, x, y - 1, x + 1);
            if (nextState.kingMe(nextState.findPiece(y - 1, x + 1))) {
                nextState.board[y - 1][x + 1] = Character.toUpperCase(nextState.board[y - 1][x + 1]);
            }
            nextState.changeTurn();
            this.childStates.add(nextState);
        }
    }

    // moves a piece up and to the left, and changes the turn
    public void moveUpAndLeft(int y, int x) {
        if (upValidLeft(y, x)) {
            Checkers nextState = copyState();
            nextState.board[y - 1][x - 1] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.updatePieces(y, x, y - 1, x - 1);
            if (nextState.kingMe(nextState.findPiece(y - 1, x - 1))) {
                nextState.board[y - 1][x - 1] = Character.toUpperCase(nextState.board[y - 1][x - 1]);
            }
            nextState.changeTurn();
            this.childStates.add(nextState);
        }
    }

    // moves down board and right
    public void moveDownAndRight(int y, int x) {
        if (downValidRight(y, x)) {
            Checkers nextState = copyState();
            nextState.board[y + 1][x + 1] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.updatePieces(y, x, y + 1, x + 1);
            if (nextState.kingMe(nextState.findPiece(y + 1, x + 1))) {
                nextState.board[y + 1][x + 1] = Character.toUpperCase(nextState.board[y + 1][x + 1]);
            }
            nextState.changeTurn();
            this.childStates.add(nextState);
        }
    }

    // moves down the board and to the left
    public void moveDownAndLeft(int y, int x) {
        if (downValidLeft(y, x)) {
            Checkers nextState = copyState();
            nextState.board[y + 1][x - 1] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.updatePieces(y, x, y + 1, x - 1);
            if (nextState.kingMe(nextState.findPiece(y + 1, x - 1))) {
                nextState.board[y + 1][x - 1] = Character.toUpperCase(nextState.board[y + 1][x - 1]);
            }
            nextState.changeTurn();
            this.childStates.add(nextState);
        }
    }

    // captures piece in any direction
    public void kingJump(int y, int x, int counter, Checkers start, boolean king) {
        this.jumpUp(y, x, counter, start, king);
        this.jumpDown(y, x, counter, start, king);
    }

    // captures piece north
    public void jumpUp(int y, int x, int counter, Checkers start, boolean king) {
        this.jumpUpLeft(y, x, counter, start, king);
        this.jumpUpRight(y, x, counter, start, king);
    }

    // captures piece south
    public void jumpDown(int y, int x, int counter, Checkers start, boolean king) {
        this.jumpDownLeft(y, x, counter, start, king);
        this.jumpDownRight(y, x, counter, start, king);
    }

    // captures piece that is northeast
    public void jumpUpRight(int y, int x, int counter, Checkers start, boolean king) {
        if (validJumpUpRight(y, x)) {
            counter++;
            Checkers nextState = copyState();
            // updates current board
            nextState.board[y - 2][x + 2] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.board[y - 1][x + 1] = ' ';
            nextState.removePieces(y - 1, x + 1);
            nextState.updatePieces(y, x, y - 2, x + 2);
            if (nextState.kingMe(nextState.findPiece(y - 2, x + 2))) {
                nextState.board[y - 2][x + 2] = Character.toUpperCase(nextState.board[y - 2][x + 2]);
            }
            if (king) {
                if (nextState.validKingJump(y - 2, x + 2)) {
                    nextState.jumpDown(y - 2, x + 2, counter, start, king);
                    nextState.jumpUp(y - 2, x + 2, counter, start, king);
                }
            } else if (nextState.validJumpUp(y - 2, x + 2)) {
                nextState.jumpUp(y - 2, x + 2, counter, start, king);
            }
            // using recursion to keep track of multi captures
            if (counter > maxJump) {
                maxJump = counter;
                start.childStates.clear();
                nextState.turn = start.turn;
                nextState.changeTurn();
                start.childStates.add(nextState);
            }
        }
    }

    // captures piece that is northwest
    public void jumpUpLeft(int y, int x, int counter, Checkers start, boolean king) {
        if (validJumpUpLeft(y, x)) {
            counter++;
            Checkers nextState = copyState();
            nextState.board[y - 2][x - 2] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.board[y - 1][x - 1] = ' ';
            nextState.removePieces(y - 1, x - 1);
            nextState.updatePieces(y, x, y - 2, x - 2);
            if (nextState.kingMe(nextState.findPiece(y - 2, x - 2))) {
                nextState.board[y - 2][x - 2] = Character.toUpperCase(nextState.board[y - 2][x - 2]);
            }
            if (king) {
                if (nextState.validKingJump(y - 2, x - 2)) {
                    nextState.jumpDown(y - 2, x - 2, counter, start, king);
                    nextState.jumpUp(y - 2, x - 2, counter, start, king);
                }
            } else if (nextState.validJumpUp(y - 2, x - 2)) {
                nextState.jumpUp(y - 2, x - 2, counter, start, king);
            }
            if (counter > maxJump) {
                maxJump = counter;
                start.childStates.clear();
                nextState.turn = start.turn;
                nextState.changeTurn();
                start.childStates.add(nextState);
            }
        }
    }

    // captures pieces that is southwest
    public void jumpDownLeft(int y, int x, int counter, Checkers start, boolean king) {
        if (this.validJumpDownLeft(y, x)) {
            counter++;
            Checkers nextState = copyState();
            nextState.board[y + 2][x - 2] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.board[y + 1][x - 1] = ' ';
            nextState.removePieces(y + 1, x - 1);
            nextState.updatePieces(y, x, y + 2, x - 2);
            if (nextState.kingMe(nextState.findPiece(y + 2, x - 2))) {
                nextState.board[y + 2][x - 2] = Character.toUpperCase(nextState.board[y + 2][x - 2]);
            }
            if (king) {
                if (nextState.validKingJump(y + 2, x - 2)) {
                    nextState.jumpDown(y + 2, x - 2, counter, start, king);
                    nextState.jumpUp(y + 2, x - 2, counter, start, king);
                }
            } else if (nextState.validJumpDown(y + 2, x - 2)) {
                nextState.jumpDown(y + 2, x - 2, counter, start, king);
            }
            if (counter > maxJump) {
                maxJump = counter;
                start.childStates.clear();
                nextState.turn = start.turn;
                nextState.changeTurn();
                start.childStates.add(nextState);
            }
        }
    }

    // captures piece that is southeast
    public void jumpDownRight(int y, int x, int counter, Checkers start, boolean king) {
        if (this.validJumpDownRight(y, x)) {
            counter++;
            Checkers nextState = copyState();
            nextState.board[y + 2][x + 2] = nextState.board[y][x];
            nextState.board[y][x] = ' ';
            nextState.board[y + 1][x + 1] = ' ';
            nextState.removePieces(y + 1, x + 1);
            nextState.updatePieces(y, x, y + 2, x + 2);
            if (nextState.kingMe(nextState.findPiece(y + 2, x + 2))) {
                nextState.board[y + 2][x + 2] = Character.toUpperCase(nextState.board[y + 2][x + 2]);
            }
            if (king) {
                if (nextState.validKingJump(y + 2, x + 2)) {
                    nextState.jumpDown(y + 2, x + 2, counter, start, king);
                    nextState.jumpUp(y + 2, x + 2, counter, start, king);
                }
            } else if (nextState.validJumpDown(y + 2, x + 2)) {
                nextState.jumpDown(y + 2, x + 2, counter, start, king);
            }
            if (counter > maxJump) {
                maxJump = counter;
                start.childStates.clear();
                nextState.turn = start.turn;
                nextState.changeTurn();
                start.childStates.add(nextState);
            }
        }
    }

    // methods to copy the board over
    public Checkers copyState() {
        Checkers ret = new Checkers(this.size, this.turn);
        ret.board = copyBoard();
        ret.blackPieces = this.copyOf(this.blackPieces);
        ret.whitePieces = this.copyOf(this.whitePieces);
        return ret;
    }

    // copies available pieces
    public ArrayList<Piece> copyOf(ArrayList<Piece> pieces) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece p : pieces) {
            ret.add(new Piece(p.color, p.y, p.x, p.king));
        }
        return ret;
    }

    // returns a new copied object of the board
    public char[][] copyBoard() {
        char[][] ret = new char[this.size][this.size];
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                ret[i][j] = this.board[i][j];
            }
        }
        return ret;
    }

    // changes turn
    public void changeTurn() {
        if (this.turn == 'b') {
            this.turn = 'w';
        } else {
            this.turn = 'b';
        }
    }

    // prints location and values of piece
    public void printPiece(Piece p) {
        System.out.println("Color: " + p.color + " x val: " + p.x + " y val: " + p.y + " kinged: " + p.king);
    }

    // kings piece if at the end of the board
    public boolean kingMe(Piece p) {
        if (p.y == 0 || p.y == this.size - 1) {
            p.king = true;
            p.color = Character.toUpperCase(p.color);
            return true;
        }
        return false;
    }

    // method useful for testing 1 possible line of movements of the board.
    public void pursueLeftPath(Checkers e) {
        if (e.childStates.size() > 0) {
            e.printBoard();
            e.pursueLeftPath(e.childStates.get(0));
        } else {
            e.printBoard();
        }
    }

    // minimax algorithim
    public Checkers controller(Checkers test, int turncount) {
        int temp = Integer.MIN_VALUE;
        Checkers board = new Checkers();
        board = test;
        if (test.turn == 'b') {
            maxValue(test, turncount);
            for (Checkers state : test.childStates) {
                if (state.maxVal > temp) {
                    board = state;
                }
            }
        }
        return board;
    }

    // alpha beta pruning method
    public Checkers ABcontroller(Checkers test, int turncount) {
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        int temp = Integer.MIN_VALUE;
        Checkers board = new Checkers();
        board = test;
        if (test.turn == 'b') {
            maxValue(test, turncount);
            for (Checkers state : test.childStates) {
                if (state.maxVal > temp) {
                    board = state;
                }
            }
        }
        return board;
    }

    // keep track of a optimal move for board
    public void maxValue(Checkers state, int turncount) {
        state.maxVal = Integer.MIN_VALUE;
        if (state.terminalTest()) {
            if (state.turn == 'b') {
                state.maxVal = Integer.MIN_VALUE;
            } else {
                state.maxVal = Integer.MAX_VALUE;
            }
        } else if (turncount == 1) {
            state.maxVal = heuristic(state);
        } else {
            state.getBlackMoves();
            turncount--;
            for (int i = 0; i < state.childStates.size(); i++) {
                minValue(state.childStates.get(i), turncount);
                state.maxVal = max(state.maxVal, state.childStates.get(i).maxVal);
            }
        }
    }

    // keep track of a optimal move for board
    public void minValue(Checkers state, int turncount) {
        state.maxVal = Integer.MAX_VALUE;
        if (state.terminalTest()) {
            if (state.turn == 'w') {
                state.maxVal = Integer.MAX_VALUE;
            } else {
                state.maxVal = Integer.MIN_VALUE;
            }
        } else if (turncount == 1) {
            state.maxVal = heuristic(state);
        } else {
            state.getWhiteMoves();
            turncount--;
            for (int i = 0; i < state.childStates.size(); i++) {
                maxValue(state.childStates.get(i), turncount);
                state.maxVal = min(state.maxVal, state.childStates.get(i).maxVal);
            }
        }
    }

    // keep track of a best board
    public void ABmaxValue(Checkers state, int turncount) {
        state.maxVal = Integer.MIN_VALUE;
        if (state.terminalTest()) {
            if (state.turn == 'b') {
                state.maxVal = Integer.MIN_VALUE;
            } else {
                state.maxVal = Integer.MAX_VALUE;
            }
        } else if (turncount == 1) {
            state.maxVal = heuristic(state);
        } else {
            state.getBlackMoves();
            turncount--;
            for (int i = 0; i < state.childStates.size(); i++) {
                minValue(state.childStates.get(i), turncount);
                state.maxVal = max(state.maxVal, state.childStates.get(i).maxVal);
                if (state.maxVal >= beta) {
                    return;
                }
                alpha = max(state.maxVal, alpha);
            }
        }
    }

    // keep track of a optimal move for board
    public void ABminValue(Checkers state, int turncount) {
        state.maxVal = Integer.MAX_VALUE;
        if (state.terminalTest()) {
            if (state.turn == 'w') {
                state.maxVal = Integer.MAX_VALUE;
            } else {
                state.maxVal = Integer.MIN_VALUE;
            }
        } else if (turncount == 1) {
            state.maxVal = heuristic(state);
        } else {
            state.getWhiteMoves();
            turncount--;
            for (int i = 0; i < state.childStates.size(); i++) {
                maxValue(state.childStates.get(i), turncount);
                state.maxVal = min(state.maxVal, state.childStates.get(i).maxVal);
                if (state.maxVal <= alpha) {
                    return;
                }
                beta = min(state.maxVal, beta);
            }
        }
    }

    // returns the minimum value between two numbers
    public double min(double a, double b) {
        if (b < a) {
            return b;
        } else {
            return a;
        }
    }

    // returns the maximum value between two numbers
    public double max(double a, double b) {
        if (b > a) {
            return b;
        } else {
            return a;
        }
    }

    // changes letters to numbers
    // used for reading user input
    public int LtoN(char letter) {
        letter = Character.toUpperCase(letter);
        if (letter == 'A') {
            return 0;
        } else if (letter == 'B') {
            return 1;
        } else if (letter == 'C') {
            return 2;
        } else if (letter == 'D') {
            return 3;
        } else if (letter == 'E') {
            return 4;
        } else if (letter == 'F') {
            return 5;
        } else if (letter == 'G') {
            return 6;
        } else if (letter == 'H') {
            return 7;
        } else {
            return -1;
        }
    }

    // method that reads users input and moves pieces accordingly
    public Checkers takeInput(String in, Checkers start) {
        int track1 = 0;
        int track2 = 1;
        char[] input = in.toCharArray();
        int y1, y2, x1, x2;
        y1 = LtoN(input[0]);
        x1 = Character.getNumericValue(input[1]);
        // checks for regular moves
        if (input[2] == '-') {
            y2 = LtoN(input[track1 + 3]);
            x2 = Character.getNumericValue(input[track2 + 3]);
            if (start.board[y1][x1] != ' ' && start.board[y2][x2] == ' ') {
                // reads users input and checks where user is trying to move and whether it is a
                // valid move
                if (y2 - y1 > 0 && x2 - x1 > 0) {
                    if (downValidRight(y1, x1)) {
                        Checkers nextState = start.copyState();
                        nextState.board[y2][x2] = nextState.board[y1][x1];
                        nextState.board[y1][x1] = ' ';
                        nextState.updatePieces(y1, x1, y2, x2);
                        nextState.kingMe(nextState.findPiece(y2, x2));
                        nextState.changeTurn();
                        return nextState;
                    } else {
                        System.out.println("invalid move");
                        return start;
                    }
                } else if (y2 - y1 > 0 && x2 - x1 < 0) {
                    if (downValidLeft(y1, x1)) {
                        Checkers nextState = start.copyState();
                        nextState.board[y2][x2] = nextState.board[y1][x1];
                        nextState.board[y1][x1] = ' ';
                        nextState.updatePieces(y1, x1, y2, x2);
                        if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                            nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                        }
                        nextState.changeTurn();
                        return nextState;
                    } else {
                        System.out.println("invalid move");
                        return start;
                    }
                } else if (y2 - y1 < 0 && x2 - x1 > 0) {
                    if (upValidRight(y1, x1)) {
                        Checkers nextState = start.copyState();
                        nextState.board[y2][x2] = nextState.board[y1][x1];
                        nextState.board[y1][x1] = ' ';
                        nextState.updatePieces(y1, x1, y2, x2);
                        if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                            nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                        }
                        nextState.changeTurn();
                        return nextState;
                    } else {
                        System.out.println("invalid move");
                        return start;
                    }
                } else if (y2 - y1 < 0 && x2 - x1 < 0) {
                    if (upValidLeft(y1, x1)) {
                        Checkers nextState = start.copyState();
                        nextState.board[y2][x2] = nextState.board[y1][x1];
                        nextState.board[y1][x1] = ' ';
                        nextState.updatePieces(y1, x1, y2, x2);
                        if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                            nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                        }
                        nextState.changeTurn();
                        return nextState;
                    } else {
                        System.out.println("invalid move");
                        return start;
                    }
                } else {
                    System.out.println("invalid move");
                    return start;
                }
            } else {
                System.out.println("invalid move");
                return start;
            }
            // allows for user's multiple jumps
        } else {
            Checkers nextState = start.copyState();
            do {
                y2 = LtoN(input[track1 + 3]);
                x2 = Character.getNumericValue(input[track2 + 3]);
                if (start.board[y1][x1] != ' ' && start.board[y2][x2] == ' ') {
                    if (y2 - y1 > 0 && x2 - x1 > 0) {
                        if (nextState.validJumpDownRight(y1, x1)) {
                            nextState.board[y2][x2] = nextState.board[y1][x1];
                            nextState.board[y1][x1] = ' ';
                            nextState.board[y1 + 1][x1 + 1] = ' ';
                            nextState.removePieces(y1 + 1, x1 + 1);
                            nextState.updatePieces(y1, x1, y2, x2);
                            if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                                nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                            }
                        } else {
                            System.out.println("invalid move");
                            return start;
                        }
                    } else if (y2 - y1 > 0 && x2 - x1 < 0) {
                        if (nextState.validJumpDownLeft(y1, x1)) {
                            nextState.board[y2][x2] = nextState.board[y1][x1];
                            nextState.board[y1][x1] = ' ';
                            nextState.board[y1 + 1][x1 - 1] = ' ';
                            nextState.removePieces(y1 + 1, x1 - 1);
                            nextState.updatePieces(y1, x1, y2, x2);
                            if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                                nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                            }
                        } else {
                            System.out.println("invalid move");
                            return start;
                        }
                    } else if (y2 - y1 < 0 && x2 - x1 > 0) {
                        if (nextState.validJumpUpRight(y1, x1)) {
                            nextState.board[y2][x2] = nextState.board[y1][x1];
                            nextState.board[y1][x1] = ' ';
                            nextState.board[y1 - 1][x1 + 1] = ' ';
                            nextState.removePieces(y1 - 1, x1 + 1);
                            nextState.updatePieces(y1, x1, y2, x2);
                            if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                                nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                            }
                        } else {
                            System.out.println("invalid move");
                            return start;
                        }
                    } else {
                        if (nextState.validJumpUpLeft(y1, x1)) {
                            nextState.board[y2][x2] = nextState.board[y1][x1];
                            nextState.board[y1][x1] = ' ';
                            nextState.board[y1 - 1][x1 - 1] = ' ';
                            nextState.removePieces(y1 - 1, x1 - 1);
                            nextState.updatePieces(y1, x1, y2, x2);
                            if (nextState.kingMe(nextState.findPiece(y2, x2))) {
                                nextState.board[y2][x2] = Character.toUpperCase(nextState.board[y2][x2]);
                            }
                        } else {
                            System.out.println("invalid move");
                            return start;
                        }
                    }
                } else {
                    System.out.println("invalid move");
                    return start;
                }
                track1 = track1 + 3;
                track2 = track2 + 3;
                y1 = y2;
                x1 = x2;
            } while (track2 < input.length - 1);
            nextState.changeTurn();
            return nextState;
        }
    }

    // changes turn
    public static char reverseTurn(char a) {
        if (a == 'b') {
            return 'w';
        } else {
            return 'b';
        }
    }

    // prints location and color of available pieces
    public void printArrayList(ArrayList<Piece> pieces) {
        for (Piece p : pieces) {
            System.out.println("color: " + p.color + " row: " + p.y + " column: " + p.x);
        }
    }

    // Heuristic function which values computes own pieces
    // It weights kings higher than regular pieces
    public static double heuristic(Checkers state) {

        if (state.terminalTest() && (state.turn == 'w')) {
            return Integer.MAX_VALUE;
        }
        if (state.terminalTest() && (state.turn == 'b')) {
            return Integer.MIN_VALUE;
        }

        ArrayList<Piece> ourPieces = getPieces(state, state.turn);
        ArrayList<Piece> enemPieces = getPieces(state, reverseTurn(state.turn));
        // ArrayList<Location> enemyP = getPieces(state, reverseTurn(state.turn));
        // Heuristic. Uses a defensive strategy of keeping pieces close together to
        // avoid bad captures.
        double score = 0;
        int enemVal = 180;
        int pval = 0;
        for (Piece p : ourPieces) {
            if (p.king) {
                pval += 30;
            }
            pval += 20;
        }
        for (Piece p : enemPieces) {
            if (p.king) {
                enemVal -= 15;
            }
            enemVal -= 10;
        }
        int scoreMod = 1;
        if (state.turn == 'b') {
            scoreMod = 1;
        } else {
            scoreMod = -1;
        }
        // System.out.println("dist value "+ Math.pow(pieces.size(),2)/distance+ " piece
        // val: "+ pval);
        score += pval;
        score += enemVal;
        score *= scoreMod;
        // score += Math.pow(pieces.size(),2)/distance;
        return score;

    }

    // starts game
    public static void runGame(int size, int depth, int ABorNot) {
        Checkers test = new Checkers(size, 'b');
        Scanner inGame = new Scanner(System.in);
        int turn = 0;
        while (!test.terminalTest() && turn < 60) {
            test.printBoard();
            if (ABorNot == 1) {
                test = test.ABcontroller(test, depth);
            } else {
                test = test.controller(test, depth);
            }
            test.printBoard();
            if (test.terminalTest()) {
                return;
            }
            System.out.println(test.turn + " to play.");
            System.out.println("Enter Move:");
            String input = inGame.nextLine();
            test = test.takeInput(input, test);
            turn++;
        }
        inGame.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, and welcome to the checkers program.");
        System.out.println("Please enter a size for your checkers board");
        int size = scanner.nextInt();
        System.out.println(
                "Please enter a depth for the heuristic function (anything above 9 moves deep will begin to take a long time)");
        int depth = scanner.nextInt();
        System.out.println(
                "Enter a 1 if you would like to use Alpha beta pruning, enter a 0 if you would like to not use it.");
        int ABorNot = scanner.nextInt();
        runGame(size, depth, ABorNot);
        scanner.close();
    }
}