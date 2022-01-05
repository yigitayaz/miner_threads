
public class Miner implements Runnable{
    private int ID;
    private int interval;
    private int capacity;
    private int oreType;
    private int productionLimit;
    private int counter;

    public Miner(int ID, int interval, int capacity, int oreType, int productionLimit){
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.oreType = oreType;
        this.productionLimit = productionLimit;
        counter = 0;
    }

    @Override
    public void run() {

    }
}
