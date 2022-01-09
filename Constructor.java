import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Constructor extends Agent{

    private static final ArrayList<Constructor> instances = new ArrayList<Constructor>();
    private final int ID;
    private final int capacity;
    private final int ingotType;

    private final Condition canProduce,isFull;

    public Constructor(int ID, int interval, int capacity, int ingotType, HW2Logger logger) {
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.ingotType = ingotType;
        this.logger = logger;
        lock = new ReentrantLock();
        canProduce = lock.newCondition();
        isFull = lock.newCondition();
        counter =0;
        isDead = false;
    }

    @Override
    public void run() {
        logger.Log(0,0,0,ID,Action.CONSTRUCTOR_CREATED);
        while(checkRawCount() || activeTransporters()){
            WaitCanProduce();
            logger.Log(0,0,0,ID,Action.CONSTRUCTOR_STARTED);
            sleep();
            logger.Log(0,0,0,ID,Action.CONSTRUCTOR_FINISHED);
            ConstructorProduced();
        }
        logger.Log(0,0,0,ID,Action.CONSTRUCTOR_STOPPED);
    }

    private void WaitCanProduce(){
        lock.lock();
        try{
            if(ingotType ==1){
                while(counter < 1){
                    canProduce.await();
                }
            }
            else{
                while(counter < 2)
                    canProduce.await();
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally{
            lock.unlock();
        }
    }
    private void ConstructorProduced(){
        lock.lock();
        try{
            if(ingotType ==1) counter--;
            else counter -= 2;
            isFull.signalAll();
        }
        finally{
            lock.unlock();
        }
    }
    public static Constructor getInstance(int ID){
        return instances.get(ID-1);
    }
    public static void addInstance(Constructor constructor){
        instances.add(constructor);

    }
    public Lock getLock(){
        return lock;

    }
    public Condition getisFull(){
        return isFull;
    }
    public Condition getCanProduce(){
        return canProduce;
    }
    public int getCounter(){
        return counter;
    }
    public void incrementCounter(int amount){
        counter += amount;
    }
    public int getCapacity(){
        return capacity;
    }
    private boolean activeTransporters(){
        ArrayList<Transporter> list = Transporter.getList();
        for(Transporter t : list){
            if(t.getTargetConstructorID() == ID){
                if(!t.isDead){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkRawCount(){
        if(ingotType == 1){
            if(counter ==0) return false;
            else return true;
        }
        else{
            if(counter <2) return false;
            else return true;
        }
    }


}
