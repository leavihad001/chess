package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            int startingRow = 2;
            int promotingRow = 8;
            int penultimateRow = 7;
            int movingDirection = 1; //positive being up
        } else {
            int startingRow = 7;
            int promotingRow = 1;
            int penultimateRow = 2;
            int movingDirection = -1; //negative being down
        }

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //Single move

        //Double move?

        //Capture? (2)

        //Promotion (Queen, Rook, Bishop, Knight) (if at penultimate and no enemy blocking)


        return moves;
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
