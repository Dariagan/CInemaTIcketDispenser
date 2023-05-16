package file_management;


public final class AssociateFilesFactory extends FilesFactory<AssociateFile>{

    public AssociateFilesFactory() {
        super("associates", ".txt");
    }

    @Override
    boolean extraSelectionConditionIsMet(String fileName) {
        return true;
    }
}
