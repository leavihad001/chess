package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeamTurn;
    private ChessBoard board;
    private ChessMove lastMove;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteKingsideRookMoved = false;
    private boolean whiteQueensideRookMoved = false;
    private boolean blackKingsideRookMoved = false;
    private boolean blackQueensideRookMoved = false;


    public ChessGame() {
        this.currentTeamTurn = TeamColor.WHITE; //The default starting team
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);

        if (currentPiece == null) {
            return null;
        }

        Collection<ChessMove> allMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove moves : allMoves) {
            ChessPiece pieceCapture = board.getPiece(moves.getEndPosition());

            //add a fake piece (make the move)
            board.addPiece(moves.getEndPosition(), currentPiece);
            board.addPiece(moves.getStartPosition(), null);

            //check the validity
            if (!isInCheck(currentPiece.getTeamColor())) {
                validMoves.add(moves);
            }
            //add to validMoves if good

            //putting stuff back where it was
            board.addPiece(moves.getStartPosition(), currentPiece);
            board.addPiece(moves.getEndPosition(), pieceCapture);
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            enPassantMoves(startPosition, currentPiece, validMoves);
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            castlingMoves(startPosition, currentPiece, validMoves);
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());

        if (currentPiece == null) {
            throw new InvalidMoveException("No piece here");
        }

        if (currentPiece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException("Illegal move: This is not your piece");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Illegal move: This move is not in the rules");
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if (currentPiece.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            }
            else {
                blackKingMoved = true;
            }
        } else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            updateRookFlags(move.getStartPosition(), currentPiece.getTeamColor());
        }

        board.addPiece(move.getStartPosition(), null);

        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
                move.getStartPosition().getColumn() != move.getEndPosition().getColumn() &&
                board.getPiece(move.getEndPosition()) == null) {
            board.addPiece(new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn()), null);
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2) {
            int row = move.getStartPosition().getRow();
            if (move.getEndPosition().getColumn() == 7) { // Kingside
                ChessPiece rook = board.getPiece(new ChessPosition(row, 8));
                board.addPiece(new ChessPosition(row, 8), null);
                board.addPiece(new ChessPosition(row, 6), rook);
            } else if (move.getEndPosition().getColumn() == 3) { // Queenside
                ChessPiece rook = board.getPiece(new ChessPosition(row, 1));
                board.addPiece(new ChessPosition(row, 1), null);
                board.addPiece(new ChessPosition(row, 4), rook);
            }
        }


        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), currentPiece);
        }

        this.lastMove = move;

        if (currentTeamTurn == TeamColor.WHITE) {
            currentTeamTurn = TeamColor.BLACK;
        } else {
            currentTeamTurn = TeamColor.WHITE;
        }
    }

    private void enPassantMoves(ChessPosition startPosition, ChessPiece pawn, Collection<ChessMove> validMoves) {
        if (lastMove == null) return;

        ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
        if (lastMovedPiece == null || lastMovedPiece.getPieceType() != ChessPiece.PieceType.PAWN) return;

        int startRow = lastMove.getStartPosition().getRow();
        int endRow = lastMove.getEndPosition().getRow();

        if (Math.abs(startRow - endRow) == 2) {
            if (lastMove.getEndPosition().getRow() == startPosition.getRow() &&
                    Math.abs(lastMove.getEndPosition().getColumn() - startPosition.getColumn()) == 1) {

                int direction = (pawn.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
                ChessPosition capturePos = new ChessPosition(startPosition.getRow() + direction, lastMove.getEndPosition().getColumn());

                board.addPiece(capturePos, pawn);
                board.addPiece(startPosition, null);
                board.addPiece(lastMove.getEndPosition(), null);

                if (!isInCheck(pawn.getTeamColor())) {
                    validMoves.add(new ChessMove(startPosition, capturePos, null));
                }

                board.addPiece(startPosition, pawn);
                board.addPiece(capturePos, null);
                board.addPiece(lastMove.getEndPosition(), lastMovedPiece);
            }
        }
    }

    private void castlingMoves(ChessPosition startPosition, ChessPiece king, Collection<ChessMove> validMoves) {
        if (startPosition.getColumn() != 5) return;

        if (isInCheck(king.getTeamColor())) return;

        int row = startPosition.getRow();
        boolean isWhite = king.getTeamColor() == TeamColor.WHITE;

        boolean kingMoved = isWhite ? whiteKingMoved : blackKingMoved;
        if (kingMoved) return;

        boolean qsRookMoved = isWhite ? whiteQueensideRookMoved : blackQueensideRookMoved;
        boolean ksRookMoved = isWhite ? whiteKingsideRookMoved : blackKingsideRookMoved;

        // Kingside
        if (!ksRookMoved && board.getPiece(new ChessPosition(row, 6)) == null && board.getPiece(new ChessPosition(row, 7)) == null) {
            if (isSafeSquare(row, 6, startPosition, king) && isSafeSquare(row, 7, startPosition, king)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(row, 7), null));
            }
        }

        // Queenside
        if (!qsRookMoved && board.getPiece(new ChessPosition(row, 2)) == null && board.getPiece(new ChessPosition(row, 3)) == null && board.getPiece(new ChessPosition(row, 4)) == null) {
            if (isSafeSquare(row, 3, startPosition, king) && isSafeSquare(row, 4, startPosition, king)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(row, 3), null));
            }
        }
    }

    private boolean isSafeSquare(int row, int col, ChessPosition start, ChessPiece king) {
        ChessPosition target = new ChessPosition(row, col);

        board.addPiece(target, king);
        board.addPiece(start, null);

        boolean isSafe = !isInCheck(king.getTeamColor());

        board.addPiece(start, king);
        board.addPiece(target, null);

        return isSafe;
    }

    private void updateRookFlags(ChessPosition start, TeamColor color) {
        if (color == TeamColor.WHITE) {
            if (start.getColumn() == 1) {
                whiteQueensideRookMoved = true;
            }
            if (start.getColumn() == 8) {
                whiteKingsideRookMoved = true;
            }
        } else {
            if (start.getColumn() == 1) {
                blackQueensideRookMoved = true;
            }
            if (start.getColumn() == 8) {
                blackKingsideRookMoved = true;
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Steps:
        ChessPosition currentKingPosition = null;

        //Find current team king (position)
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentSquare = new ChessPosition(row, col);
                ChessPiece kingSearch = board.getPiece(currentSquare);

                if (kingSearch != null && kingSearch.getTeamColor() == teamColor && kingSearch.getPieceType() == ChessPiece.PieceType.KING) {
                    currentKingPosition = currentSquare;
                }
            }
        }

        //Get all opposing team moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentSquare = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentSquare);

                if (currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> currentEnemyMoves = currentPiece.pieceMoves(board, currentSquare);
                    for (ChessMove move : currentEnemyMoves) {
                        if (move.getEndPosition().equals(currentKingPosition)) {return true;}
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return anyMovesLeft(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return anyMovesLeft(teamColor);
    }

    private boolean anyMovesLeft(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentSquare = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentSquare);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> ourMoves = validMoves(currentSquare);

                    if (!ourMoves.isEmpty()) {return false;}
                }
            }
        }
        return true;
    }


    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamTurn == chessGame.currentTeamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamTurn, board);
    }
}
