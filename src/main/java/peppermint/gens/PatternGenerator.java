package peppermint.gens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatternGenerator {
    private Random random;
    
    // Regular patterns (higher chance to generate)
    private static final String[] REGULAR_PATTERNS = {
        "Classic", "SLS+DRS", "DSR", "DSR+3R", "CB", 
        "Classic+LS", "Classic+ILS", "DSR+LS", "DSR+LS+3R", 
        "DSR+ILS", "DSR+ILS+3R", "MR+Classic", "MR+DSR", "MR+DSR+3R"
    };
    
    // Inverted patterns (lower chance to generate)
    private static final String[] INVERTED_PATTERNS = {
        "Inverted Classic", "Inverted DSR", "Inverted DSR+3R"
    };
    
    // Water ski patterns (only for pool levels)
    private static final String[] WATER_SKI_PATTERNS = {
        "Water Ski", "Water Ski DSR", "Water Ski DSR+3R"
    };
    
    // Inverted water ski patterns (only for pool levels, lower chance)
    private static final String[] INVERTED_WATER_SKI_PATTERNS = {
        "Inverted Water Ski", "Inverted Water Ski DSR", "Inverted Water Ski DSR+3R"
    };
    
    // Corner suns patterns
    private static final String[] CORNER_SUNS_PATTERNS = {
        "Corner Suns S", "Corner Suns", "Corner Suns L", "Corner Suns XL"
    };
    
    // Zombie chewer patterns (special conditions)
    private static final String[] ZOMBIE_CHEWER_PATTERNS = {
        "Zombie Chewer", "Zombie Chewer Xtreme"
    };
    
    public PatternGenerator() {
        this.random = new Random();
    }
    
    public String generatePattern(String level, boolean forAllLevels) {
        // Check if level is a pool level
        boolean isPoolLevel = level.contains("(Pool)");
        
        List<String> availablePatterns = new ArrayList<>();
        
        // Add regular patterns (these are always available)
        for (String pattern : REGULAR_PATTERNS) {
            availablePatterns.add(pattern);
        }
        
        // Add inverted patterns (lower chance)
        if (random.nextInt(100) < 20) { // 20% chance to include inverted patterns
            for (String pattern : INVERTED_PATTERNS) {
                availablePatterns.add(pattern);
            }
        }
        
        // Add water ski patterns if it's a pool level
        if (isPoolLevel) {
            for (String pattern : WATER_SKI_PATTERNS) {
                availablePatterns.add(pattern);
            }
            
            // Add inverted water ski patterns (lower chance)
            if (random.nextInt(100) < 20) { // 20% chance to include inverted water ski patterns
                for (String pattern : INVERTED_WATER_SKI_PATTERNS) {
                    availablePatterns.add(pattern);
                }
            }
        }
        
        // Add corner suns patterns
        for (String pattern : CORNER_SUNS_PATTERNS) {
            availablePatterns.add(pattern);
        }
        
        // Special conditions for zombie chewer patterns
        // These would be added based on specific conditions in a real implementation
        if (random.nextInt(100) < 10) { // 10% chance for special patterns
            for (String pattern : ZOMBIE_CHEWER_PATTERNS) {
                availablePatterns.add(pattern);
            }
        }
        
        // Select a random pattern from available patterns
        int index = random.nextInt(availablePatterns.size());
        return availablePatterns.get(index);
    }
    
    public List<String> getAllPatterns() {
        List<String> allPatterns = new ArrayList<>();
        
        // Add all pattern types to the list
        for (String pattern : REGULAR_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        for (String pattern : INVERTED_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        for (String pattern : WATER_SKI_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        for (String pattern : INVERTED_WATER_SKI_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        for (String pattern : CORNER_SUNS_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        for (String pattern : ZOMBIE_CHEWER_PATTERNS) {
            allPatterns.add(pattern);
        }
        
        return allPatterns;
    }
    
    public String getPatternDescription(String patternName) {
        switch (patternName) {
            case "Classic":
                return "One Row of sun producing plants (Sunflower, Sunshroom) and another in front of sun producing plants which are the defense. Classic also has five variants, Classic+LS is the landscape version of Classic, where sun producing plants are placed on the top sections only, defense goes in other sections below the sun producing plants, Classic+ILS is like Classic+LS, other than you place sun producing plants at the bottom section, instead of the top, other sections are defense, in Classic+LS and Classic+ILS, the last two rows are always for defense. Inverted Classic has you put Sunflowers at the end of the front yard/back yard instead of at the beginning of front yard/back yard, and MR+Classic have you put your sunflower in the middle row instead of the first row, it is preferrable to place the defense in front the sun producing plants";
            case "SLS+DRS":
                return "Left side is just for sun producing plants, the other is for defense";
            case "DSR":
                return "It is like Classic, other than we have two rows of sun producing plants, and we have other rows for defense, DSR+3R variant gives you the third row for defense, the MR+DSR and MR+DSR+3R variants have you put sunflowers in the middle row instead of the first row";
            case "CB":
                return "Checkboard pattern where white squares are sun producing plants, black squares are defense";
            case "Water Ski":
                return "Generated only when the level generated is a Pool Level, and we generate one pattern for every level, instead of one pattern for all, Water Ski has you put 3 Lily pads on water and sun producing plants on the first two lily pads and defense on the last lily pad, then you place more lily pads and defense on those lily pads, Water Ski also has a DSR variant and also a Inverted variant too";
            case "Corner Suns":
                return "You place sun producing plants on a 2x2 corners at the start of front yard/back yard of top and bottom sections only, the defense is always on the middle section and on last two rows, it has 3 variants, Corner Suns S is 1x2 corners instead, Corner Suns L is 3x2 corners instead and Corner Suns XL is 4x2 corners instead";
            case "Zombie Chewer":
                return "For every All-Star Zombie, you have 3 seconds to place a sun producing plant in front of a zombie, the Xtreme variant allows you to place any plant in front of a zombie";
            case "Inverted Classic":
            case "Inverted DSR":
            case "Inverted DSR+3R":
                return "Inverted pattern with lower chance to generate than regular patterns";
            case "Water Ski DSR":
            case "Water Ski DSR+3R":
                return "DSR variant of Water Ski pattern for Pool levels";
            case "Inverted Water Ski":
            case "Inverted Water Ski DSR":
            case "Inverted Water Ski DSR+3R":
                return "Inverted Water Ski patterns for Pool levels with lower chance to generate";
            case "Classic+LS":
            case "Classic+ILS":
                return "Landscape variants of Classic pattern";
            case "DSR+3R":
            case "DSR+LS":
            case "DSR+LS+3R":
            case "DSR+ILS":
            case "DSR+ILS+3R":
                return "DSR variants with additional rows or layouts";
            case "MR+Classic":
            case "MR+DSR":
            case "MR+DSR+3R":
                return "Middle Row variants of Classic and DSR patterns";
            case "Corner Suns S":
            case "Corner Suns L":
            case "Corner Suns XL":
                return "Corner Suns pattern variants with different corner sizes";
            case "Zombie Chewer Xtreme":
                return "Extreme variant of Zombie Chewer that allows placing any plant in front of zombies";
            default:
                return "Description not available for this pattern";
        }
    }
    
    public static void main(String[] args) {
        PatternGenerator generator = new PatternGenerator();
        
        // Test the generator
        String[] testLevels = {"1-1 (Day)", "2-5 (Night)", "3-3 (Pool)", "4-7 (Fog)", "5-9 (Roof)"};
        
        System.out.println("Generated Patterns:");
        for (String level : testLevels) {
            String pattern = generator.generatePattern(level, true);
            System.out.println(level + " -> " + pattern);
        }
    }
}