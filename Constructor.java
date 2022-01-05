import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.DoubleStream;

public class Constructor implements Runnable{
    private final HW2Logger logger;
    private final int ID;
    private final int interval;
    private final int capacity;
    private final int ingotType;
    private int counter;

    private final Lock lock;
    private final Condition isArrived;

    public Constructor(int ID, int interval, int capacity, int ingotType, HW2Logger logger) {
        this.ID = ID;
        this.interval = interval;
        this.capacity = capacity;
        this.ingotType = ingotType;
        this.logger = logger;
        lock = new ReentrantLock();
        isArrived = lock.newCondition();
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

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally
    }
    private void ConstructorProduced(){

    }
}
