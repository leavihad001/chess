package ui;

public class ChessBoardDraw {
    private static int row = 0;
    private static int col = 0;

    public ChessBoardDraw(int row, int col) {
        ChessBoardDraw.row = row;
        ChessBoardDraw.col = col;
    }

    public static void draw(String teamChoice) {
        System.out.println("\n");
        //Starting with just a white board and then adding the other stuff after I got it

        //header draw
        //piece set 1
        //middle
        //piece set 2

        boolean color = !teamChoice.equalsIgnoreCase("BLACK");

        drawHeadFoot(color);
        drawPieceGrid(color);
        drawMiddleGrid();
        drawPieceGrid(color);
        drawHeadFoot(color);

        System.out.println("\n");
    }

    private static void drawHeadFoot(boolean color) {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("   ");

        String[] letters = { " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h " };
        if (color) {
            for (String letter : letters) {
                System.out.print(" " + letter + " ");
            }
        } else {
            for (int i = letters.length-1; i > 0; i--) {
                System.out.print(" " + letters[i] +" ");
            }
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void drawPieceGrid(boolean color) {
        for (row = 0; row < 8; row++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("   ");

            for (col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }
                System.out.print("   ");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("   \n");
        }
    }

    private static void drawMiddleGrid() {
        for (row = 0; row < 8; row++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("   ");

            for (col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }
                System.out.print("   ");
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("   \n");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Drawing White's Perspective ===");
        draw("WHITE");
    }
}