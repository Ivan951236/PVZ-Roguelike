package peppermint.gens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeedSlotGenerator {
    private Random random;
    
    // List of all possible plants
    private static final String[] PLANT_LIST = {
        "Peashooter", "Sunflower", "Cherry Bomb", "Wall-nut", "Potato Mine",
        "Snow Pea", "Chomper", "Repeater", "Puff-shroom", "Sun-shroom",
        "Fume-shroom", "Grave Buster", "Hypno-shroom", "Scaredy-shroom", 
        "Ice-shroom", "Doom-shroom", "Lily Pad", "Squash", "Threepeater",
        "Tangle Kelp", "Jalapeno", "Spikeweed", "Torchwood", "Tall-nut",
        "Sea-shroom", "Plantern", "Cactus", "Blover", "Split Pea",
        "Starfruit", "Pumpkin", "Magnet-shroom", "Cabbage-pult", "Flower Pot",
        "Kernel-pult", "Coffee Bean", "Garlic", "Umbrella Leaf", "Marigold",
        "Melon-pult", "Gatling Pea", "Twin Sunflower", "Gloom-shroom", 
        "Cattail", "Winter Melon", "Gold Magnet", "Spikerock", "Cob Cannon", 
        "Imitater"
    };
    
    public SeedSlotGenerator() {
        this.random = new Random();
    }
    
    /**
     * Generates seed slots based on the specified number (6, 7, 8, 9, or 10)
     * @param numSlots Number of seed slots (must be 6, 7, 8, 9, or 10)
     * @return List of plant names for the seed slots
     */
    public List<String> generateSeedSlots(int numSlots) {
        // Validate the number of slots
        if (numSlots < 6 || numSlots > 10) {
            throw new IllegalArgumentException("Number of seed slots must be between 6 and 10");
        }
        
        List<String> selectedPlants = new ArrayList<>();
        List<String> availablePlants = new ArrayList<>();
        
        // Create a copy of the plant list to avoid duplicates
        for (String plant : PLANT_LIST) {
            availablePlants.add(plant);
        }
        
        // Randomly select plants for the seed slots
        for (int i = 0; i < numSlots; i++) {
            if (availablePlants.isEmpty()) {
                // If we run out of unique plants, start over with the full list
                for (String plant : PLANT_LIST) {
                    availablePlants.add(plant);
                }
            }
            
            int randomIndex = random.nextInt(availablePlants.size());
            String selectedPlant = availablePlants.remove(randomIndex);
            selectedPlants.add(selectedPlant);
        }
        
        return selectedPlants;
    }
    
    public List<String> getAllPlants() {
        List<String> allPlants = new ArrayList<>();
        for (String plant : PLANT_LIST) {
            allPlants.add(plant);
        }
        return allPlants;
    }
    
    public static void main(String[] args) {
        SeedSlotGenerator generator = new SeedSlotGenerator();
        
        // Test the generator with different slot counts
        int[] slotCounts = {6, 7, 8, 9, 10};
        
        for (int slotCount : slotCounts) {
            System.out.println("Generated " + slotCount + " seed slots:");
            List<String> seeds = generator.generateSeedSlots(slotCount);
            for (int i = 0; i < seeds.size(); i++) {
                System.out.println("Slot " + (i+1) + ": " + seeds.get(i));
            }
            System.out.println();
        }
    }
}