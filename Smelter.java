import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Smelter implements Runnable{

    private static final ArrayList<Smelter> instances = new ArrayList<Smelter>();

    private final int ID;
    private final int interval;
    private final int iCapacity;
    private final int oCapacity;
    private final int ingotType;
    private int oreCounter;
    private int ingotCounter;
    HW2Logger logger;

    private final Lock lock;
    private final Condition didNotArrive;
    private final Condition isFull;
    private final Condition isEmpty;

    public Smelter(int ID, int interval, int iCapacity, int oCapacity, int ingotType, HW2Logger logger) {
        this.ID = ID;
        this.interval = interval;
        this.iCapacity = iCapacity;
        this.oCapacity = oCapacity;
        this.ingotType = ingotType;
        this.oreCounter = 0;
        this.ingotCounter = 0;
        this.logger = logger;
        lock = new ReentrantLock();
        didNotArrive = lock.newCondition();
        isFull = lock.newCondition();
        isEmpty = lock.newCondition();
    }

    @Override
    public void run(){
        logger.Log(0,ID,0,0,Action.SMELTER_CREATED);
        while(/*there are active transporters*/){
            WaitCanProduce();
            logger.Log(0,ID,0,0,Action.SMELTER_STARTED);
            sleep();
            logger.Log(0,ID,0,0,Action.SMELTER_STOPPED);
            IngotProduced();
        }
        SmelterStopped();
        logger.Log(0,ID,0,0,Action.SMELTER_STOPPED);
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
    private void WaitCanProduce(){
        lock.lock();
        try{
            if(ingotType ==0){
                while(oreCounter <1){
                    didNotArrive.await();
                }

            }
            else if(ingotType == 1){
                while(oreCounter<2){
                    didNotArrive.await();
                }
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        finally{
            
            lock.unlock();
        }

    }
    private void IngotProduced(){

    }
    private void SmelterStopped(){

    }
    public static Smelter getInstance(int ID){
        return instances.get(ID-1);
    }
    public static void addInstance(Smelter smelter){
        instances.add(smelter);

    }

}
