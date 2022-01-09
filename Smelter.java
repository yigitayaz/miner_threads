import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Smelter extends Agent{

    private static final ArrayList<Smelter> instances = new ArrayList<Smelter>();
    private final int ID;
    private final int iCapacity;
    private final int oCapacity;
    private final int ingotType;
    private int oreCounter;

    private final Condition canProduce;
    private final Condition isFull;
    private final Condition isEmpty;

    public Smelter(int ID, int interval, int iCapacity, int oCapacity, int ingotType, HW2Logger logger) {
        this.ID = ID;
        this.interval = interval;
        this.iCapacity = iCapacity;
        this.oCapacity = oCapacity;
        this.ingotType = ingotType;
        this.oreCounter = 0;
        this.counter = 0;
        this.logger = logger;
        lock = new ReentrantLock();
        canProduce = lock.newCondition();
        isFull = lock.newCondition();
        isEmpty = lock.newCondition();
        isDead = false;
    }

    @Override
    public void run(){
        logger.Log(0,ID,0,0,Action.SMELTER_CREATED);
        while(checkRawCount() || activeTransporters()){
            WaitCanProduce();
            logger.Log(0,ID,0,0,Action.SMELTER_STARTED);
            sleep();
            logger.Log(0,ID,0,0,Action.SMELTER_FINISHED);
            IngotProduced();
        }
        SmelterStopped();
        logger.Log(0,ID,0,0,Action.SMELTER_STOPPED);
    }

    private void WaitCanProduce(){
        lock.lock();
        try{
            if(ingotType ==0){
                while(oreCounter <1 || counter>= oCapacity){
                    canProduce.await();
                }

            }
            else if(ingotType == 1){
                while(oreCounter<2 || counter>= oCapacity ){
                    canProduce.await();
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
        lock.lock();
        try{
            isFull.signalAll();
            isEmpty.signalAll();
        }
        finally{
            if(ingotType == 1) oreCounter -=2;
            else oreCounter--;
            counter++;
            lock.unlock();
        }
    }
    private void SmelterStopped(){
        isDead = true;
    }
    public static Smelter getInstance(int ID){
        return instances.get(ID-1);
    }
    public static void addInstance(Smelter smelter){
        instances.add(smelter);

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
    public Condition getCanProduce(){
        return canProduce;
    }
    public int getOreCounter(){
        return oreCounter;
    }
    public void incrementOreCounter(int amount){
        oreCounter += amount;
    }
    public int getIngotCounter(){
        return counter;
    }
    public void incrementIngotCounter(int amount){
        counter += amount;
    }
    public int getOreCapacity(){
        return oCapacity;
    }
    private boolean activeTransporters(){
        ArrayList<Transporter> list = Transporter.getList();
        for(Transporter t : list){
            if(t.getTargetSmelterID() == ID){
                if(!t.isDead){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkRawCount(){
        if(ingotType == 0){
            if(oreCounter ==0) return false;
            else return true;
        }
        else{
            if(oreCounter <2) return false;
            else return true;
        }
    }
}
