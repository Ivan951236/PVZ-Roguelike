package peppermint.vm;

public class VMDetector {
    
    /**
     * Checks if the application is running inside a virtual machine
     * @return true if running in a VM, false otherwise
     */
    public static boolean isRunningInVirtualMachine() {
        // Check system properties that might indicate VM
        String vendor = System.getProperty("java.vm.vendor").toLowerCase();
        String name = System.getProperty("java.vm.name").toLowerCase();
        
        // Check for common VM indicators
        if (vendor.contains("virtualbox") || 
            vendor.contains("vmware") || 
            vendor.contains("microsoft") || 
            name.contains("virtualbox") || 
            name.contains("vmware")) {
            return true;
        }
        
        // Check for hypervisor bit in CPUID (on Windows)
        if (isWindows()) {
            return checkHypervisorBit();
        }
        
        // Check for VM-specific system properties
        if (isRunningInVMByProperties()) {
            return true;
        }
        
        // Check for VM-specific directories/files (Unix-like systems)
        if (isUnix() && isRunningInVMByFiles()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if the application should refuse to run based on is_spicy configuration
     * @param isSpicy Whether the current pack has spicy configuration enabled
     * @return true if app should not run (in VM with spicy config), false otherwise
     */
    public static boolean shouldRefuseToRun(boolean isSpicy) {
        if (isSpicy && isRunningInVirtualMachine()) {
            return true;
        }
        return false;
    }
    
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    private static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }
    
    private static boolean checkHypervisorBit() {
        // This is a simplified check - in a real implementation, this would
        // require native code or use of a library to check CPUID directly
        // For this prototype, we'll just check for known VM indicators in system properties
        String biosVendor = System.getProperty("sun.os.patch.level"); // This is just a placeholder
        
        // In a real implementation, we would check for hypervisor presence via CPUID
        // For now, we'll just return false as this requires native code
        return false;
    }
    
    private static boolean isRunningInVMByProperties() {
        // Check various system properties for VM indicators
        String[] vmIndicators = {
            "vbox", "virtualbox", "vmware", "virtual machine", 
            "hypervisor", "xen", "kvm", "qemu", "parallels"
        };
        
        // Check multiple system properties
        String[] propertiesToCheck = {
            "java.vm.vendor", "java.vm.name", 
            "java.vm.specification.vendor", "java.vm.info",
            "os.name", "os.version"
        };
        
        for (String property : propertiesToCheck) {
            String value = System.getProperty(property);
            if (value != null) {
                value = value.toLowerCase();
                for (String indicator : vmIndicators) {
                    if (value.contains(indicator)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private static boolean isRunningInVMByFiles() {
        // Check for VM-specific files on Unix-like systems
        String[] vmFiles = {
            "/sys/class/dmi/id/product_name",
            "/sys/class/dmi/id/sys_vendor",
            "/proc/scsi/scsi",
            "/proc/ide/hd0/model"
        };
        
        for (String filePath : vmFiles) {
            try {
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    java.util.Scanner scanner = new java.util.Scanner(file);
                    if (scanner.hasNextLine()) {
                        String content = scanner.nextLine().toLowerCase();
                        scanner.close();
                        
                        if (content.contains("virtualbox") || 
                            content.contains("vmware") || 
                            content.contains("virtual") || 
                            content.contains("innotek")) {
                            return true;
                        }
                    } else {
                        scanner.close();
                    }
                }
            } catch (Exception e) {
                // If we can't read the file, continue checking others
                continue;
            }
        }
        
        return false;
    }
    
    public static void main(String[] args) {
        System.out.println("Checking for VM...");
        System.out.println("Running in VM: " + isRunningInVirtualMachine());
        System.out.println("Should refuse to run (spicy=true): " + shouldRefuseToRun(true));
        System.out.println("Should refuse to run (spicy=false): " + shouldRefuseToRun(false));
    }
}