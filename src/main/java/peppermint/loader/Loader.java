package peppermint.loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    private List<String> libraryPaths;
    
    public Loader() {
        libraryPaths = new ArrayList<>();
        initializeLibraryPaths();
    }
    
    private void initializeLibraryPaths() {
        // Add paths to all the JAR libraries
        libraryPaths.add("dist/PepperMintUI.jar");
        libraryPaths.add("dist/PepperMintGens.jar");
        libraryPaths.add("dist/PepperMintHater.jar");
        libraryPaths.add("dist/PepperMintEncryption.jar");
        libraryPaths.add("dist/PepperMintARL.jar");
    }
    
    public void loadLibraries() {
        try {
            List<URL> urls = new ArrayList<>();
            
            for (String libPath : libraryPaths) {
                File file = new File(libPath);
                if (file.exists()) {
                    urls.add(file.toURI().toURL());
                } else {
                    System.out.println("Warning: Library file not found: " + libPath);
                }
            }
            
            URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), 
                                                            getClass().getClassLoader());
            
            Thread.currentThread().setContextClassLoader(classLoader);
            System.out.println("All libraries loaded successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}