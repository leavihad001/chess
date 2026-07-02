package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (getPieceType() == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (getPieceType() == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (getPieceType() == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else {
            return pawnMoves(board, myPosition);
        }
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] posDirections = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

        for (int[] direction : posDirections) {

            int rowChange = direction[0]; //the first number
            int colChange = direction[1]; //the second number

            int currentRow = myPosition.getRow() + rowChange;
            int currentCol = myPosition.getColumn() + colChange;

            while (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPosition = board.getPiece(nextPosition);

                if (pieceAtPosition != null) {
                    //found something
                    if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        //enemy check
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                    //can't go any further
                    break;
                } else {
                    //add possible move to list
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                    //Keep moving
                    currentRow += rowChange;
                    currentCol += colChange;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] posDirections = {{0, 1}, {-1, 0}, {1, 0}, {0, -1}};

        for (int[] direction : posDirections) {

            int rowChange = direction[0]; //the first number
            int colChange = direction[1]; //the second number

            int currentRow = myPosition.getRow() + rowChange;
            int currentCol = myPosition.getColumn() + colChange;

            while (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPosition = board.getPiece(nextPosition);

                if (pieceAtPosition != null) {
                    //found something
                    if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        //enemy check
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                    //can't go any further
                    break;
                } else {
                    //add possible move to list
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                    //Keep moving
                    currentRow += rowChange;
                    currentCol += colChange;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] posDirections = {{0, 1}, {-1, 0}, {1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

        for (int[] direction : posDirections) {

            int rowChange = direction[0]; //the first number
            int colChange = direction[1]; //the second number

            int currentRow = myPosition.getRow() + rowChange;
            int currentCol = myPosition.getColumn() + colChange;

            while (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPosition = board.getPiece(nextPosition);

                if (pieceAtPosition != null) {
                    //found something
                    if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        //enemy check
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                    //can't go any further
                    break;
                } else {
                    //add possible move to list
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                    //Keep moving
                    currentRow += rowChange;
                    currentCol += colChange;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] posDirections = {{0, 1}, {-1, 0}, {1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

        for (int[] direction : posDirections) {

            int rowChange = direction[0]; //the first number
            int colChange = direction[1]; //the second number

            int currentRow = myPosition.getRow() + rowChange;
            int currentCol = myPosition.getColumn() + colChange;

            if (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPosition = board.getPiece(nextPosition);

                if (pieceAtPosition != null) {
                    //found something
                    if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        //enemy check
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                } else {
                    //add possible move to list
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] posDirections = {{2, 1}, {-2, 1}, {2, -1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};

        for (int[] direction : posDirections) {

            int rowChange = direction[0]; //the first number
            int colChange = direction[1]; //the second number

            int currentRow = myPosition.getRow() + rowChange;
            int currentCol = myPosition.getColumn() + colChange;

            if (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
                ChessPosition nextPosition = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPosition = board.getPiece(nextPosition);

                if (pieceAtPosition != null) {
                    //found something
                    if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        //enemy check
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                } else {
                    //add possible move to list
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        //Take color and current position into account
        int startingRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        //int promotingRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int penultimateRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 7 : 2;
        int movingDirection = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1; //positive being up

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean promo = row == penultimateRow;

        //Single move
        ChessPosition nextPosition = new ChessPosition(row + movingDirection, col);
        ChessPiece pieceAtPosition = board.getPiece(nextPosition);

        if (pieceAtPosition == null) {
            pawnNewMoves(moves, myPosition, nextPosition, promo);
        }

        //Double move
        if (row == startingRow){
            ChessPosition doubleNextPosition = new ChessPosition(row + (2*movingDirection), col);
            ChessPiece pieceAtDoublePosition = board.getPiece(doubleNextPosition);
            if (pieceAtPosition == null && pieceAtDoublePosition == null) {
                //square open
                moves.add(new ChessMove(myPosition, doubleNextPosition, null));
            }
        }

        //Capture? (2)
        ChessPosition leftCaptPosition = new ChessPosition(row + movingDirection, col - 1);
        ChessPosition rightCaptPosition = new ChessPosition(row + movingDirection, col + 1);

        //left
        if (col > 1) {
            pieceAtPosition = board.getPiece(leftCaptPosition);
            if (pieceAtPosition != null) {
                if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                    pawnNewMoves(moves, myPosition, leftCaptPosition, promo);
                }
            }
        }

        //right
        if (col < 8) {
            pieceAtPosition = board.getPiece(rightCaptPosition);
            if (pieceAtPosition != null) {
                if (pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                    pawnNewMoves(moves, myPosition, rightCaptPosition, promo);
                }
            }
        }

        return moves;
    }

    private void pawnNewMoves(Collection<ChessMove> currentMoves, ChessPosition myPosition, ChessPosition endPos, boolean promo){
        if (promo){
            currentMoves.add(new ChessMove(myPosition, endPos, PieceType.QUEEN));
            currentMoves.add(new ChessMove(myPosition, endPos, PieceType.ROOK));
            currentMoves.add(new ChessMove(myPosition, endPos, PieceType.KNIGHT));
            currentMoves.add(new ChessMove(myPosition, endPos, PieceType.BISHOP));
        } else {
            currentMoves.add(new ChessMove(myPosition, endPos, null));
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
