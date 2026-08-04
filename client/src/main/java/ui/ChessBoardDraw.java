package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.Collection;

public class ChessBoardDraw {

    public ChessBoardDraw() {}

    public static void draw(ChessGame game, String teamChoice, Collection<ChessPosition> highlightedSquares) {
        System.out.println("\n");
        ChessBoard board = game.getBoard();
        boolean isWhite = !teamChoice.equalsIgnoreCase("BLACK");

        drawHeadFoot(isWhite);
        drawMainGrid(board, isWhite, highlightedSquares);
        drawHeadFoot(isWhite);

        System.out.print("\n");
    }

    private static void drawHeadFoot(boolean isWhite) {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("   ");

        String[] letters = { " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h " };
        if (isWhite) {
            for (String letter : letters) {
                System.out.print(letter);
            }
        } else {
            for (int i = letters.length-1; i >= 0; i--) {
                System.out.print(letters[i]);
            }
        }
        System.out.print("   ");
        clearLine();
    }

    private static void drawMainGrid(ChessBoard board, boolean isWhite, Collection<ChessPosition> highlightedSquares) {
        int startRow = isWhite ? 8 : 1;
        int endRow = isWhite ? 0 : 9;
        int rowStep = isWhite ? -1 : 1;

        int startCol = isWhite ? 1 : 8;
        int endCol = isWhite ? 9 : 0;
        int colStep = isWhite ? 1 : -1;

        for (int row = startRow; row != endRow; row += rowStep) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");

            for (int col = startCol; col != endCol; col += colStep) {
                ChessPosition currentPos = new ChessPosition(row, col);
                boolean isLightSquare = (row + col) % 2 == 1;
                boolean isHighlighted = (highlightedSquares != null && highlightedSquares.contains(currentPos));

                if (isHighlighted) {
                    if (isLightSquare) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                } else {
                    if (isLightSquare) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    }
                }
                
                ChessPiece piece = board.getPiece(currentPos);
                printPieceCharacter(piece);
            }

            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");

            clearLine();
        }
    }

    private static void printPieceCharacter(ChessPiece piece) {
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
            return;
        }

        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        String icon = switch (type) {
            case KING -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };

        System.out.print(icon);
    }

    private static void clearLine() {
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print("\n");
    }

    public static void main(String[] args) {
        chess.ChessGame testGame = new chess.ChessGame();

        System.out.println("=== Drawing White's Perspective ===");
        draw(testGame, "WHITE", null);

        System.out.println("=== Drawing Black's Perspective ===");
        draw(testGame, "BLACK", null);
    }
}