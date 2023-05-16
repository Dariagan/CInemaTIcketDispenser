package file_management;

import cinema.Seat;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import static java.util.Objects.isNull;

public final class TheaterFile extends File {

    TheaterFile(File file) {
        super(file.getAbsolutePath());
    }

    /**
     * Reads the theater's number from its file name
     * @return the number of the theater put in the file name.
     * @throws RuntimeException if the theater file name is not adequate to be read from
     */
    public int getTheaterNumber(){

        String fileName = this.getName();

        final String THEATER_FILE_NAME = "Theater";

        if (fileName.startsWith(THEATER_FILE_NAME)) {
            return Character.getNumericValue(fileName.charAt(THEATER_FILE_NAME.length()));
        }
        else throw new RuntimeException("Theater file name " + fileName + " not recognized.");
    }

    /**
     * <p>Reads through all the asterisks and spaces from the theater file to obtain the position of all the existent seats,
     * and add all the instances of <code>Seat</code> constructed from their position to a set, which is finally returned.</p>
     * <p>Sets value of the first position of the passed arrays <code>maxRows</code> and <code>maxCols</code> to
     * the maximum number of row and columns found in the file, respectively.</p>
     *
     * @param file read theater file
     * @param maxRows array used to pass the maximum number of rows by reference, to its first position.
     * @param maxCols array used to pass the maximum number of columns by reference, to its first position.
     * @return <p>set of existent seats (excluding empty tiles)</p>
     */
    public TreeSet<Seat> getSeats (TheaterFile file, int[] maxRows, int[] maxCols){
        try{
            String filePath = file.getAbsolutePath();
            java.io.FileReader fr = new java.io.FileReader(filePath);
            try (BufferedReader br = new BufferedReader(fr)) {
                TreeSet<Seat> foundSeats = new TreeSet<>();
                maxRows[0] = 0;
                int i; String line;
                for(i = 1; !isNull(line = br.readLine()); i++){
                    int j;
                    for (j = 1; j <= line.length(); j++){
                        if (line.charAt(j-1) == '*'){
                            foundSeats.add(new Seat(i, j));
                        }
                    }
                    maxCols[0] = Math.max(maxCols[0], j - 1);
                }
                maxRows[0] = i - 1;

                br.close();
                fr.close();
                return foundSeats;
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
