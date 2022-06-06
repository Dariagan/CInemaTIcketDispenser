package file_management;

import java.io.File;
import java.util.ArrayList;

public class AssociateFilesFactory extends FilesFactory{

    public AssociateFilesFactory() {
        super("associates", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }

    public ArrayList<AssociateFile> getFiles() {
        ArrayList<AssociateFile> selectedFiles = new ArrayList<>();

        for(File file:getMatchingFiles()){
            selectedFiles.add(new AssociateFile(file));
        }
        return selectedFiles;
    }

}
