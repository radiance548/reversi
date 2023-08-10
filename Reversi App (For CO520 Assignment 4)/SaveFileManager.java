import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * A small utility class for retrieving save files.
 *
 * @author Radiance O. Ngonnase
 * @version 2021.05.19
 */
public class SaveFileManager
{
    // static field
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    /**
     * Constructor for objects of class SaveFileManager
     */
    public SaveFileManager()
    {
        fileChooser.setFileFilter(new FileNameExtensionFilter("SERIALISED file", "serialised"));
    }

    /**
     * Get the required save file, using the appropriate
     * JFileChooser dialog, depending on the command.
     *
     */
    public static File getSaveFile(String command, JFrame frame)
    {
        File selectedFile = null;
        
        if (command.equals("Save")) {
            int returnVal = fileChooser.showSaveDialog(frame);
    
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                
                if (selectedFile != null) {
                    try {
                        if (! selectedFile.getName().toLowerCase().endsWith(".serialised")) {
                            selectedFile = new File(selectedFile + ".serialised");
                        }
                        
                        if (! selectedFile.createNewFile() && selectedFile.exists()) {
                            int choice = JOptionPane.showConfirmDialog(frame,
                                                    		       selectedFile.getName() + " already exists." + 
                                                    		       "\nDo you want to replace it?",
                                                    		       "Confirm Save As",
                                                    		       JOptionPane.YES_NO_OPTION);
                            if (choice == 1 || choice == -1) {
                                selectedFile = getSaveFile("Save", frame);
                            }
                        }
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        else if (command.equals("Restore")) {
            int returnVal = fileChooser.showOpenDialog(frame);
    
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null && (! selectedFile.getName().toLowerCase().endsWith(".serialised"))) {
                    try {
                        selectedFile = new File(selectedFile + ".serialised");
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        
        return selectedFile;
    }
}
