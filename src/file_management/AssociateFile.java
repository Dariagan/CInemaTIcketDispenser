package file_management;

import cinema.Seat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.TreeSet;

import static java.util.Objects.isNull;

public class AssociateFile extends File {

    public AssociateFile (File file){
        super(file.getAbsolutePath());
    }

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
