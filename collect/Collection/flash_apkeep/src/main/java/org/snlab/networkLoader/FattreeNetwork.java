package org.snlab.networkLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.snlab.network.Device;
import org.snlab.network.Network;
import org.snlab.network.Rule;

public class FattreeNetwork {

    // Base directory for all data files
    private static final String BASE_DIR = "../data/FuXi_data/fattree/fattree48/";

    // Method to load the Fattree network from files
    public static Network getNetwork() {
        Network n = new Network("Fattree");

        // Add devices to the network from device_name file
        String[] devicenames = loadDeviceNames(BASE_DIR + "device_names");

        for (String name : devicenames) {
            Device device = n.addDevice(name);
            try {
                // Read configuration for each device from local files in the rule directory
                Scanner in = new Scanner(new File(BASE_DIR + "rule/" + name));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    String[] tokens = line.split(" ");
                    
                    // Handle firewall rules
                    if (tokens[0].equals("fw")) {
                        String pn = tokens[3].split("\\.")[0];
                        if (device.getPort(pn) == null) {
                            device.addPort(pn);
                        }
                        long ip = Long.parseLong(tokens[1]);
                        Rule rule = new Rule(device, ip, Integer.parseInt(tokens[2]), device.getPort(pn));
                        device.addInitialRule(rule);
                        n.addInitialRule(rule);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Load topology connections from the topology file
        loadTopologyConnections(n, BASE_DIR + "topology");

        // Assign unique IDs to all devices
        n.getAllDevices().forEach(device -> device.uid = Device.cnt++);

        return n;
    }

    // Helper method to load device names from the device_name file
    private static String[] loadDeviceNames(String filePath) {
        List<String> deviceNames = new ArrayList<>();
        try {
            Scanner in = new Scanner(new File(filePath));
            while (in.hasNextLine()) {
                deviceNames.add(in.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return deviceNames.toArray(new String[0]);
    }

    // Helper method to load topology connections from the topology file
    private static void loadTopologyConnections(Network n, String filePath) {
        try {
            Scanner in = new Scanner(new File(filePath));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] tokens = line.split(" ");
                if (tokens.length == 4) {
                    String device1 = tokens[0];
                    String port1 = tokens[1];
                    String device2 = tokens[2];
                    String port2 = tokens[3];
                    n.addLink(device1, port1, device2, port2);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
