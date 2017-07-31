package com.otg.worldpacker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

// To create your own worldpacker jar, do the following:
// 
// 1. Uncomment one of the following three @Mod lines and edit them for your worldpack:
// - Change modid to the internal name of your mod. Use lower-case and normal alphabet only.
// - Change name to the display (full) name of your mod.
// 2. Using a command prompt (cmd) run the command "gradle build" in the /project/ directory, this should generate a worldpacker-1.0.jar file in the /project/build/libs/ directory.
// 3. Using an archiving tool such as WinRar look inside the generated worldpacker-1.0.jar file and edit the following:
// - Replace the /assets/worldpacker/YourWorldDir/ directory with your world directory (containing the WorldBiomes and WorldObject directories).
// - Edit the mcmod.info file and fill in your mod's information.
// - When you're done rename the worldpacker-1.0.jar file to reflect your mod name + MC version + mod version, for instance: "MyWorldPack-1.10.2-v1.0"
// 4. That's it, copy your jar file a /mods/ directory and run the game!

// Uncomment this line if your worldpack requires only OpenTerrainGenerator
@Mod(modid = "myworldpackid", name = "My worldpack name", version = "1.0", acceptableRemoteVersions = "*", useMetadata = true, dependencies = "required-after:openterraingenerator")
// Uncomment and edit this line if your worldpack requires other mods or worldpacks.
//@Mod(modid = "myworldpackid", name = "My worldpack name", version = "1.0", acceptableRemoteVersions = "*", useMetadata = true, dependencies = "required-after:openterraingenerator;required-after:otgflatlands;required-after:otgskylands;required-after:otgvoid")
// Uncomment this line to run and test this mod from your development environment without requiring OTG. 
//@Mod(modid = "myworldpackid", name = "My worldpack name", version = "1.0", acceptableRemoteVersions = "*", useMetadata = true)
public class WorldPacker
{   
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
		try
		{  
			// Fetch the world files from this mod's own jar
			JarFile jarFile = new JarFile(FMLCommonHandler.instance().findContainerFor(this).getSource());
			Enumeration<JarEntry> entries = jarFile.entries();
			
			String worldName = null;
			ArrayList<JarEntry> srcWorldFilesInJar = new ArrayList<JarEntry>();
			
			while(entries.hasMoreElements())
			{
				JarEntry jarEntry = entries.nextElement();			    
				if(jarEntry.getName().startsWith("assets/worldpacker/") || jarEntry.getName().startsWith("assets\\worldpacker\\"))
				{		
					if(jarEntry.isDirectory() && !jarEntry.getName().equals("assets/worldpacker") && !jarEntry.getName().equals("assets\\worldpacker"))
					{
						File file = new File(jarEntry.getName());
						File parentFile = file.getParentFile();
						String parentFileName = parentFile.getAbsolutePath();
						if((parentFileName.endsWith("assets\\worldpacker") || parentFileName.endsWith("assets/worldpacker")) && worldName == null) // TODO: Check for each world if it already exists, not only the first one.
						{
							worldName = jarEntry.getName().replace("assets/worldpacker/", "").replace("/", "").replace("assets\\worldpacker\\", "").replace("\\", "");
						}
					}
					srcWorldFilesInJar.add(jarEntry);
				}
			}

			// Write the files to the output directory
    		if(worldName != null && srcWorldFilesInJar.size() > 0)
    		{
				File existingWorldConfig = new File(new File(".").getCanonicalPath() + File.separator + "mods" + File.separator + "OpenTerrainGenerator" + File.separator + "worlds" + File.separator + worldName + File.separator + "WorldConfig.ini");
		    	if(!existingWorldConfig.exists())
		    	{
		            for(JarEntry jarEntry : srcWorldFilesInJar)
		            {
			            File f = new File(new File(".").getCanonicalPath() + File.separator + "mods" + File.separator + "OpenTerrainGenerator" + File.separator + "worlds" + File.separator + jarEntry.getName().replace("assets/worldpacker", "").replace("assets\\worldpacker", ""));
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
			jarFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
}