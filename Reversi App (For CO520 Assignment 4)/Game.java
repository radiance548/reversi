import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
* Models a two-player version of the Reversi game on 
* the "board" shown in the main area of the application
* window and updates the other areas accordingly.
*
* @author Radiance O. Ngonnase
* @version 2021.05.19
 */
public class Game
{
    // static fields - The side of a disc to be placed facing up on the board,
    // depending on the player that is currently playing at any given time
    private static ImageIcon darkDiscSide;
    private static ImageIcon lightDiscSide;
    
    // Which player is currently playing
    private ImageIcon currentPlayer;
    
    // The number of players that cannot make valid moves
    private int playersThatCannotMove;
    
    // The number of buttons that a move has been played on
    private int playedButtons;
    
    /// The winner of the game
    private String winner;
    
    // An instance of the application
    private Reversi reversi;
    
    // The board
    private Board board;
    
    /**
     * Constructor for objects of class Game
     */
    public Game(Reversi reversi)
    {
        darkDiscSide = new ImageIcon("disc images/dark disc side.png");
        lightDiscSide = new ImageIcon("disc images/light disc side.png");
        
        playersThatCannotMove = 0;
        playedButtons = 4;
        currentPlayer = darkDiscSide;
        winner = "";
        
        this.reversi = reversi;
        board = reversi.board;
    }

