package file_management;

public final class MovieFilesFactory extends FilesFactory<MovieFile>{

    public MovieFilesFactory() {
        super("movies", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }
}
