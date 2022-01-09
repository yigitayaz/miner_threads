import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator {
    private static ArrayList<Miner> miners = new ArrayList<>();
    private static ArrayList<Smelter> smelters = new ArrayList<>();
    private static ArrayList<Constructor> constructors = new ArrayList<>();
    private static ArrayList<Transporter> transporters = new ArrayList<>();

    public static void main(String[] args) {

        HW2Logger logger = HW2Logger.getInstance();

        Scanner sc = new Scanner(System.in);
        int[] inputArray = new int[4];
        int agentCount,totalAgentCount = 0;
        agentCount = sc.nextInt();
        totalAgentCount += agentCount;

        for(int i = 0; i < agentCount; i++){
            for(int j =0 ; j< 4; j++){
                inputArray[j] = sc.nextInt();
            }
            Miner miner = new Miner(i+1,inputArray[0],inputArray[1],inputArray[2],inputArray[3],logger);
            miners.add(miner);
            Miner.addInstance(miner);
        }

        agentCount = sc.nextInt();
        totalAgentCount += agentCount;

        for(int i = 0; i < agentCount; i++){
            for(int j =0 ; j< 4; j++){
                inputArray[j] = sc.nextInt();
            }
            Smelter smelter = new Smelter(i+1,inputArray[0],inputArray[1],inputArray[2],inputArray[3],logger);
            smelters.add(smelter);
            Smelter.addInstance(smelter);
        }

        agentCount = sc.nextInt();
        totalAgentCount += agentCount;
        inputArray = new int[3];
        for(int i = 0; i < agentCount; i++){
            for(int j =0 ; j< 3; j++){
                inputArray[j] = sc.nextInt();
            }
            Constructor constructor = new Constructor(i+1,inputArray[0],inputArray[1],inputArray[2],logger);
            constructors.add(constructor);
            Constructor.addInstance(constructor);
        }

        agentCount = sc.nextInt();
        totalAgentCount += agentCount;
        inputArray = new int[5];

        for(int i = 0; i < agentCount; i++){
            for(int j =0 ; j< 5; j++){
                inputArray[j] = sc.nextInt();
            }
            Transporter transporter = new Transporter(i+1,inputArray[0],inputArray[1],inputArray[2],inputArray[3],inputArray[4],logger);
            transporters.add(transporter);
            Transporter.addInstance(transporter);
        }

        logger.Initialize();

        ExecutorService executor = Executors.newFixedThreadPool(totalAgentCount);

        for(Miner m: miners){
            executor.execute(m);
        }
        for(Smelter s: smelters){
            executor.execute(s);
        }
        for(Constructor c: constructors){
            executor.execute(c);
        }
        for(Transporter t: transporters){
            executor.execute(t);
        }
        executor.shutdown();
//        PrintStream out = null;
//       try {
//           out = new PrintStream(new FileOutputStream("myout6.txt"));
//       } catch (FileNotFoundException e) {
//           e.printStackTrace();
//       }
//        System.setOut(out);

    }





}
