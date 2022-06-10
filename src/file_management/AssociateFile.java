package file_management;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashSet;

import static java.util.Objects.isNull;

public final class AssociateFile extends File {

    AssociateFile (File file){
        super(file.getAbsolutePath());
    }

    /**
     * Gets all the associates' credit card numbers in the file by removing all the spaces in each of the read lines of
     * the file and then parsing it into <code>Long</code>, which is then added to the <code>HashSet</code> of
     * <code>Long</code> to be returned.
     * @return set containing all the found credit card numbers
     */
    public HashSet<Long> getAssociates(){

        try {
            HashSet<Long> foundAssociates = new HashSet<>();

            java.io.FileReader fr = new java.io.FileReader(this.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);

            String line;

            while (!isNull(line = br.readLine())) {
                if (!line.isBlank()) {
                    line = line.replaceAll(" ", "");
                    foundAssociates.add(Long.parseLong(line));
                }
            }

            br.close();
            fr.close();

            return foundAssociates;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
