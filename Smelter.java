public class Smelter implements Runnable{
    private int ID;
    private int interval;
    private int iCapacity;
    private int oCapacity;
    private int ingotType;

    public Smelter(int ID, int interval, int iCapacity, int oCapacity, int ingotType) {
        this.ID = ID;
        this.interval = interval;
        this.iCapacity = iCapacity;
        this.oCapacity = oCapacity;
        this.ingotType = ingotType;
    }

    @Override
    public void run(){

    }
}
