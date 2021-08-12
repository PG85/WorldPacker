package com.otg.presetpacker;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PresetUnpackUtil {
    static Map<String, String> versionedPresetNames = new HashMap<>();

    public static boolean requiresErrorScreen = false;

    public static String thePreset = "";
    public static int majorVersion;
    public static int majorVersionBefore;

    public static int extractPreset(JarFile jarFile, String presetFolderPath, boolean isDedicatedServer) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        String presetName;
        Map<String, ArrayList<JarEntry>> srcWorldFilesInJar = new HashMap<>();
        int filesWritten = 0;
        // Get all versions & preset names
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.getName().contains("WorldConfig.ini")) {
                presetName = jarEntry.getName().split("/")[1];
                int[] parsedVersion = getVersions(new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry))));

                String versionedPresetName;
                // Code Authvin made that we don't use anymore
                /*if (presetName.matches("\\.+[ _-]v[0-9]+$")) {
                    versionedPresetName = presetName;
                } else {
                    versionedPresetName = presetName + " v" + parsedVersion[0];
                }*/
                versionedPresetName = presetName;
                thePreset = versionedPresetName;
                // Check if there's already a preset with this major version
                File oldDir = new File(presetFolderPath + versionedPresetName);
                boolean keepCurrentPreset = false;
                if (oldDir.exists()) {
                    // Compare minor version. If we're not newer, we won't be writing to file
                    int[] oldVersion = getVersions(new BufferedReader(new FileReader(presetFolderPath + versionedPresetName + File.separator + "WorldConfig.ini")));
                    if (parsedVersion[1] <= oldVersion[1] && parsedVersion[0] == oldVersion[0]) continue;
                    if (parsedVersion[0] > oldVersion[0]) {
                        if (isDedicatedServer) {
                            System.out.print("\u001B[33m");
                            System.out.println("\u001B[33m############# UPDATE SKIPPED #############");
                            System.out.println("\u001B[33mTried to update OTG preset " + versionedPresetName + " in " + oldDir.getAbsolutePath() + " but its version is too low.");
                            System.out.println("\u001B[33mUpdate skipped.");
                            System.out.println("\u001B[33mUpdating may lead to errors and seams for existing worlds using this preset.");
                            System.out.println("\u001B[33mTo force update, delete " + oldDir.getAbsolutePath() + ". Always back up your world save files before updating.");
                            System.out.println("\u001B[33m############# UPDATE NOTICE ############# ");
                            System.out.print("\u001B[0m");
                            keepCurrentPreset = true;
                        } else {
                            requiresErrorScreen = true;
                            keepCurrentPreset = true;
                            if (new File("./.REPLACE_ME").exists()) {
                                keepCurrentPreset = false;
                                requiresErrorScreen = false;
                                new File("./.REPLACE_ME").delete();
                            } else if (new File("./.DENY_PRESET_UPDATE").exists()) {
                                requiresErrorScreen = false;
                            }
                        }
                    }
                    majorVersion = parsedVersion[0];
                    majorVersionBefore = oldVersion[0];
                }

                if (!keepCurrentPreset) {
                    File backupDir = new File(oldDir.getParent() + "/" + presetName + "-BACKUP");
                    oldDir.renameTo(backupDir);
                    // We're gonna be transferring this preset to file
                    srcWorldFilesInJar.put(presetName, new ArrayList<>());
                    versionedPresetNames.put(presetName, versionedPresetName);
                }
            }
        }
        // At this point, srcWorldFilesInJar contains an arraylist per presetname in source file
        // versionedPresetNames contains versioned name for each presetname

        entries = jarFile.entries();
        // Get all files
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            // Jar files only ever use the / separator, regardless of system
            if (jarEntry.getName().startsWith("presets/") && !jarEntry.getName().equalsIgnoreCase("presets/")) {
                // This is a preset file/folder
                // Get the preset name to store it under
                // "assets/presetpacker/Biome Bundle/Biomes/Jungle.bc" gets shortened to "Biome Bundle"
                presetName = jarEntry.getName().split("/")[1];
                if (srcWorldFilesInJar.containsKey(presetName)) {
                    srcWorldFilesInJar.get(presetName).add(jarEntry);
                }
            }
        }

        // Write the files to the output directory
        for (String key : srcWorldFilesInJar.keySet()) {
            // We have already checked whether we will write all the presets in srcWorldFilesInJar, so let's just do that
            for (JarEntry entry : srcWorldFilesInJar.get(key)) {
                File f = new File(presetFolderPath + entry.getName().replace("presets/" + key, versionedPresetNames.get(key)));
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] byteArray = new byte[4096];
                    int i;
                    java.io.InputStream is = jarFile.getInputStream(entry);
                    while ((i = is.read(byteArray)) > 0) {
                        fos.write(byteArray, 0, i);
                    }
                    is.close();
                    fos.close();
                }
                filesWritten++;
            }
        }
        return filesWritten;
    }

    private static int[] getVersions(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Version:")) {
                break;
            }
        }
        if (line == null)
            return new int[]{0, 0};
        String v = line.split(":")[1];
        v = v.trim();
        if (line.contains(".")) {
            return new int[]{
                    Integer.parseInt(v.split("\\.")[0]),
                    Integer.parseInt(v.split("\\.")[1])
            };
        }
        return new int[]{
                Integer.parseInt(v),
                0
        };
    }
}
