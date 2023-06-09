package com.null8.GameEngine2D.util;

import com.null8.GameEngine2D.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Objects;

public class FileUtils {
    private FileUtils() {
    }

    public static String loadAsString(String file) {
        StringBuilder result = new StringBuilder();

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                    Main.class.getResourceAsStream("/assets/shaders/" + file)
            )));

            String buffer = "";

            while ((buffer = reader.readLine()) != null) {
                result.append(buffer).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


}
