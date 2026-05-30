package pbo.f01;

import java.util.*;

public class App {

    // =========================================================
    // ENTITIES (Plain Java - no JPA)
    // =========================================================

    static class ParkingArea {
        String name;
        int capacity;
        String allowedType;
        List<Vehicle> vehicles = new ArrayList<>();

        ParkingArea(String name, int capacity, String allowedType) {
            this.name = name;
            this.capacity = capacity;
            this.allowedType = allowedType;
        }

        boolean isFull()              { return vehicles.size() >= capacity; }
        boolean accepts(String type)  { return allowedType.equalsIgnoreCase(type); }
        int getOccupied()             { return vehicles.size(); }
    }

    static class Vehicle {
        String plateNumber;
        String owner;
        String type;
        ParkingArea parkingArea;

        Vehicle(String plateNumber, String owner, String type) {
            this.plateNumber = plateNumber;
            this.owner = owner;
            this.type = type;
        }
    }

    // =========================================================
    // IN-MEMORY STORAGE
    // =========================================================

    static Map<String, ParkingArea> areas    = new LinkedHashMap<>();
    static Map<String, Vehicle>     vehicles = new LinkedHashMap<>();

    // =========================================================
    // COMMANDS
    // =========================================================

    static void addArea(String name, int capacity, String allowedType) {
        if (areas.containsKey(name)) return;
        areas.put(name, new ParkingArea(name, capacity, allowedType));
    }

    static void addVehicle(String plateNumber, String owner, String type) {
        if (vehicles.containsKey(plateNumber)) return;
        vehicles.put(plateNumber, new Vehicle(plateNumber, owner, type));
    }

    static void park(String plateNumber, String areaName) {
        Vehicle vehicle = vehicles.get(plateNumber);
        if (vehicle == null) return;

        ParkingArea area = areas.get(areaName);
        if (area == null) return;

        if (!area.accepts(vehicle.type)) return;
        if (area.isFull()) return;

        vehicle.parkingArea = area;
        area.vehicles.add(vehicle);
    }

    static void displayAll() {
        // Sort areas by name ascending
        List<ParkingArea> sortedAreas = new ArrayList<>(areas.values());
        sortedAreas.sort(Comparator.comparing(a -> a.name));

        for (ParkingArea area : sortedAreas) {
            System.out.printf("%s %s %d|%d%n",
                    area.name, area.allowedType, area.capacity, area.getOccupied());

            // Sort vehicles by plate ascending
            List<Vehicle> sortedVehicles = new ArrayList<>(area.vehicles);
            sortedVehicles.sort(Comparator.comparing(v -> v.plateNumber));

            for (Vehicle v : sortedVehicles) {
                System.out.printf("%s %s %s%n", v.plateNumber, v.owner, v.type);
            }
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("#");
            String command = parts[0].trim();

            switch (command) {
                case "area-add":
                    if (parts.length >= 4)
                        addArea(parts[1], Integer.parseInt(parts[2].trim()), parts[3].trim());
                    break;

                case "vehicle-add":
                    if (parts.length >= 4)
                        addVehicle(parts[1].trim(), parts[2], parts[3].trim());
                    break;

                case "park":
                    if (parts.length >= 3)
                        park(parts[1].trim(), parts[2]);
                    break;

                case "display-all":
                    displayAll();
                    break;
            }
        }
    }
}