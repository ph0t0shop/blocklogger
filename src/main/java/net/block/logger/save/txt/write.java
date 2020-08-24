package net.block.logger.save.txt;

import java.io.FileWriter;
import java.io.IOException;

public class Write {

    public static void writeToFile(String data, String path) throws IOException {
        
        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(data);
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}