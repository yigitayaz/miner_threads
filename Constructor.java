public class Constructor implements Runnable{
    private int ID;
    private int interval;
    private int capacity;
    private int ingotType;

    public Constructor(int ID, int interval, int capacity, int ingotType) {
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.ingotType = ingotType;
    }

    @Override
    public void run() {

    }
}
