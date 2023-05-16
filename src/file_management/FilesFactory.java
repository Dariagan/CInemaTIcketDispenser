package file_management;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class FilesFactory <T extends File>{

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
     * Returns a list of files whose file extension is the same as the factory's established, and which meet the
     * criteria established by the sub-factory' implementation of the
     * <code>extraSelectionConditionIsMet(String fileName)</code> method.
     *
     * @return list of files which match the file extension, and the sub-factory's selection condition
     */
    public ArrayList<T> getMatchingFiles() {

        String dirPath = System.getProperty("user.dir") + "\\data\\" + FILES_FOLDER;
        File dataFolder = new File(dirPath);

        File[] readFiles = dataFolder.listFiles();
        ArrayList<T> foundFiles = new ArrayList<>();

        if (readFiles != null) {
            for (File file : readFiles) {
                String fileName = file.getName();
                if (fileName.endsWith(FILE_EXTENSION) && extraSelectionConditionIsMet(fileName)) {
                
                    T newFile = createInstance(file);
                    foundFiles.add(newFile);
                    
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

    private T createInstance(File file) {
        try {
            Class<?> clazz = Class.forName(getClassName());
            Constructor<?> constructor = clazz.getDeclaredConstructor(File.class);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(file);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create instance of type T.", e);
        }
    }

    private String getClassName() {
        return getClass().getGenericSuperclass().getTypeName()
                .replaceAll(".*<(.+)>", "$1");
    }
}