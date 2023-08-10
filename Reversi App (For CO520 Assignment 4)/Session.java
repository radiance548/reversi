import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a game session. It also has utility methods
 * for retrieving and saving the state of the session. At 
 * any given time, the state of a session is kept by the game that is
 * currently being (or was most recently) played during it.
 *
 * @author Radiance O. Ngonnase
 * @version 2021.05.19
 */
public class Session
{
    // A list of the games played in this session
    private ArrayList<Game> games;

    /**
     * Constructor for objects of class Session
     */
    public Session()
    {
        games = new ArrayList<>();
    }

    /**
     * Add a new game to this session.
     */
    public void addGame(Game game)
    {
        for (Game aGame : games) {
            if (game == aGame) {
                return;
            }
        }
        
        games.add(game);
    }
    
    /**
     * Get the state of this session.
     */
    protected HashMap getState()
    {
        if (games.size() > 0) {
            return games.get(games.size() - 1).getStateData();
        }
        
        return null;
    }
    
    /**
     * Save the state of this session to a save file.
     */
    public boolean saveState(File saveFile)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile, false))) {
	    oos.writeObject(getState());
	    return true;
	} 
	catch (Exception e) {
            return false;
	}
    }
    
    /**
     * Get the state of this session from a save file.
     * 
     * @throws  CorruptedSaveFileException  If the save file is corrupted.
     * @throws  FileNotFoundException  If the save file does not exist.
     */
    public HashMap getState(File saveFile) throws CorruptedSaveFileException, FileNotFoundException
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
    	    HashMap<String, Object> state = (HashMap) ois.readObject();
    	    return state;
    	}
    	catch (FileNotFoundException e) {
    	    throw new FileNotFoundException("The selected game session could not be restored," +
                                            "\nbecause its corresponding save file does not exist or is inaccessible.");
    	}
	catch (Exception e) {
	    throw new CorruptedSaveFileException(saveFile);
	}
    }
}
