package file_management;

import java.io.File;
import java.util.ArrayList;

public final class AssociateFilesFactory extends FilesFactory{

    public AssociateFilesFactory() {
        super("associates", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }

    /**
     * Gets all the matching files from the parent factory's <code>getMatchingFiles()</code> method, and constructs an
     * instance of <code>AssociateFile</code> for each file said method returned.
     * @return list of associate files which have the correct file extension
     */
    public ArrayList<AssociateFile> getFiles() {
        ArrayList<AssociateFile> selectedFiles = new ArrayList<>();

        for(File file:getMatchingFiles()){
            selectedFiles.add(new AssociateFile(file));
        }
        return selectedFiles;
    }
}