    /**
     * Start this game on the board.
     */
    public void start()
    {
        int boardSize = board.getSize();
        board.setGameStarted(true);
        
        Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // if button is any of the four center ones
                    // pre-fill it (in an alternating pattern)
                // else if any other button
                    // add action listener that invokes the move() function
                if ((i == (boardSize / 2) - 1 && j == (boardSize / 2) - 1) ||
                    (i == (boardSize / 2) && j == (boardSize / 2))) {
                    buttonCoordinatesMap[i][j].setImage(lightDiscSide.getImage());
                    buttonCoordinatesMap[i][j].setEnabled(false);
                }
                else if ((i == (boardSize / 2) - 1 && j == (boardSize / 2)) ||
                         (i == (boardSize / 2) && j == (boardSize / 2) - 1)) {
                    buttonCoordinatesMap[i][j].setImage(darkDiscSide.getImage());
                    buttonCoordinatesMap[i][j].setEnabled(false);
                }
                else {
                    buttonCoordinatesMap[i][j].setEnabled(true);
                    buttonCoordinatesMap[i][j].addActionListener(e -> move((Board.CustomButton) e.getSource()));
                }
            }
        }
        
        updateFrame(0, 1, "2", "2", "Game started. It is " + reversi.playerNames[0].getText() + "'s turn to play.");
        
        board.getPanel().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (board.isGameStarted()) {
                    if (currentPlayer == darkDiscSide) {
                        reversi.statusBarMessage.setText(reversi.playerNames[0].getText() + " is playing...");
                    }
                    else if (currentPlayer == lightDiscSide) {
                        reversi.statusBarMessage.setText(reversi.playerNames[1].getText() + " is playing...");
                    }
                }
            }
        });
    }
    
    /**
     * Carry out the processing related to making
     * a move at a specific coordinate of the board.
     */
    private void move(Board.CustomButton button)
    {
        if (moveIsLegal(button, true)) {
            int captures = capture(button);
            button.setEnabled(false);
            if (currentPlayer == darkDiscSide) {
                button.setImage(darkDiscSide.getImage());
                currentPlayer = lightDiscSide;
                updateFrame(0, 1,
                            String.valueOf(Integer.parseInt(reversi.playersNumberOfDiscs[0].getText()) + captures + 1),
                            String.valueOf(Integer.parseInt(reversi.playersNumberOfDiscs[1].getText()) - captures),
                            reversi.playerNames[0].getText() + " has played. It is now  " +
                            reversi.playerNames[1].getText() + "'s turn to play.");
            }
            else if (currentPlayer == lightDiscSide) {
                button.setImage(lightDiscSide.getImage());
                currentPlayer = darkDiscSide;
                updateFrame(1, 0,
                            String.valueOf(Integer.parseInt(reversi.playersNumberOfDiscs[1].getText()) + captures + 1),
                            String.valueOf(Integer.parseInt(reversi.playersNumberOfDiscs[0].getText()) - captures),
                            reversi.playerNames[1].getText() + " has played. It is now  " +
                            reversi.playerNames[0].getText() + "'s turn to play.");
            }
            playedButtons++;
        }
        
        int boardSize = board.getSize();
        if (! canMove() && playedButtons < (boardSize * boardSize)) {
            String name = "";
            if (currentPlayer == darkDiscSide) {
                name = reversi.playerNames[0].getText();
            }
            else if (currentPlayer == lightDiscSide) {
                name = reversi.playerNames[1].getText();
            }
            
            Object[] options = {"Pass"};
            int choice = JOptionPane.showOptionDialog(reversi.frame,
                                                      "You are not able to make any legal moves, " + name +
                                                      ". Click on the \n'Pass' button below to pass your turn.",
                                                      "Pass Your Turn",
                                                      JOptionPane.YES_NO_OPTION,
                                                      JOptionPane.INFORMATION_MESSAGE,
                                                      null,
                                                      options, 
                                                      options[0]);
            if (choice == 0){
                if (currentPlayer == darkDiscSide) {
                    playersThatCannotMove++;
                    currentPlayer = lightDiscSide;
                    reversi.statusBarMessage.setText(reversi.playerNames[0].getText() + " has played. It is now  " +
                                                     reversi.playerNames[1].getText() + "'s turn to play.");
                }
                else if (currentPlayer == lightDiscSide) {
                    playersThatCannotMove++;
                    currentPlayer = darkDiscSide;
                    reversi.statusBarMessage.setText(reversi.playerNames[1].getText() + " has played. It is now  " + 
                                                     reversi.playerNames[0].getText() + "'s turn to play.");
                }
                
                if (! canMove()) {
                    playersThatCannotMove++;
                }
                else {
                    playersThatCannotMove = 0;
                }
            }
        }
        
        if (playersThatCannotMove == 2 || playedButtons == (boardSize * boardSize)) {
            board.setGameStarted(false);
            if (playersThatCannotMove != 2) {
                reversi.statusBarMessage.setText("Game has ended.");
            }
            else {
                reversi.statusBarMessage.setText("Game has ended, because neither player could make a legal move.");
            }
            int player1NumberOfDiscs = Integer.parseInt(reversi.playersNumberOfDiscs[0].getText());
            int player2NumberOfDiscs = Integer.parseInt(reversi.playersNumberOfDiscs[1].getText());
            
            reversi.playerScores[0].setText(String.valueOf(Integer.parseInt(reversi.playerScores[0].getText()) +  player1NumberOfDiscs));
            reversi.playerScores[1].setText(String.valueOf(Integer.parseInt(reversi.playerScores[1].getText()) +  player2NumberOfDiscs));
            
            String declareWinnerMessage = "";
            if (player1NumberOfDiscs > player2NumberOfDiscs) {
                winner = reversi.playerNames[0].getText();
                declareWinnerMessage = winner + " wins with a score of " + player1NumberOfDiscs + ".";
            }
            else if (player2NumberOfDiscs > player1NumberOfDiscs) {
                winner = reversi.playerNames[1].getText();
                declareWinnerMessage = winner + " wins with a score of " + player2NumberOfDiscs + ".";
            }
            else if (player2NumberOfDiscs == player1NumberOfDiscs) {
                winner = "Neither of you";
                declareWinnerMessage = "The game ended in a tie, with scores of \n" + player1NumberOfDiscs + " each. Therefore, neither of you wins.";
            }
            
            Object[] options = {"OK"};
            int choice = JOptionPane.showOptionDialog(reversi.frame,
                                                      declareWinnerMessage,
                                                      "GAME OVER",
                                                      JOptionPane.YES_NO_OPTION,
                                                      JOptionPane.INFORMATION_MESSAGE,
                                                      null,
                                                      options, 
                                                      options[0]);
            if (choice == 0 || choice == -1){
                board.setGameStarted(false);
                Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
                
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        if (buttonCoordinatesMap[i][j].getImage() == null) {
                            buttonCoordinatesMap[i][j].setEnabled(false);
                        }
                    }
                }
                reversi.startButtonPanel.setVisible(true);
            }
        }
    }
    
    /**
     * Capture pieces starting from the x and y position of the current element in the board
     * by looking into the eight possible directions to find another piece of the current player 
     * (skipping pieces of the opponent). If such a piece is found, then it flips all skipped pieces.
     * (This method is invoked by the move method before the current player is changed.)
     */
    private int capture(Board.CustomButton button)
    {
        int captures = 0;
        int boardSize = board.getSize();
        Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
        
        capturing:
        for (int  i = 0;  i < boardSize; i++) {
            for (int  j = 0;  j < boardSize; j++) {
                if (buttonCoordinatesMap[i][j] == button) {
                    // north direction
                    int x = j;
                    int y = i;
                    
                    while ((y - 1 >=  0) && buttonCoordinatesMap[y - 1][x].getImage() != null) {
                        y--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][x].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][x].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][x].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][x].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // north-east direction
                    x = j;
                    y = i;
                    while ((x + 1 < boardSize) && (y - 1 >= 0) && buttonCoordinatesMap[y - 1][x + 1].getImage() != null) {
                        y--;
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                    }
                    
                    // east direction
                    x = j;
                    y = i;
                    
                    while ((x + 1 < boardSize) && buttonCoordinatesMap[y][x + 1].getImage() != null) {
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int l = (j + 1); l < x ; l++) {
                                    if (buttonCoordinatesMap[y][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[y][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int l = (j + 1); l < x ; l++) {
                                    if (buttonCoordinatesMap[y][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[y][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // south-east direction
                    x = j;
                    y = i;
                    
                    while ((x + 1 < boardSize) && (y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x + 1].getImage() != null) {
                        y++;
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                    }
                    
                    // south direction
                    x = j;
                    y = i;
                    
                    while ((y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x].getImage() != null) {
                        y++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][x].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][x].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][x].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][x].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // south-west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && (y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x - 1].getImage() != null) {
                        y++;
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                    }
                    
                    // west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && buttonCoordinatesMap[y][x - 1].getImage() != null) {
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int l = (j - 1); l > x ; l--) {
                                    if (buttonCoordinatesMap[y][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[y][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int l = (j - 1); l > x ; l--) {
                                    if (buttonCoordinatesMap[y][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[y][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // north-west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && (y - 1 >= 0) && buttonCoordinatesMap[y - 1][x - 1].getImage() != null) {
                        y--;
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(darkDiscSide.getImage());
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        buttonCoordinatesMap[k][l].setImage(lightDiscSide.getImage());
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                    }
                
                    break capturing;
                }
            }
        }

        return captures;
    }
    
    /**
     * Compute	if a move would	be legal, and if not shows an 
     * appropriate message in the status bar. A move is legal
     * only if it would capture at least one of the opponent's
     * pieces.
     */
    private boolean moveIsLegal(Board.CustomButton button, boolean showStatus)
    {
        boolean isLegal = false;
        int captures = 0;
        int boardSize = board.getSize();
        Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
        
        checkCapturing:
        for (int  i = 0;  i < boardSize; i++) {
            for (int  j = 0;  j < boardSize; j++) {
                if (buttonCoordinatesMap[i][j] == button) {
                    // north direction
                    int x = j;
                    int y = i;
                    
                    while ((y - 1 >=  0) && buttonCoordinatesMap[y - 1][x].getImage() != null) {
                        y--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][x].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][x].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // north-east direction
                    x = j;
                    y = i;
                    while ((x + 1 < boardSize) && (y - 1 >= 0) && buttonCoordinatesMap[y - 1][x + 1].getImage() != null) {
                        y--;
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                    }
                    
                    // east direction
                    x = j;
                    y = i;
                    
                    while ((x + 1 < boardSize) && buttonCoordinatesMap[y][x + 1].getImage() != null) {
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int l = (j + 1); l < x ; l++) {
                                    if (buttonCoordinatesMap[y][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int l = (j + 1); l < x ; l++) {
                                    if (buttonCoordinatesMap[y][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // south-east direction
                    x = j;
                    y = i;
                    
                    while ((x + 1 < boardSize) && (y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x + 1].getImage() != null) {
                        y++;
                        x++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j + 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l++;
                                }
                                break;
                            }
                        }
                    }
                    
                    // south direction
                    x = j;
                    y = i;
                    
                    while ((y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x].getImage() != null) {
                        y++;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][x].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][x].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // south-west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && (y + 1 < boardSize) && buttonCoordinatesMap[y + 1][x - 1].getImage() != null) {
                        y++;
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i + 1); k < y ; k++) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                    }
                    
                    // west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && buttonCoordinatesMap[y][x - 1].getImage() != null) {
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                for (int l = (j - 1); l > x ; l--) {
                                    if (buttonCoordinatesMap[y][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                for (int l = (j - 1); l > x ; l--) {
                                    if (buttonCoordinatesMap[y][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    // north-west direction
                    x = j;
                    y = i;
                    
                    while ((x - 1 >= 0) && (y - 1 >= 0) && buttonCoordinatesMap[y - 1][x - 1].getImage() != null) {
                        y--;
                        x--;
                        
                        if (currentPlayer == darkDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == darkDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == lightDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                        else if (currentPlayer == lightDiscSide) {
                            if (buttonCoordinatesMap[y][x].getImage() == lightDiscSide.getImage()) {
                                int l = j - 1;
                                for (int k = (i - 1); k > y ; k--) {
                                    if (buttonCoordinatesMap[k][l].getImage() == darkDiscSide.getImage()) {
                                        captures++;
                                    }
                                    l--;
                                }
                                break;
                            }
                        }
                    }
                
                    break checkCapturing;
                }
            }
        }

        if (captures == 0) {
            if (showStatus) {
                reversi.statusBarMessage.setText("Illegal move. A legal move would capture at least one of your opponent's pieces.");
            }
        }
        else if (captures > 0) {
            isLegal = true;
        }
        
        return isLegal;
    }
    
    /**
     * 	Check based on the isMoveLegal method if
     * 	the current player can make a valid move.
     */
    private boolean canMove()
    {
        int boardSize = board.getSize();
        Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (buttonCoordinatesMap[i][j].getImage() == null) {
                    if (moveIsLegal(buttonCoordinatesMap[i][j], false)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 	Update the score panels and the status bar
     * 	of the main application window.
     */
    private void updateFrame(int index1, int index2, String text1, String text2, String text3)
    {
        reversi.playersNumberOfDiscs[index1].setText(text1);
        reversi.playersNumberOfDiscs[index2].setText(text2);
        reversi.statusBarMessage.setText(text3);
    }
    
    /**
     * 	Get all the details of the state
     * 	of this game, stored in a HashMap.
     */
    protected HashMap getStateData()
    {
        HashMap<String, Object> stateData = new HashMap<>();
        
        int boardSize = board.getSize();
        Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
        String[][] buttonsState = new String[boardSize][boardSize];
        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (buttonCoordinatesMap[i][j].getImage() == null) {
                    buttonsState[i][j] = "empty";
                }
                else if (buttonCoordinatesMap[i][j].getImage() != null) {
                    if (buttonCoordinatesMap[i][j].getImage() == darkDiscSide.getImage()) {
                        buttonsState[i][j] = "black";
                    }
                    else if (buttonCoordinatesMap[i][j].getImage() == lightDiscSide.getImage()) {
                        buttonsState[i][j] = "white";
                    }
                }
            }
        }
        
        stateData.put("Board buttons' state", buttonsState);
        
        if (currentPlayer == darkDiscSide) {
            stateData.put("Current Player", "black");
        }
        else if (currentPlayer == lightDiscSide) {
            stateData.put("Current Player", "white");
        }
        
        stateData.put("Player Names", reversi.playerNames);
        stateData.put("Player Scores", reversi.playerScores);
        stateData.put("Players' Number Of Discs", reversi.playersNumberOfDiscs);
        stateData.put("Game Started", board.isGameStarted());
        if (! board.isGameStarted()) {
            stateData.put("Winner", winner);
        }
        stateData.put("Status Bar's Message", reversi.statusBarMessage);
        stateData.put("GUI buttons' state", new boolean[] {reversi.play.isEnabled(), reversi.startButtonPanel.isVisible()});
        return stateData;
    }
    
    /**
     * Restore a saved game using the state data gotten from its corresponding save file.
     * 
     * @throws  CorruptedSaveFileException  If the save file is malformed/corrupted.
     * 
     */
    protected void restore(HashMap<String, Object> restoredStateData) throws CorruptedSaveFileException
    {
        try {
            if (restoredStateData.get("Current Player").equals("black")) {
                currentPlayer = darkDiscSide;
            }
            else if (restoredStateData.get("Current Player").equals("white")) {
                currentPlayer = lightDiscSide;
            }
            
            playersThatCannotMove = 0;
            playedButtons = 0;
            
            JTextField[] restoredPlayerNames = (JTextField[]) restoredStateData.get("Player Names");
            JLabel[] restoredPlayerScores = (JLabel[]) restoredStateData.get("Player Scores");
            JLabel[] restoredPlayersNumberOfDiscs = (JLabel[]) restoredStateData.get("Players' Number Of Discs");
            
            for (int i = 0; i < 2; i++) {
                if (! restoredPlayerNames[i].isEditable()) {
                    reversi.playerNames[i].setText(restoredPlayerNames[i].getText());
                    reversi.playerNames[i].setEditable(false);
                }
                
                reversi.playerScores[i].setText(restoredPlayerScores[i].getText());
                reversi.playersNumberOfDiscs[i].setText(restoredPlayersNumberOfDiscs[i].getText());
            }
            
            JLabel restoredStatusBarMessage = (JLabel) restoredStateData.get("Status Bar's Message");
            reversi.statusBarMessage.setText(restoredStatusBarMessage.getText());
            
            boolean[] guiButtonsState = (boolean[]) restoredStateData.get("GUI buttons' state");
            reversi.play.setEnabled(guiButtonsState[0]);
            reversi.startButtonPanel.setVisible(guiButtonsState[1]);
            
            String[][] buttonsState = (String[][]) restoredStateData.get("Board buttons' state");
            int boardSize = buttonsState.length;
            reversi.boardSize = boardSize;
            reversi.replaceBoard();
            board = reversi.board;
            
            board.setGameStarted((boolean) restoredStateData.get("Game Started"));
            
            Board.CustomButton[][] buttonCoordinatesMap = board.getButtonCoordinatesMap();
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (buttonsState[i][j].equals("empty")) {
                        buttonCoordinatesMap[i][j].setEnabled(true);
                        buttonCoordinatesMap[i][j].addActionListener(e -> move((Board.CustomButton) e.getSource()));
                    }
                    else if (buttonsState[i][j].equals("black")) {
                        buttonCoordinatesMap[i][j].setImage(darkDiscSide.getImage());
                        buttonCoordinatesMap[i][j].setEnabled(false);
                        playedButtons++;
                    }
                    else if (buttonsState[i][j].equals("white")) {
                        buttonCoordinatesMap[i][j].setImage(lightDiscSide.getImage());
                        buttonCoordinatesMap[i][j].setEnabled(false);
                        playedButtons++;
                    }
                }
            }
            
            if (! board.isGameStarted()) {
                String winner = (String) restoredStateData.get("Winner");
                if (! winner.equals("Neither of you")) {
                    JOptionPane.showMessageDialog(reversi.frame,
                                                  winner + " won this game. \nCheck the score panel to know your\n" + 
                                                  "scores (# of discs on the board).",
                                                  "GAME OVER",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(reversi.frame,
                                                  winner + " won this game,\nbecause it ended in a tie.\nCheck the score panel to know your\n" + 
                                                  "scores (# of discs on the board).",
                                                  "GAME OVER",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            board.getPanel().addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (board.isGameStarted()) {
                        if (currentPlayer == darkDiscSide) {
                            reversi.statusBarMessage.setText(reversi.playerNames[0].getText() + " is playing...");
                        }
                        else if (currentPlayer == lightDiscSide) {
                            reversi.statusBarMessage.setText(reversi.playerNames[1].getText() + " is playing...");
                        }
                    }
                }
            });
        }
        catch (Exception e) {
            throw new CorruptedSaveFileException();
        }
    }
}
