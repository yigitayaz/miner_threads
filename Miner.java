import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Miner extends Agent{

    private static final ArrayList<Miner> instances = new ArrayList<Miner>();

    private final int  ID;
    //private final int  interval;
    private final int capacity;
    private final int oreType;
    private final int productionLimit;
    private int totalCount;

    private Condition isFull, isEmpty;

    public Miner(int ID, int interval, int capacity, int oreType, int productionLimit,HW2Logger logger){
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.oreType = oreType;
        this.productionLimit = productionLimit;
        this.counter = 0;
        totalCount = 0;
        this.logger = logger;
        lock = new ReentrantLock();
        isFull = lock.newCondition();
        isEmpty = lock.newCondition();
        isDead = false;
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
        lock.lock();
        try{
            counter++;
            totalCount++;
            isEmpty.signalAll();
        } finally{
            lock.unlock();
        }
    }
    private void MinerStopped(){
        isDead = true;
    }
    public static Miner getInstance(int ID){
        return instances.get(ID-1);
    }
    public static void addInstance(Miner miner){
        instances.add(miner);

    }
    public int getCounter(){
        return counter;
    }
    public void incrementCounter(int i){
        counter +=i;
    }
    public Lock getLock(){
        return lock;
    }
    public Condition getIsEmpty(){
        return isEmpty;
    }
    public Condition getIsFull(){
        return isFull;
    }

}
