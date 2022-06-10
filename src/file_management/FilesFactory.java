package file_management;

import java.io.File;
import java.util.ArrayList;

public abstract class FilesFactory {

    private final String FILES_FOLDER;
    private final String FILE_EXTENSION;
    private String FILENAME_NOUN;

    String getFILE_EXTENSION() {
        return FILE_EXTENSION;
    }

    String getFILENAME_NOUN() {
        return FILENAME_NOUN;
    }

    public FilesFactory(String filesFolder, String fileExtension) {
        this.FILES_FOLDER = filesFolder;
        this.FILE_EXTENSION = fileExtension;
    }

    public FilesFactory(String filesFolder, String fileExtension, String fileNameNoun) {
        this.FILES_FOLDER = filesFolder;
        this.FILE_EXTENSION = fileExtension;
        this.FILENAME_NOUN = fileNameNoun;
    }

    /**
     * Returns a list of files whose file extension is the same as the factory's established, and also which meet the
     * criteria established by the sub-factory' implementation of the
     * <code>extraSelectionConditionIsMet(String fileName)</code> method.
     *
     * @return positively selected files
     */
    ArrayList<File> getMatchingFiles() {

        String dirPath = System.getProperty("user.dir") + "\\data\\" + FILES_FOLDER;
        File dataFolder = new File(dirPath);

        File[] readFiles = dataFolder.listFiles();
        ArrayList<File> foundFiles = new ArrayList<>();

        if (readFiles != null) {
            for (File file : readFiles) {
                String fileName = file.getName();
                if (fileName.endsWith(FILE_EXTENSION) && extraSelectionConditionIsMet(fileName)) {
                    foundFiles.add(file);
                    System.out.printf("%s %s\n", fileName, "loaded");
                } else {
                    System.out.printf("%s was not loaded, doesn't match file-name format of %s\n",
                            fileName, getClass().getSimpleName());
                }
            }
        } else {
            String exceptionText = String.format("No files were found at %s", dirPath);
            throw new RuntimeException(exceptionText);
        }
        return foundFiles;
    }
    abstract boolean extraSelectionConditionIsMet(String fileName);
}