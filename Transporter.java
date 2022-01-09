import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.DoubleStream;

public class Transporter extends Agent{
    private static final ArrayList<Transporter> instances = new ArrayList<>();

    private final int ID;
    private final int minerID;
    private final int sourceSmelterID;
    private final int targetSmelterID;
    private final int targetConstructorID;
    private boolean nothingToCarry ;


    public Transporter(int ID, int travelTime, int minerID, int sourceSmelterID, int targetSmelterID, int targetConstructorID,HW2Logger logger) {
        this.ID = ID;
        this.interval = travelTime;
        this.minerID = minerID;
        this.sourceSmelterID = sourceSmelterID;
        this.targetSmelterID = targetSmelterID;
        this.targetConstructorID = targetConstructorID;
        this.logger = logger;
        isDead = false;
        nothingToCarry = false;
    }

    @Override
    public void run() {
        logger.Log(0,0,ID,0,Action.TRANSPORTER_CREATED);
        Agent source;
        if(minerID != 0){
            logger.Log(minerID,0,ID,0,Action.TRANSPORTER_GO);
             source = Miner.getInstance(minerID);
        }

        else {
            logger.Log(0, sourceSmelterID, ID, 0, Action.TRANSPORTER_GO);
             source  = Smelter.getInstance(sourceSmelterID);
        }
        while(source.counter > 0  || !source.isDead){
            sleep();
            if(minerID != 0)
                logger.Log(minerID,0,ID,0,Action.TRANSPORTER_ARRIVE);
            else
                logger.Log(0,sourceSmelterID,ID,0,Action.TRANSPORTER_ARRIVE);
            WaitNextLoad();
            if(nothingToCarry) break;
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
        isDead = true;
        logger.Log(0,0,ID,0,Action.TRANSPORTER_STOPPED);
    }


    private void WaitNextLoad(){

        if(minerID != 0){
            Miner miner = Miner.getInstance(minerID);
            Lock lock = miner.getLock();
            Condition isEmpty = miner.getIsEmpty();
            lock.lock();
            try{


                while(miner.getCounter()  == 0 ){
                    if(miner.isDead){
                        nothingToCarry = true;
                        break;
                    }
                    isEmpty.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                if(miner.getCounter() !=0)
                miner.incrementCounter(-1);
                lock.unlock();
            }
        }
        else{
            Smelter smelter = Smelter.getInstance(sourceSmelterID);
            Lock lock = smelter.getLock();
            Condition isEmpty = smelter.getIsEmpty();
            lock.lock();
            try{


                while(smelter.getIngotCounter()== 0){
                    if(smelter.isDead){
                        nothingToCarry = true;
                        break;
                    }
                    isEmpty.await();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();

            }
            finally{
                if(smelter.getIngotCounter() !=0)
                smelter.incrementIngotCounter(-1);
                lock.unlock();
            }

        }
    }
    private void WaitUnLoad(){
        if(targetConstructorID != 0){
            Constructor constructor = Constructor.getInstance(targetConstructorID);
            Lock lock = constructor.getLock();
            Condition isFull = constructor.getisFull();
            lock.lock();
            try{


                int capacity = constructor.getCapacity();
                while(constructor.getCounter() >= capacity){
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
            lock.lock();
            try{


                int capacity = smelter.getOreCapacity();
                while(smelter.getOreCounter()>= capacity ){
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
            miner.getLock().lock();
            miner.getIsFull().signal();
            miner.getLock().unlock();
        }
        else{
            // Buraya bakicam
            Smelter smelter = Smelter.getInstance(sourceSmelterID);
            smelter.getLock().lock();
            smelter.getCanProduce().signal();
            smelter.getLock().unlock();
        }
    }
    private void Unloaded(){
        if(targetSmelterID !=0){
            Smelter smelter = Smelter.getInstance(targetSmelterID);
            smelter.getLock().lock();
            smelter.getCanProduce().signal();
            smelter.getLock().unlock();
        }
        else{
            Constructor constructor = Constructor.getInstance(targetConstructorID);
            constructor.getLock().lock();
            constructor.getCanProduce().signal();
            constructor.getLock().unlock();
        }
    }

    public static ArrayList<Transporter> getList(){
        return instances;
    }
    public static void addInstance(Transporter transporter) {
        instances.add(transporter);
    }

    public int getTargetSmelterID() {
        return targetSmelterID;
    }

    public int getTargetConstructorID() {
        return targetConstructorID;
    }
}



