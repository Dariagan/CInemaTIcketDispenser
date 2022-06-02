import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.isNull;

public final class TheaterFile extends File {

    public TheaterFile (File file){
        super(file.getAbsolutePath());
    }

    public int getTheaterNumber(){

        String theaterFileName = this.getName();

        if (theaterFileName.startsWith(this.toString())) {
            return Character.getNumericValue(theaterFileName.charAt(this.toString().length()));
        }
        else throw new RuntimeException("Theater file name " + theaterFileName + " not recognized.");

    }

    public ArrayList<Seat> getSeats (TheaterFile file, int[] maxRows, int[] maxCols){
        try{
            String filePath = file.getAbsolutePath();
            java.io.FileReader fr = new java.io.FileReader(filePath);
            BufferedReader br  = new BufferedReader(fr);

            String line;

            ArrayList<Seat> foundSeats = new ArrayList<>();
            maxRows[0] = 0;

            int i;
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
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String toString(){
        return "Theater";
    }
}
