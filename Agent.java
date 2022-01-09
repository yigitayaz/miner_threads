import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.stream.DoubleStream;

public abstract class Agent implements Runnable {
    protected int interval;
    protected HW2Logger logger;
    protected Lock lock;
    protected int counter;
    protected boolean isDead;

    protected void sleep(){
        Random random = new Random(System.currentTimeMillis());
        DoubleStream stream;
        stream = random.doubles(1, interval - interval * 0.01, interval + interval * 0.01);
        try {
            Thread.sleep((long) stream.findFirst().getAsDouble());
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
