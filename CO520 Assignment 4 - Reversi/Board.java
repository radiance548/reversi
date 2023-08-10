import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * Represents the board of the Reversi game.
 *
 * @author Radiance O. Ngonnase
 * @version 2021.05.19
 */
public class Board
{
    // The size of the board
    // (The board is a size x size grid)
    private int size;
    
    // The panel that serves as the board
    private JPanel board;
    
    // A data structure that serves as a mapping between coordinates
    // and the buttons (tiles) that make up the board
    private CustomButton[][] buttonCoordinatesMap;
    
    // Whether a game has started on the board
    protected boolean gameStarted;
    
    /**
     * Constructor for objects of class Board
     */
    public Board(int size)
    {
        // Set the board size and create a board
        this.size = size;
        buttonCoordinatesMap = new CustomButton[size][size];
        makeGameBoard();
        
        // By default, no game has started on the board
        this.gameStarted = false;
    }

    /**
     * Make the game board.
     */
    private void makeGameBoard()
    {
        board = new JPanel();
        board.setBorder(new LineBorder(null, 12));
        board.setLayout(new GridLayout(size, size, 1, 1));
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                CustomButton button = new CustomButton();
                button.setBorder(new LineBorder(Color.WHITE));
                button.setRolloverEnabled(false);
                button.setFocusPainted(false);
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) 
                    {
                        if (gameStarted) {
                            button.setBorder(new LineBorder(Color.BLACK, 3));
                            button.getModel().setRollover(true);
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) 
                    {
                        if (gameStarted) {
                            button.setBorder(new LineBorder(Color.WHITE));
                            button.getModel().setRollover(false);
                        }
                    }
                });
                
                buttonCoordinatesMap[i][j] = button;
                board.add(button);
            }
        }
    }
    
    /**
     * Set the size of the board.
     */
    public void setSize(int size)
    {
        this.size = size;
        makeGameBoard();
    }
    
    /**
     * Get the size of the board.
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * Set whether the game has started.
     */
    public void setGameStarted(boolean started)
    {
        gameStarted = started;
    }
    
    /**
     * Get whether the game has started.
     */
    public boolean isGameStarted()
    {
        return gameStarted;
    }
    
    /**
     * Get the JPanel that represents the game board.
     */
    public JPanel getPanel()
    {
        return board;
    }
    
    /**
     * Get the mapping between coordinates and the
       buttons (tiles) that make up the board.
     */
    public CustomButton[][] getButtonCoordinatesMap()
    {
        return buttonCoordinatesMap;
    }

    // ---- Inner class ----
    
    /**
     * A custom JButton that serves as a tile on the game board.
     * 
     */
    protected class CustomButton extends JButton
    { 
        // The disc to be placed on the tile
        private ImageIcon disc;
    
        /**
         * Create a new, empty tile (button) that will be added to the board.
         */
        public CustomButton()
        {
            super();
            disc = new ImageIcon();
        }
    
        /**
         * Places a disc on a tile (button) with a particular side facing up.
         */
        public void setImage(Image image)
        {
            disc.setImage(image);
            repaint();
        }
        
        /**
         * Gets the disc occupying a tile (button) on the board.
         */
        public Image getImage()
        {
            return disc.getImage();
        }
        
        // The following methods are redefinitions of methods
        // inherited from the superclass.
        
        /**
         * Allow images used to represent the disc sides to be placed on the tile
         * and rescaled with the tile size without minimal effect on their quality.
         * 
         * @param g The graphics context that can be used to draw on this component.
         */
        @Override
        public void paintComponent(Graphics g)
        {
            Graphics2D graphics2D = (Graphics2D) g;
            Insets insets = getInsets();
            
            RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHints(renderingHints);
            
            if (getModel().isPressed()) {
                graphics2D.setColor(new Color(116, 161, 109).darker());
            }
            else if(getModel().isRollover()) {
                graphics2D.setColor(new Color(116, 161, 109).brighter());
            }
            else {
                graphics2D.setColor(new Color(116, 161, 109));
            }
            graphics2D.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            
            if (disc != null) {
                graphics2D.drawImage(disc.getImage(), 
                                     (-(getWidth() - insets.left) / 2),
                                     (-(getHeight() - insets.top) / 2), 
                        	     (getWidth() - insets.left) * 2,
                        	     (getHeight() - insets.top) * 2,
                        	     null);
            }
        }
    }
}
