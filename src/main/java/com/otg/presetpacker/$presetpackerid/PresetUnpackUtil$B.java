package com.otg.presetpacker.$presetpackerid;

import java.io.*;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PresetUnpackUtil$B {
    public List<String> PRESET_NAME = new ArrayList<>();
    public List<Integer> NEW_MAJOR_VERSION = new ArrayList<>();
    public List<Integer> OLD_MAJOR_VERSION = new ArrayList<>();
    public List<Path> PRESET_PATH = new ArrayList<>();

    private static int[] getVersions(BufferedReader reader) throws IOException {
        int[] arr = new int[] {0,0};
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("MajorVersion:")) {
                arr[0] = Integer.parseInt(line.split(":")[1].trim());
            }
            if (line.contains("MinorVersion:")) {
                arr[1] = Integer.parseInt(line.split(":")[1].trim());
            }
        }
        return arr;
    }
}
