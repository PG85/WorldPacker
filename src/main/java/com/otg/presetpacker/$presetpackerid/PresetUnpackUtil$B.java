package com.otg.presetpacker.$presetpackerid;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PresetUnpackUtil$B {
    public List<String> PRESET_NAME = new ArrayList<>();
    public List<Integer> NEW_MAJOR_VERSION = new ArrayList<>();
    public List<Integer> OLD_MAJOR_VERSION = new ArrayList<>();
    public List<Path> PRESET_PATH = new ArrayList<>();

    public int extractPreset(JarFile jarFile, String presetFolderPath, boolean isDedicatedServer) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        String presetName;
        Map<String, ArrayList<JarEntry>> srcWorldFilesInJar = new HashMap<>();
        int filesWritten = 0;
        // Get all versions & preset names
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.getName().contains("WorldConfig.ini")) {
                presetName = jarEntry.getName().split("/")[1];
                int[] newVersion = getVersions(new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry))));
                // Check if there's already a preset with this major version
                File oldInstallDir = new File(presetFolderPath + presetName);
                if (oldInstallDir.exists()) {
                    // Compare minor version. If we're not newer, we won't be writing to file
                    int[] installedVersion = getVersions(new BufferedReader(new FileReader(presetFolderPath + presetName + File.separator + "WorldConfig.ini")));
                    // Installed preset is newer or equal
                    if (newVersion[1] <= installedVersion[1] && newVersion[0] <= installedVersion[0]) continue;
                    // Installed preset is an older major version
                    if (newVersion[0] > installedVersion[0]) {
                        // We're on a server and cannot error screen - post a message in the logs, then don't update
                        if (isDedicatedServer) {
                            System.out.print("\u001B[33m");
                            System.out.println("\u001B[33m############# UPDATE SKIPPED #############");
                            System.out.println("\u001B[33mFailed to update OTG preset " + presetName + " in " + oldInstallDir.getAbsolutePath() + " but it's too old to be updated.");
                            System.out.println("\u001B[33mUpdating may lead to errors and seams for existing worlds using this preset.");
                            System.out.println("\u001B[33mTo force update, delete " + oldInstallDir.getAbsolutePath() + ". Always back up your world save files before updating.");
                            System.out.print("\u001B[0m");
                            continue;
                        }
                        // We've been here before, and placed a REPLACE_ME file
                        if (oldInstallDir.toPath().resolve(".REPLACE_ME").toFile().exists()) {
                            new File("./.REPLACE_ME").delete();
                        }
                        // We've been here before, and we're not updating the preset
                        else if (oldInstallDir.toPath().resolve(".DENY_PRESET_UPDATE").toFile().exists()) {
                            continue;
                        }
                        // We haven't been here before, and we're giving the user an error screen
                        else {
                            PRESET_NAME.add(presetName);
                            NEW_MAJOR_VERSION.add(newVersion[0]);
                            OLD_MAJOR_VERSION.add(installedVersion[0]);
                            PRESET_PATH.add(oldInstallDir.toPath());
                            continue;
                        }
                    }
                }
                // TODO: This gives access errors
//                Path destinationPath = oldInstallDir.toPath();
//                Path backupPath = oldInstallDir.toPath().getParent().resolveSibling(presetName+"-backup");
//                try {
//                    if (backupPath.toFile().exists()) {
//                        Files.walk(backupPath)
//                                .sorted(Comparator.reverseOrder())
//                                .map(Path::toFile)
//                                .forEach(File::delete);
//                    }
//                    Files.move(destinationPath, backupPath);
//                } catch (IOException e) {
//                    System.out.println("Failed to rename old file "+destinationPath.getFileName());
//                    e.printStackTrace();
//                }

                // We're gonna be transferring this preset to file
                srcWorldFilesInJar.put(presetName, new ArrayList<>());
            }
        }
        // At this point, srcWorldFilesInJar contains an arraylist per presetname in source file
        // versionedPresetNames contains versioned name for each presetname

        entries = jarFile.entries();
        // Get all files
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            // Jar files only ever use the / separator, regardless of system
            if (jarEntry.getName().startsWith("Presets/") && !jarEntry.getName().equalsIgnoreCase("presets/")) {
                // This is a preset file/folder
                // Get the preset name to store it under
                // "Presets/Biome Bundle/Biomes/Jungle.bc" gets shortened to "Biome Bundle"
                presetName = jarEntry.getName().split("/")[1];
                if (srcWorldFilesInJar.containsKey(presetName)) {
                    srcWorldFilesInJar.get(presetName).add(jarEntry);
                }
            }
        }
        Path presetInstallPath = new File(presetFolderPath).toPath().getParent();
        // Write the files to the output directory
        for (String key : srcWorldFilesInJar.keySet()) {
            // We have already checked whether we will write all the presets in srcWorldFilesInJar, so let's just do that
            for (JarEntry entry : srcWorldFilesInJar.get(key)) {
                File f = presetInstallPath.resolve(entry.getName()).toFile();
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
