# PresetPacker
A simple mod/plugin project used to package OTG preset configuration files as a mod/plugin (.jar file), just like Biome Bundle, Vanilla Vistas, Skylands etc.

How to use:
1. Download the source code (clone the repo, easy to do via git client)
2. Open build.gradle and edit where it says "Edit Here"
3. Replace the "MyPreset" folder under "presets" with your own preset
4. (Optional) If you want to use your own logo, replace the "logo.png" file under "src/main/resources/"
5. Run the "build_preset.bat" file
6. Find your preset jar in the "output" folder, you can rename it as you like

Make sure the MajorVersion/MinorVersion in your preset's WorldConfig match the version you put in build.gradle. These are used when users update to a new version of your preset. A higher major version number means that the update will cause problems for existing worlds, so users get a warning when updating. If only the minor version is higher, the update is considered safe so the existing preset files are updated automatically.

* Check that your mod is set up correctly by making sure it show up in the mods menu, and the mod logo is displayed in the OTG world creation menu.
