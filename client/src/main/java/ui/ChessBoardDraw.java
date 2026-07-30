package ui;

public class ChessBoardDraw {
    private static int row = 8;

    public ChessBoardDraw(int row) {
        ChessBoardDraw.row = row;
    }

    public static void draw(String teamChoice) {
        System.out.println("\n");

        boolean isWhite = !teamChoice.equalsIgnoreCase("BLACK");

        if (isWhite) {
            row = 8;
        } else {
            row = 1;
        }

        drawHeadFoot(isWhite);
        drawPieceGrid(isWhite);
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

    private static void drawPieceGrid(boolean isWhite) {
        keyPiecePrint(!isWhite, 0);
        if (isWhite) {
            row--;
        } else {
            row++;
        }
        pawnPiecePrint(!isWhite, 1);
        if (isWhite) {
            row--;
        } else {
            row++;
        }

        drawMiddleGrid(isWhite);

        pawnPiecePrint(isWhite, 0);
        if (isWhite) {
            row--;
        } else {
            row++;
        }
        keyPiecePrint(isWhite, 1);
        if (isWhite) {
            row--;
        } else {
            row++;
        }
    }

    private static void keyPiecePrint(boolean isWhite, int i) {
        String[] bPieceCharsWhiteOrdered = {" ♜ ", " ♞ ", " ♝ ", " ♛ ", " ♚ ", " ♝ ", " ♞ ", " ♜ "};
        String[] wPieceCharsWhiteOrdered = {" ♖ ", " ♘ ", " ♗ ", " ♕ ", " ♔ ", " ♗ ", " ♘ ", " ♖ "};

        String[] wPieceCharsBlackOrdered = {" ♖ ", " ♘ ", " ♗ ", " ♔ ", " ♕ ", " ♗ ", " ♘ ", " ♖ "};
        String[] bPieceCharsBlackOrdered = {" ♜ ", " ♞ ", " ♝ ", " ♚ ", " ♛ ",  " ♝ ", " ♞ ", " ♜ "};

        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + row + " ");

        for (int col = 0; col < 8; col++) {
            if ((i + (col + 1)) % 2 == 0) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            }
            if (isWhite && i == 0) {
                System.out.print(wPieceCharsBlackOrdered[col]);
            } else if (isWhite && i == 1) {
                System.out.print(wPieceCharsWhiteOrdered[col]);
            } else if (!isWhite && i == 0) {
                System.out.print(bPieceCharsWhiteOrdered[col]);
            } else {
                System.out.print(bPieceCharsBlackOrdered[col]);
            }
        }

        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + row + " ");

        clearLine();
    }

    private static void pawnPiecePrint(boolean isWhite, int i) {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + row + " ");

        for (int col = 0; col < 8; col++) {
            if ((i + (col + 1)) % 2 == 0) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            } else {
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            }

            if (isWhite) {
                System.out.print(EscapeSequences.WHITE_PAWN);
            } else {
                System.out.print(EscapeSequences.BLACK_PAWN);
            }
        }

        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(" " + row + " ");

        clearLine();
    }

    private static void drawMiddleGrid(boolean isWhite) {
        for (int i = 0; i < 4; i++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");

            for (int col = 0; col < 8; col++) {
                if ((i + (col + 1)) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }
                System.out.print(EscapeSequences.EMPTY);
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");

            clearLine();

            if (isWhite) {
                row--;
            } else {
                row++;
            }
        }
    }

    private static void clearLine() {
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print("\n");
    }

    public static void main(String[] args) {
        System.out.print("=== Drawing White's Perspective ===");
        draw("WHITE");

        System.out.print("=== Drawing Blacks's Perspective ===");
        draw("BLACK");
    }
}