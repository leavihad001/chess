package ui;

public class ChessBoardDraw {
    public static void draw(String teamChoice) {
        System.out.println("\n");
        //Starting with just a white board and then adding the other stuff after I got it

        //header
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("\n");

        //middle grid and sides

        for (int row = 0; row < 10; row++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print("   ");

            for (int col = 0; col < 8; col++) {
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

        //footer
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("\n");



        //reset
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println("\n");
    }

    public static void main(String[] args) {
        System.out.println("=== Drawing White's Perspective ===");
        draw("WHITE");
    }
}