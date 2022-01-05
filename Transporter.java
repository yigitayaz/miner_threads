public class Transporter implements Runnable{
    private int ID;
    private int travelTime;
    private int minerID;
    private int sourceSmelterID;
    private int targetSmelterID;

    public Transporter(int ID, int travelTime, int minerID, int sourceSmelterID, int targetSmelterID) {
        this.ID = ID;
        this.travelTime = travelTime;
        this.minerID = minerID;
        this.sourceSmelterID = sourceSmelterID;
        this.targetSmelterID = targetSmelterID;

    }

    @Override
    public void run() {

    }
}
