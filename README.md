# Plants vs. Zombies Roguelike Preset Generator

## Overview
This application generates random presets for a Plants vs. Zombies roguelike game. It creates randomized level sequences, planting patterns, and seed slot selections to provide varied gameplay experiences

## Features
- Random level generation across 5 worlds (Day, Night, Pool, Fog, Roof)
- Multiple planting pattern options with detailed descriptions
- Configurable seed slot generation (6-10 slots)
- Support for endless mode presets
- Custom theme system with OpenPGP support
- VM detection to prevent running in virtualized environments

## Building the Project
This project uses Apache Ant for building. The build process creates multiple JAR libraries:

- `PepperMintLoader.jar` - Loader Library
- `PepperMintUI.jar` - GUI and Layout Library  
- `PepperMintGens.jar` - Generators Library
- `PepperMint.jar` - Main Library
- `PepperMintHater.jar` - Virtual Machine Detecting Library
- `PepperMintEncryption.jar` - Library with OpenPGP Compatibility
- `PepperMintARL.jar` - Archive Reading Library

To build the project, run:
```
ant build
```

To run the application, run:
```
ant run
```

## Project Structure
```
src/
└──main/
   └──java/
       └──peppermint/
           ├──Main.java
           ├──loader/
           │  └──Loader.java
           ├──ui/
           │  ├──MainWindow.java
           │  └──CustomThemeCreator.java
           ├──gens/
           │  ├──LevelGenerator.java
           │  ├──PatternGenerator.java
           │  ├──SeedSlotGenerator.java
           │  └──PresetGenerator.java
           ├──vm/
           │  └──VMDetector.java
           ├──crypto/
           │  └──OpenPGPEncryption.java
           ├──themes/
           │   └──ThemeManager.java
           └──archive/
               └──ArchiveReader.java
```

## Custom Themes
The application supports custom themes stored in the JAR Library's `PepperMintThemes` directory. Themes can be packaged as `.pmt` files (tar.xz archives) and may be signed with OpenPGP for security

Which the intended structure should be:

```
custom-theme.pmt/
├───mani.toml
├───openpgp-key.asc
└───theme.toml
```
This is a example with OpenPGP encryption

### Worlds and Levels
- Day (1-1 to 1-10)
- Night (2-1 to 2-10) 
- Pool (3-1 to 3-10)
- Fog (4-1 to 4-10)
- Roof (5-1 to 5-10)

### Planting Patterns
The application includes various planting patterns with different strategies:
- Classic: Basic defense with sun producers in front
- DSR: Double sun row strategy
- CB: Checkboard pattern
- Water Ski: For pool levels
- And many more with special variations

### Plant List
Full list of 49 Plants from the original PvZ game including Peashooter, Sunflower, Wall-nut, etc