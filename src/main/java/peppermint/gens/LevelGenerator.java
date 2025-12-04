package peppermint.gens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelGenerator {
    private Random random;
    
    public LevelGenerator() {
        this.random = new Random();
    }
    
    public List<String> generateLevels(int numLevels, boolean canBeEndless) {
        List<String> levels = new ArrayList<>();
        
        if (canBeEndless) {
            // For endless modes, return an empty list
            return levels;
        }
        
        // Generate up to 12 levels
        int levelsToGenerate = Math.min(numLevels, 12);
        
        for (int i = 0; i < levelsToGenerate; i++) {
            levels.add(generateRandomLevel());
        }
        
        return levels;
    }
    
    private String generateRandomLevel() {
        // Select a world randomly
        int world = random.nextInt(5) + 1; // 1-5 for Day, Night, Pool, Fog, Roof
        int levelNum = random.nextInt(10) + 1; // 1-10 for each world
        
        switch (world) {
            case 1: // Day
                return formatLevel(levelNum, 1, "Day");
            case 2: // Night
                return formatLevel(levelNum, 2, "Night");
            case 3: // Pool
                return formatLevel(levelNum, 3, "Pool");
            case 4: // Fog
                return formatLevel(levelNum, 4, "Fog");
            case 5: // Roof
                return formatLevel(levelNum, 5, "Roof");
            default:
                return formatLevel(1, 1, "Day"); // Default case
        }
    }
    
    private String formatLevel(int levelNum, int worldNum, String worldName) {
        return worldNum + "-" + levelNum + " (" + worldName + ")";
    }
    
    public List<String> getAllPossibleLevels() {
        List<String> allLevels = new ArrayList<>();
        
        // Add all possible levels for each world
        for (int world = 1; world <= 5; world++) {
            String worldName = getWorldName(world);
            for (int level = 1; level <= 10; level++) {
                allLevels.add(world + "-" + level + " (" + worldName + ")");
            }
        }
        
        return allLevels;
    }
    
    private String getWorldName(int worldNum) {
        switch (worldNum) {
            case 1: return "Day";
            case 2: return "Night";
            case 3: return "Pool";
            case 4: return "Fog";
            case 5: return "Roof";
            default: return "Day";
        }
    }
    
    public static void main(String[] args) {
        LevelGenerator generator = new LevelGenerator();
        
        // Test the generator
        List<String> levels = generator.generateLevels(12, false);
        System.out.println("Generated Levels:");
        for (String level : levels) {
            System.out.println(level);
        }
    }
}