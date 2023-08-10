import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import java.util.HashMap;

/**
 * Main class of the Reversi application. Builds and
 * displays the application GUI and initialises all other components.
 *
 * To start the application, create an instance of this class.
 *
 * @author Radiance O. Ngonnase
 * @version 2021.05.19
 * 
 */
public class Reversi
{
    // The application window
    protected JFrame frame;
    
    // The names of the players
    protected JTextField[] playerNames;
    
    // The number of discs of each color on the board
    protected JLabel[] playersNumberOfDiscs;
    
    // The players' scores in the session
    protected JLabel[] playerScores;
    
    // The "Play" button
    protected JButton play;
    
    // The panel that contains the "Start New Game" button
    protected JPanel startButtonPanel;
    
    // The message displayed on the status bar
    protected JLabel statusBarMessage;
    
    // The "board" used to play the Reversi game
    protected Board board;
    
    // The panel that contains the board
    protected JPanel boardPanel;
    
    // The size of the board
    protected int boardSize;
    
    // The current game session
    private Session gameSession;
    
    /**
     * Constructor for objects of class Reversi
     */
    public Reversi()
    {
        // Set the default size of the "board" 
        // shown in the board area of the window
        boardSize = 8;
        
        gameSession = new Session();
        
        makeFrame();
    }
    
    // ---- Methods to build the frame (application window) and its menu using Swing ----
    
    /**
     * Create the application window and its content.
     */
    private void makeFrame()
    {
        // Set UI defaults
        UIManager.put("Menu.selectionBackground", new ColorUIResource(Color.LIGHT_GRAY));
        UIManager.put("Menu.font", new FontUIResource(new Font("sans-serif", Font.PLAIN, 12)));
        UIManager.put("MenuItem.borderPainted", false);
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(Color.LIGHT_GRAY));
        UIManager.put("MenuItem.acceleratorForeground", new ColorUIResource(Color.BLACK));
        UIManager.put("MenuItem.font", new FontUIResource(new Font("sans-serif", Font.PLAIN, 12)));
        UIManager.put("PopupMenu.border", new LineBorder(Color.WHITE.darker()));
        UIManager.put("Separator.foreground", new ColorUIResource(Color.LIGHT_GRAY));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Trebuchet MS", Font.PLAIN, 14)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("Trebuchet MS", Font.BOLD, 14)));
        UIManager.put("Button.disabledText", new ColorUIResource(Color.WHITE));
        
        // Create the application window
        frame = new JFrame("Play Reversi Game");
        
