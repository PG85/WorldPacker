# WorldPacker
A project that can be used by OTG worldpack creators to package their worldpack as a seperate mod, just like Biome Bundle, Skylands, Flatlands and Void..

How to use:
1. Download the source code and run "gradle setupDecompWorkspace" and "gradle eclipse" in the directory. This may fail because of a time-out, if so try a couple of times. 
2. The default MC version is 1.10.2, you can change this to 1.11.2 in the build.gradle file by changing version = "1.10.2-12.18.3.2185" to version = "1.11.2-13.20.0.2315" and mappings = "snapshot_20161111" to mappings = "snapshot_20170430". After you do this run "gradleSetupDecompWorkspace" and "gradle eclipse" again.
3. (Optional) Create a new eclipse workspace (or another IDE of your preference) and import the project.
4. (Optional) If you want to run the project from Eclipse create a run configuration for the project with main class "GradleStart".
5. Follow the instructions at the top of WorldPacker.java to customise and build the mod jar.
6. Using a command prompt (cmd) run the command "gradle build" in the /project/ directory, this should generate a worldpacker-1.0.jar file in the /project/build/libs/ directory.
7. Using an archiving tool such as WinRar look inside the generated worldpacker-1.0.jar file and edit the following:
- Replace the /assets/worldpacker/YourWorldDir/ directory with your world directory (containing the WorldBiomes and WorldObject directories).
- Edit the mcmod.info file and fill in your mod's information.
- When you're done rename the worldpacker-1.0.jar file to reflect your mod name + MC version + mod version, for instance: "MyWorldPack-1.10.2-v1.0"
8. That's it, copy your jar file a /mods/ directory and run the game!
