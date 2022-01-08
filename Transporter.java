import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.DoubleStream;

public class Transporter implements Runnable{
    private final int ID;
    private final int travelTime;
    private final int minerID;
    private final int sourceSmelterID;
    private final int targetSmelterID;
    private final int targetConstructorID;
    private final HW2Logger logger;

    public Transporter(int ID, int travelTime, int minerID, int sourceSmelterID, int targetSmelterID, int targetConstructorID,HW2Logger logger) {
        this.ID = ID;
        this.travelTime = travelTime;
        this.minerID = minerID;
        this.sourceSmelterID = sourceSmelterID;
        this.targetSmelterID = targetSmelterID;
        this.targetConstructorID = targetConstructorID;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.Log(0,0,ID,0,Action.TRANSPORTER_CREATED);
        if(minerID != 0) logger.Log(minerID,0,ID,0,Action.TRANSPORTER_GO);
        else logger.Log(0,sourceSmelterID,ID,0,Action.TRANSPORTER_GO);
        
        while(/*The source has ingots or is active*/){
            sleep();
            if(minerID != 0)
                logger.Log(minerID,0,ID,0,Action.TRANSPORTER_ARRIVE);
            else
                logger.Log(0,sourceSmelterID,ID,0,Action.TRANSPORTER_ARRIVE);
            WaitNextLoad();
            if(minerID != 0)
                logger.Log(minerID,0,ID,0,Action.TRANSPORTER_TAKE);
            else
                logger.Log(0,sourceSmelterID,ID,0,Action.TRANSPORTER_TAKE);
            Loaded(/*Source*/);

            if(minerID !=0){
                if(targetSmelterID !=0)
                    logger.Log(minerID,targetSmelterID,ID,0,Action.TRANSPORTER_GO);

                else
                    logger.Log(minerID,0,ID,targetConstructorID,Action.TRANSPORTER_GO);

            }
            else
                logger.Log(0,sourceSmelterID,ID,targetConstructorID,Action.TRANSPORTER_GO);

            sleep();
            if(targetSmelterID != 0)
                logger.Log(0,targetSmelterID,ID,0,Action.TRANSPORTER_ARRIVE);

            else
                logger.Log(0,0,ID,targetConstructorID,Action.TRANSPORTER_ARRIVE);
            WaitUnLoad();

            if(targetSmelterID != 0)
                logger.Log(0,targetSmelterID,ID,0,Action.TRANSPORTER_DROP);

            else
                logger.Log(0,0,ID,targetConstructorID,Action.TRANSPORTER_DROP);
            Unloaded(/*Target*/);

            if(minerID !=0){
                if(targetSmelterID !=0)
                    logger.Log(minerID,targetSmelterID,ID,0,Action.TRANSPORTER_GO);

                else
                    logger.Log(minerID,0,ID,targetConstructorID,Action.TRANSPORTER_GO);

            }
            else
                logger.Log(0,sourceSmelterID,ID,targetConstructorID,Action.TRANSPORTER_GO);

        }
        logger.Log(0,0,ID,0,Action.TRANSPORTER_STOPPED);
    }

    private void sleep(){
        Random random = new Random(System.currentTimeMillis());
        DoubleStream stream;
        stream = random.doubles(1, travelTime - travelTime * 0.01, travelTime + travelTime * 0.01);
        try {
            Thread.sleep((long) stream.findFirst().getAsDouble());
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    private void WaitNextLoad(){
        if(minerID != 0){
            Miner miner = Miner.getInstance(minerID);
            Lock lock = miner.getLock();
            Condition isEmpty = miner.getIsEmpty();

            try{
                lock.lock();
                int counter = miner.getCounter();
                while(counter  == 0){
                    isEmpty.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                miner.incrementCounter(-1);
                lock.unlock();
            }
        }
        else{
            Smelter smelter = Smelter.getInstance(sourceSmelterID);
            Lock lock = smelter.getLock();
            Condition isEmpty = smelter.getIsEmpty();
            try{
                lock.lock();
                int counter = smelter.getIngotCounter();
                while(counter == 0){
                    isEmpty.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                smelter.incrementIngotCounter(-1);
                lock.unlock();
            }

        }
    }
    private void WaitUnLoad(){
        if(targetConstructorID != 0){
            Constructor constructor = Constructor.getInstance(minerID);
            Lock lock = constructor.getLock();
            Condition isFull = constructor.getisFull();

            try{
                lock.lock();
                int counter = constructor.getCounter();
                int capacity = constructor.getCapacity();
                while(counter >= capacity){
                    isFull.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                constructor.incrementCounter(1);
                lock.unlock();
            }
        }
        else{
            Smelter smelter = Smelter.getInstance(targetSmelterID);
            Lock lock = smelter.getLock();
            Condition isFull = smelter.getIsFull();
            try{
                lock.lock();
                int counter = smelter.getIngotCounter();
                int capacity = smelter.getOreCapacity();
                while(counter>= capacity ){
                    isFull.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                smelter.incrementOreCounter(1);
                lock.unlock();
            }

        }
    }
    private void Loaded(){
        if(minerID !=0){
            Miner miner = Miner.getInstance(minerID);
            miner.getIsFull().signal();
        }
        else{
            // Buraya bakicam
            Smelter smelter = Smelter.getInstance(sourceSmelterID);
            smelter.getIsFull().signal();
        }
    }
    private void Unloaded(){
        if(targetSmelterID !=0){
            Smelter smelter = Smelter.getInstance(targetSmelterID);
            smelter.getIsFull().signal();
        }
        else{
            Constructor constructor = Constructor.getInstance(targetConstructorID);
            constructor.getisFull().signal();
        }
    }
}
