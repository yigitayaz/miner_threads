import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Miner implements Runnable{
    private final int  ID;
    private final int  interval;
    private final int capacity;
    private final int oreType;
    private final int productionLimit;
    private int counter;
    private int totalCount;
    HW2Logger logger;

    private Lock lock;
    private Condition isFull, isEmpty;

    public Miner(int ID, int interval, int capacity, int oreType, int productionLimit,HW2Logger logger){
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.oreType = oreType;
        this.productionLimit = productionLimit;
        counter = 0;
        totalCount = 0;
        this.logger = logger;
        lock = new ReentrantLock();
        isFull = lock.newCondition();
        isEmpty = lock.newCondition();
    }

    @Override
    public void run() {
        logger.Log(ID,0,0,0,Action.MINER_CREATED);
        while(totalCount != productionLimit) {
            WaitCanProduce();
            logger.Log(ID, 0, 0, 0, Action.MINER_STARTED);
            sleep();
            logger.Log(ID,0,0,0,Action.MINER_FINISHED);
            OreProduced();

        }

        MinerStopped();
        logger.Log(ID,0,0,0,Action.MINER_STOPPED);
    }
    private void sleep(){
        Random random = new Random(System.currentTimeMillis());
        DoubleStream stream;
        stream = random.doubles(1, interval - interval * 0.01, interval + interval * 0.02);
        try {
            Thread.sleep((long) stream.findFirst().getAsDouble());
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    private  void WaitCanProduce(){
        lock.lock();
        try{
            while(capacity == counter){
                isFull.await();
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();

        }
        finally {
            lock.unlock();
        }

    }
    private void OreProduced(){

    }
    private void MinerStopped(){

    }
}
