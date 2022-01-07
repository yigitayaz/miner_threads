import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Constructor implements Runnable{

    private static final ArrayList<Constructor> instances = new ArrayList<Constructor>();

    private final HW2Logger logger;
    private final int ID;
    private final int interval;
    private final int capacity;
    private final int ingotType;
    private int counter;

    private final Lock lock;
    private final Condition didNotArrive;

    public Constructor(int ID, int interval, int capacity, int ingotType, HW2Logger logger) {
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.ingotType = ingotType;
        this.logger = logger;
        lock = new ReentrantLock();
        didNotArrive = lock.newCondition();
        counter =0;

    }

    @Override
    public void run() {
        logger.Log(0,0,0,ID,Action.CONSTRUCTOR_CREATED);
        while(/*there are actuve trabsportes abd ores in the incomung storage*/){
            WaitCanProduce();
            logger.Log(0,0,0,ID,Action.CONSTRUCTOR_STARTED);
            sleep();
            logger.Log(0,0,0,ID,Action.CONSTRUCTOR_FINISHED);
            ConstructorProduced();
        }
        logger.Log(0,0,0,ID,Action.CONSTRUCTOR_STOPPED);
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
            if(ingotType ==1){
                while(counter < 1){
                    didNotArrive.await();
                }
            }
            else{
                while(counter < 2)
                    didNotArrive.await();
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

    }
    public static Constructor getInstance(int ID){
        return instances.get(ID-1);
    }
    public static void addInstance(Constructor constructor){
        instances.add(constructor);

    }

}
