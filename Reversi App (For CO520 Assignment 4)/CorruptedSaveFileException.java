import java.io.File;

/**
 * Capture or report a corrupted save file.
 * 
 * @author Radiance O. Ngonnase
 * @version 2021.05.19
 */
public class CorruptedSaveFileException extends Exception
{
    // The corrupted save file.
    private File saveFile;

    /**
     * Store the details in error.
     * @param saveFile The corrupted save file.
     */
    public CorruptedSaveFileException(File saveFile)
    {
        this.saveFile = saveFile;
    }
    
    /**
     * Another constructor for objects of this class, for
     * when the details are unavailable for storing in error.
     */
    public CorruptedSaveFileException()
    {
    }

    /**
     * @return The corrupted save file.
     */
    public File getSaveFile()
    {
        return saveFile;
    }
    
    /**
     * @return A diagnostic string.
     */
    public String getMessage()
    {
        return "The selected game session could not be restored," +
               "\nbecause its corresponding save file is malformed or corrupted.";
    }
}
