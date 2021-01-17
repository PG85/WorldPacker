package com.otg.presetpacker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PresetUnpackUtil {
    public static void extractPreset(JarFile jarFile, String presetFolderPath) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        String presetName = null;
        ArrayList<JarEntry> srcWorldFilesInJar = new ArrayList<JarEntry>();

        while(entries.hasMoreElements())
        {
            JarEntry jarEntry = entries.nextElement();
            // Jar files only ever use the / separator, regardless of system
            if(jarEntry.getName().startsWith("assets/presetpacker/"))
            {
                if(jarEntry.isDirectory() && !jarEntry.getName().equals("assets/presetpacker"))
                {
                    File file = new File(jarEntry.getName());
                    File parentFile = file.getParentFile();
                    String parentFileName = parentFile.getAbsolutePath();
                    if(parentFileName.endsWith("presetpacker") && presetName == null) // TODO: Check for each world if it already exists, not only the first one.
                    {
                        presetName = jarEntry.getName().replace("assets/presetpacker/", "").replace("/", "");
                    }
                }
                srcWorldFilesInJar.add(jarEntry);
            }
        }

        // Write the files to the output directory
        if(presetName != null && srcWorldFilesInJar.size() > 0)
        {
            File existingWorldConfig = new File(presetFolderPath + presetName + File.separator + "WorldConfig.ini");
            if(!existingWorldConfig.exists())
            {
                for(JarEntry jarEntry : srcWorldFilesInJar)
                {
                    File f = new File(presetFolderPath + jarEntry.getName().replace("assets/presetpacker", ""));
                    if(jarEntry.isDirectory())
                    {
                        f.mkdirs();
                    } else {
                        f.createNewFile();
                        FileOutputStream fos = new FileOutputStream(f);
                        byte[] byteArray = new byte[1024];
                        int i;
                        java.io.InputStream is = jarFile.getInputStream(jarEntry);
                        while ((i = is.read(byteArray)) > 0)
                        {
                            fos.write(byteArray, 0, i);
                        }
                        is.close();
                        fos.close();
                    }
                }
            }
        }
    }
}
