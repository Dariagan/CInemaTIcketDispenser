import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import static java.util.Objects.isNull;

public class AssociateFile extends File {

    public AssociateFile (File file){
        super(file.getAbsolutePath());
    }

    public long[] getAssociateArray(){//todo volver list de Long

        String filePath = this.getAbsolutePath();
        try {
            java.io.FileReader fr = new java.io.FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            int i=0; String line;
            while(!isNull(line = br.readLine())) {
                if (!line.isBlank()) i++;
            }
            //todo chequear si i al final vale 1 de más
            long [] associates = new long[i];

            fr = new java.io.FileReader(filePath);
            br = new BufferedReader(fr);

            for (i = 0; !isNull(line = br.readLine()); i++) {
                line = line.replaceAll(" ", "");
                associates[i] = Long.parseLong(line);
            }
            br.close();
            fr.close();
            return associates;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
