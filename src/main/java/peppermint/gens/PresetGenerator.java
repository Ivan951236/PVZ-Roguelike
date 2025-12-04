package peppermint.gens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PresetGenerator {
    private LevelGenerator levelGenerator;
    private PatternGenerator patternGenerator;
    private SeedSlotGenerator seedSlotGenerator;
    private Random random;
    
    public PresetGenerator() {
        this.levelGenerator = new LevelGenerator();
        this.patternGenerator = new PatternGenerator();
        this.seedSlotGenerator = new SeedSlotGenerator();
        this.random = new Random();
    }
    
    /**
     * Generates a complete preset with levels, patterns, and seed slots
     * @param numLevels Number of levels to generate (up to 12)
     * @param generateLevels Whether to generate levels (false for endless modes)
     * @param numSeedSlots Number of seed slots (6-10)
     * @param generatePatternForEveryLevel Whether to generate a pattern for each level or just once
     * @return Complete preset with levels, patterns, and seed slots
     */
    public Preset generatePreset(int numLevels, boolean generateLevels, int numSeedSlots, boolean generatePatternForEveryLevel) {
        Preset preset = new Preset();
        
        // Generate levels if requested
        if (generateLevels) {
            List<String> levels = levelGenerator.generateLevels(numLevels, false);
            preset.setLevels(levels);
            
            // Generate patterns
            if (generatePatternForEveryLevel) {
                List<String> patterns = new ArrayList<>();
                for (String level : levels) {
                    patterns.add(patternGenerator.generatePattern(level, false));
                }
                preset.setPatterns(patterns);
            } else {
                // Generate one pattern for all levels
                String pattern = patternGenerator.generatePattern(levels.get(0), true);
                List<String> patterns = new ArrayList<>();
                for (int i = 0; i < levels.size(); i++) {
                    patterns.add(pattern);
                }
                preset.setPatterns(patterns);
            }
        } else {
            // For endless modes, no levels are generated
            preset.setLevels(new ArrayList<>());
            preset.setPatterns(new ArrayList<>());
        }
        
        // Generate seed slots
        List<String> seedSlots = seedSlotGenerator.generateSeedSlots(numSeedSlots);
        preset.setSeedSlots(seedSlots);
        
        return preset;
    }
    
    /**
     * Generates a preset with random parameters
     * @return Complete preset with randomly generated parameters
     */
    public Preset generateRandomPreset() {
        // Randomly decide if levels should be generated (for endless modes)
        boolean generateLevels = random.nextBoolean();
        
        int numLevels = 0;
        if (generateLevels) {
            // Generate between 1 and 12 levels
            numLevels = random.nextInt(12) + 1;
        }
        
        // Randomly select number of seed slots (6-10)
        int numSeedSlots = random.nextInt(5) + 6;
        
        // Randomly decide if pattern should be generated for every level or just once
        boolean generatePatternForEveryLevel = random.nextBoolean();
        
        return generatePreset(numLevels, generateLevels, numSeedSlots, generatePatternForEveryLevel);
    }
    
    public static class Preset {
        private List<String> levels;
        private List<String> patterns;
        private List<String> seedSlots;
        
        public Preset() {
            this.levels = new ArrayList<>();
            this.patterns = new ArrayList<>();
            this.seedSlots = new ArrayList<>();
        }
        
        public List<String> getLevels() {
            return levels;
        }
        
        public void setLevels(List<String> levels) {
            this.levels = levels;
        }
        
        public List<String> getPatterns() {
            return patterns;
        }
        
        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }
        
        public List<String> getSeedSlots() {
            return seedSlots;
        }
        
        public void setSeedSlots(List<String> seedSlots) {
            this.seedSlots = seedSlots;
        }
        
        /**
         * Gets the complete preset as formatted strings for display
         * @return List of formatted strings representing the preset
         */
        public List<String> getFormattedPreset() {
            List<String> formatted = new ArrayList<>();
            
            // Add levels with their patterns
            for (int i = 0; i < levels.size(); i++) {
                formatted.add(levels.get(i));
                if (i < patterns.size()) {
                    formatted.add(patterns.get(i));
                }
                formatted.add(""); // Empty line for spacing
            }
            
            // Add seed slots
            formatted.add("SEED SLOTS:");
            for (int i = 0; i < seedSlots.size(); i++) {
                formatted.add(seedSlots.get(i));
            }
            
            return formatted;
        }
    }
    
    public static void main(String[] args) {
        PresetGenerator generator = new PresetGenerator();
        
        System.out.println("Generating random preset:");
        Preset preset = generator.generateRandomPreset();
        
        List<String> formattedPreset = preset.getFormattedPreset();
        for (String line : formattedPreset) {
            System.out.println(line);
        }
        
        System.out.println("\nGenerating preset with specific parameters:");
        Preset preset2 = generator.generatePreset(5, true, 8, true);
        
        List<String> formattedPreset2 = preset2.getFormattedPreset();
        for (String line : formattedPreset2) {
            System.out.println(line);
        }
    }
}