        // Ensure that on closing the window, the
        // application is actually terminated.
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                quit();
            }
        });
        
        // Make the menu bar of the application window
        makeMenuBar();
        
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        // Create and add the player panel to the window
        JComponent playerPanel = makePlayerPanel();
        contentPane.add(playerPanel, BorderLayout.WEST);
        
        // Create the panel that contains the board and
        // add it to the window
        boardPanel = makeBoardPanel();
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustBoardPanelPadding();
            }
        });
        contentPane.add(boardPanel, BorderLayout.CENTER);
        
        // Create and add the status bar to the window      
        JComponent statusBar = makeStatusBar();
        contentPane.add(statusBar, BorderLayout.SOUTH);
        
        // Set the default size of the window, 
        // place it at the center of the screen, and show it
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(((d.height / 5) * 4), (d.height / 2));    
        frame.setLocation(((d.width / 2) - (frame.getWidth() / 2)), ((d.height / 2) - (frame.getHeight() / 2)));
        frame.setVisible(true);
    }
    
    /**
     * Create the application window's menu bar.
     */
    private void makeMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Create the Options menu
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        
        JMenuItem item = new JMenuItem("Change Board Size");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_DOWN_MASK));
        item.addActionListener(e -> { changeBoardSize(); });
        menu.add(item);
        menu.addSeparator();
    
        item = new JMenuItem("New Game Session");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> { startNewGameSession(); });
        menu.add(item);
        
        item = new JMenuItem("Save Game Session");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> { saveGameSession(); });
        menu.add(item);
    
        item = new JMenuItem("Restore Game Session");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> { restoreGameSession(); });
        menu.add(item);
        menu.addSeparator();
        
        item = new JMenuItem("Quit");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> { quit(); });
        menu.add(item);
    }
    
    // ---- Implementation of menu functions ----
    
    /**
     * Change Board Size function: Change the game board's size.
     */
    private void changeBoardSize()
    {
        Object[] possibilities = {"4", "6", "8", "10", "12", "14", "16", "18", "20"};
        String choice = (String)JOptionPane.showInputDialog(frame,
                                                            "Select a new board size: \n"
                                                            + "(The board will become a size x size board)",
                                                            "Change Board Size",
                                                            JOptionPane.PLAIN_MESSAGE,
                                                            null,
                                                            possibilities,
                                                            "8");
        
        
        if ((choice != null)) {
            if (Integer.parseInt(choice) == 8) {
                if (boardSize == 8) {
                    return;
                }
            }
            
            boardSize = Integer.parseInt(choice);
            if (board.isGameStarted()) {
                int choice2 = JOptionPane.showConfirmDialog(
            		    frame, 
            		    "Are you sure you want to change the size of the board?\n" + 
            		    "(A game is in progress on the board. \n" + 
            		    "Changing the board size will restart the game.)" ,
            		    "Confirm Board Reset",
            		    JOptionPane.YES_NO_OPTION,
            		    JOptionPane.QUESTION_MESSAGE);
        	if (choice2 == 0) {
        	    startNewGame();
        	}
        	else if (choice2 != 0) {
        	    return;
        	}
            }
            else {
                replaceBoard();
            }
        }
        else {
            return;
        }
    }
    
    /**
     * New Game Session function: Start a new game session for different players.
     */
    private void startNewGameSession()
    {
        gameSession = new Session();
        replaceBoard();
        
        for (int i = 0; i < 2; i++) {
            playerNames[i].setEditable(true);
            playerNames[i].setText("");
            playerScores[i].setText("0");
            playersNumberOfDiscs[i].setText("0");
        }
        
        statusBarMessage.setText("To start this session, enter the player names and click on the \"PLAY\" button.");
        play.setEnabled(true);
        startButtonPanel.setVisible(false);
    }

    /**
     * Save Game Session function: Save the application state corresponding to the current game session
     * to a save file.
     */
    private void saveGameSession()
    {
        if(gameSession.getState() != null) {
            if (gameSession.saveState(new SaveFileManager().getSaveFile("Save", frame))) {
                Object[] options = {"Start New Game Session", "Quit"};
                int choice = JOptionPane.showOptionDialog(frame,
                                                          "The current game session has been saved. \n What would you like to do now? " + 
                                                          "(Select an option below)\n\nNOTE: Closing this dialog box will automatically" +
                                                          " exit the application.",
                                                          "Choose an Option",
                                                          JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.INFORMATION_MESSAGE,
                                                          null,
                                                          options, 
                                                          options[0]);
                if (choice == 0){
                    startNewGameSession();
                }
                else if (choice == 1 || choice == -1){
                    quit();
                }
            }
            else {
                Object[] options = {"Try Again", "Cancel"};
                int choice = JOptionPane.showOptionDialog(frame,
                                                          "The current game session could not be saved.",
                                                          "Session Save Error",
                                                          JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.ERROR_MESSAGE,
                                                          null,
                                                          options, 
                                                          options[0]);
                if (choice == 0){
                    saveGameSession();
                }
            }
        }
        else {
            sendNotification("To be able to save a game session, you need to have started playing.", "Start Playing");
        }
    }
    
    /**
     * Restore Game Session function: Restore the application state corresponding to a game session
     * from a save file.
     */
    private void restoreGameSession()
    {
        if (gameSession.getState() == null || ! board.isGameStarted()) {
            try {
                HashMap<String, Object> restoredStateData = gameSession.getState(new SaveFileManager().getSaveFile("Restore", frame));
                Game restoredGame = new Game(this);
                gameSession.addGame(restoredGame);
                restoredGame.restore(restoredStateData);
                
                sendNotification("The selected game session has been restored.", "Session Restored");
            }
            catch (Exception e) {
                sendNotification(e.getMessage(), "Session Restore Error");
            }
        }
        else {
            sendNotification("You cannot restore a game session while a game is being played.", "Finish Playing");
        }
    }
    
    /**
     * Quit function: Exit the application.
     */
    private void quit()
    {
        int choice = JOptionPane.showConfirmDialog(frame,
                                		   "Are you sure you want to quit?",
                                		   "Confirm Quit",
                                		   JOptionPane.YES_NO_OPTION);
    	if (choice == 0) {
    	    System.exit(0);
    	}
    }
    
     // ---- Support methods used to build some of the frame's components using Swing ----
     
    /**
     * Helper method for the createPlayerContent method. 
     * Generate panels that contain a specific piece
     * of information about a particular player.
     */
    private JPanel createInfoPanel(JLabel informationLabel, JLabel information, Color foreground, Color background)
    {
        // Create the information label
        JLabel label = informationLabel;
        label.setFont(new Font("sans-serif", Font.BOLD, 14));
        label.setForeground(foreground);
        
        // Set up the label that holds the information itseld
        information.setFont(new Font("sans-serif", Font.PLAIN, 14));
        information.setForeground(foreground);
        
        // Add both into panel with horizontal box layout for spacing
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(6, 12, 6, 12));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.setBackground(background);
        infoPanel.add(label);
        infoPanel.add(information);
        
        return infoPanel;
    }
    
    /**
     * Helper method for the makePlayerPanel method. 
     * Generate the panel in the player panel that 
     * contains information about a particular player.
     */
    private JPanel createPlayerContent(String player, JLabel noOfDiscs, JLabel score, JTextField nameField, Color foreground, Color background)
    {
        // Create the panel that is going to contain
        // all the information about the player
        // (This will be the main panel)
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new GridLayout(4, 1));
        innerPanel.setBorder(new LineBorder(foreground.darker(), 3));
        innerPanel.setBackground(background);
        
        // Create the player label
        JLabel playerLabel = new JLabel(player);
        playerLabel.setForeground(foreground);
        playerLabel.setFont(new Font("sans-serif", Font.BOLD, 20));
        
        // Add into panel with vertical box layout for spacing
        JPanel playerLabelPanel = new JPanel();
        playerLabelPanel.setLayout(new BoxLayout(playerLabelPanel, BoxLayout.Y_AXIS));
        playerLabelPanel.setBorder(new EmptyBorder(12, 12, 6, 12));
        playerLabelPanel.setBackground(background);
        playerLabelPanel.add(playerLabel);
        playerLabelPanel.add(Box.createVerticalStrut(20));
        
        // Add that panel into the main panel
        innerPanel.add(playerLabelPanel);
        
        // Create the name field label
        JLabel nameFieldLabel = new JLabel("Name:");
        nameFieldLabel.setFont(new Font("sans-serif", Font.BOLD, 14));
        nameFieldLabel.setForeground(foreground);
        
        // Set up the name field
        nameField.setBorder(new LineBorder(foreground));
        nameField.setFont(new Font("sans-serif", Font.PLAIN, 13));
        nameField.setMaximumSize(nameField.getPreferredSize());
        
        // Add both into panel with horizontal box layout for spacing
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.setBorder(new EmptyBorder(12, 12, 6, 12));
        namePanel.setBackground(background);
        namePanel.add(nameFieldLabel);
        namePanel.add(Box.createHorizontalStrut(5));
        namePanel.add(nameField);
        
        // Add that panel into the main panel
        innerPanel.add(namePanel);
        
        // Create the panel that holds the information about
        // the number of discs this player has on the board
        JPanel discsInfo = createInfoPanel(new JLabel("# of discs on the board: "), noOfDiscs, foreground, background);
        
        // Add that panel into the main panel
        innerPanel.add(discsInfo);
        
        // Create the panel that holds the information
        // about this player's score in the session
        JPanel scoreInfo = createInfoPanel(new JLabel("Score in this session: "), score, foreground, background);
        
        // Add that panel into the main panel
        innerPanel.add(scoreInfo);
        
        // Add the main panel into a panel with flow layout for spacing
        JPanel panel = new JPanel();
        panel.add(innerPanel);
        
        return panel;
    }
    
    /**
     * Make the player panel.
     */
    private JPanel makePlayerPanel()
    {
        // Create the player panel
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK));
        
        playerNames = new JTextField[] {new JTextField(11), new JTextField(11)};
        playersNumberOfDiscs = new JLabel[] {new JLabel("0"), new JLabel("0")};
        playerScores = new JLabel[] {new JLabel("0"), new JLabel("0")};
        startButtonPanel = new JPanel();
        
        // Create the player 1 panel of the player panel
        final JTextField nameField1 = playerNames[0];
        JLabel noOfBlackDiscs = playersNumberOfDiscs[0];
        JLabel player1Score = playerScores[0];
        JComponent innerPanel1 = createPlayerContent("Player 1 (Black)", noOfBlackDiscs, player1Score, nameField1, Color.WHITE, Color.BLACK);
        innerPanel1.setBorder(new EmptyBorder(10, 6, 10, 6));
        
        // Create the player 2 panel of the player panel
        final JTextField nameField2 = playerNames[1];
        JLabel noOfWhiteDiscs = playersNumberOfDiscs[1];
        JLabel player2Score = playerScores[1];
        JComponent innerPanel2 = createPlayerContent("Player 2 (White)", noOfWhiteDiscs, player2Score, nameField2, Color.BLACK, Color.WHITE);
        innerPanel2.setBorder(new EmptyBorder(0, 6, 10, 4));
        
        // Add the two panels into panel with grid layout for sizing
        JComponent panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(innerPanel1);
        panel.add(innerPanel2);
        
        // Add that panel into a scroll pane to support resizing of
        // the main application window
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add the scroll pane into the player panel
        playerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create the "Play" button
        play = new JButton("PLAY");
        play.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createRaisedSoftBevelBorder(),
               BorderFactory.createLineBorder(new Color(132, 115, 158), 5)));
        play.setBackground(new Color(132, 115, 158));
        play.setFont(new Font("sans-serif", Font.BOLD, 20));
        play.setFocusPainted(false);
        play.addActionListener(ev -> play(play));
        
        // Add into panel with box layout for sizing
        JPanel playButtonPanel = new JPanel();
        playButtonPanel.setLayout(new GridLayout(1, 1));
        playButtonPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        playButtonPanel.add(play);
        
        // Create the "Start New Game" button
        JButton startNewGame = new JButton(" START NEW GAME ");
        startNewGame.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createRaisedSoftBevelBorder(),
               BorderFactory.createLineBorder(Color.WHITE, 10)));
        startNewGame.setBackground(Color.WHITE);
        startNewGame.setForeground(new Color(132, 115, 158));
        startNewGame.setFont(new Font("sans-serif", Font.BOLD, 20));
        startNewGame.setFocusPainted(false);
        startNewGame.addActionListener(ev -> startNewGame());
        
        // Add into panel with box layout for sizing
        startButtonPanel.setLayout(new GridLayout(1, 1));
        startButtonPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        startButtonPanel.add(startNewGame);
        startButtonPanel.setVisible(false);
        
        // Add the two previously created panels into this one for spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        buttonPanel.add(new JSeparator());
        buttonPanel.add(playButtonPanel);
        buttonPanel.add(startButtonPanel);
        
        // Add that panel into the player panel
        playerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return playerPanel;
    }
    
    /**
     * Make the game board.
     */
    private JPanel makeBoardPanel()
    {
        // Create the board
        board = new Board(boardSize);
        
        // Set the spacing around the board
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(board.getPanel(), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Make the status bar.
     */
    private JComponent makeStatusBar()
    {
        // Create the status bar message label
        statusBarMessage = new JLabel("To start playing, enter the player names and click on the \"PLAY\" button.");
        statusBarMessage.setFont(new Font("sans-serif", Font.PLAIN, 12)); 
        
        // Add the label to a panel with flow layout first
        JComponent innerPanel = new JPanel();
        innerPanel.add(statusBarMessage);
        
        // Then add panel to a second panel with flow layout for spacing
        JPanel statusBar = new JPanel();
        statusBar.add(innerPanel);
        statusBar.setBorder(new LineBorder(null, 3));
        
        // Finally, add the second panel into a scroll pane to 
        // support resizing of the main application window
        JScrollPane scrollPane = new JScrollPane(statusBar);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        return scrollPane;
    }
    
    /**
     * Implementation of the "Play" button function. 
     * Validate the inputs into the player name text
     * fields and starts the actual game session.
     */
    private void play(JButton play) 
    {
        // If names have been entered and they are not duplicates, locking editing of the names
        if (playerNames[0].getText().equals(playerNames[1].getText()) == false) {
            if (playerNames[0].getText().isEmpty() == false ) {
                playerNames[0].setEditable(false);
            }
            if (playerNames[1].getText().isEmpty() == false ) {
                playerNames[1].setEditable(false);
            }
        }
        
        // In any case, update the status bar depending on the input that has or has not been entered
        if (playerNames[0].isEditable() == false && playerNames[1].isEditable() == false) {
            play.setEnabled(false);
            startNewGame();
        }
        else if (playerNames[0].getText().isEmpty() || playerNames[1].getText().isEmpty()) {
            if (playerNames[0].isEditable() == false) {
                statusBarMessage.setText("Please enter a name for player 2.");
            }
            else if (playerNames[1].isEditable() == false) {
                statusBarMessage.setText("Please enter a name for player 1.");
            }
            else {
                statusBarMessage.setText("Please enter the name of each player.");
            }
        }
        else if (playerNames[0].getText().equals(playerNames[1].getText())) {
            statusBarMessage.setText("Please enter different names for each player.");
        }
    }
    
    /**
     * Implementation of the "Start New Game" button function. 
     * Start a new game on the "board".
     */
    private void startNewGame() 
    {
        replaceBoard();
        
        Game newGame = new Game(this);
        gameSession.addGame(newGame);
        newGame.start();
        
        startButtonPanel.setVisible(false);
    }
    
    // ---- Other support methods ----
    
    /** 
     * Increase or decrease the padding around the panel that
     * contains the "board", depending on the application window's
     * state.
     */
    private void adjustBoardPanelPadding() {
        if (frame.getExtendedState() == Frame.NORMAL) {
            boardPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        } 
        else if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
            boardPanel.setBorder(new EmptyBorder(20, 250, 20, 250));
        }
    }
    
    /** 
     * Replace the "board" shown in the application window.
     */
    protected void replaceBoard() {
        Container contentPane = frame.getContentPane();
        contentPane.remove(boardPanel);
        boardPanel = makeBoardPanel();
        adjustBoardPanelPadding();
        contentPane.add(boardPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    /** 
     * Send notifications.
     */
    private void sendNotification(String message, String title) {
        if (title.endsWith("Error")) {
            JOptionPane.showMessageDialog(frame,
                                          message,
                                          title,
                                          JOptionPane.ERROR_MESSAGE,
                                          null);
        }
        else {
            JOptionPane.showMessageDialog(frame,
                                          message,
                                          title,
                                          JOptionPane.INFORMATION_MESSAGE,
                                          null);
        }
    }
    
    // ---- Inner class ----
    
    /**
     * Utility class to improve the appearance of the application's interface.
     */
    private class CustomScrollBarUI extends BasicScrollBarUI
    {
        /**
         * Constructor for objects of class CustomScrollBarUI
         */
        public CustomScrollBarUI()
        {
            super();
        }
        
        /**
         * Has the same purpose as the method it is overriding.
         */
        @Override 
        protected void configureScrollBarColors()
        {
            this.thumbColor = Color.LIGHT_GRAY;
        }
        
        /**
         * Has the same purpose as the method it is overriding.
         */
        @Override
        protected JButton createDecreaseButton(int orientation) 
        {
            JButton button = super.createDecreaseButton(orientation);
            button.setBackground(frame.getBackground());
            button.setBorder(new EmptyBorder(1, 1, 1, 1));
            return button;
        }
        
        /**
         * Has the same purpose as the method it is overriding.
         */
        @Override
        protected JButton createIncreaseButton(int orientation) 
        {
            JButton button = super.createIncreaseButton(orientation);
            button.setBackground(frame.getBackground());
            button.setBorder(new EmptyBorder(1, 1, 1, 1));
            return button;
        }
    }
}