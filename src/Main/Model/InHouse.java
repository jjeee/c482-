package Model;

public class InHouse extends Part {
    private int machineId;

    public InHouse(int id, String name, double price, int stock, int min, int max, int machineID) {
        super(id, name, price, stock, min, max);
        this.machineId=machineID;
    }

    public int getMachineId() { return machineId; }

    public void setMachineId(int machineId) { this.machineId = machineId; }
}
