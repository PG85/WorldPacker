# WorldPacker
A project that can be used by OTG worldpack creators to package their worldpack as a seperate mod, just like Biome Bundle, Skylands, Flatlands and Void..

How to use:
1. Download the source code and run "gradle setupDecompWorkspace" in the directory. This may fail because of a time-out, if so try a couple of times. The default MC version is 1.10.2, you can change this in the build.gradle file before running the "gradle setupDecompWorkSpace" command.
2. Create a new eclipse project (or another IDE of your preference) and import the project.
3. (Optional) If you want to run the project from Eclipse create a run configuration for the project with main class "GradleStart".
4. Follow the instructions at the top of WorldPacker.java to customise and build the mod jar.
