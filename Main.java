import java.io.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static FileInfo fileInfo = new FileInfo();
    private static TSP tsp;
    private static Timer timer = new Timer();
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Problema: " + args[0]);
        System.out.println("Ants: " + args[2] + " thresh: " + args[3]);

        boolean first = true;
        while(true) {
            startTimer(args);
            readFile(args[0]);
            if(first) {
                tsp = new TSP(fileInfo.getName(), fileInfo.getDimension(), fileInfo.getCoordinates(), fileInfo.getBestKnown(), fileInfo.getType(), random.nextInt(), Integer.parseInt(args[2]), Double.parseDouble(args[3]), Integer.MAX_VALUE);
                first = false;
            }else
                tsp = new TSP(fileInfo.getName(), fileInfo.getDimension(), fileInfo.getCoordinates(), fileInfo.getBestKnown(), fileInfo.getType(), random.nextInt(), Integer.parseInt(args[2]), Double.parseDouble(args[3]), tsp.getBestCost());

            tsp.start(1);


        }
    }

    private static void readFile(String file){
        FileInputStream fis = null;
        //TODO .src da togliere per JAR
        String path = "./src/ALGO_cup_2018_problems/";
        try {
            fis = new FileInputStream(path + file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            boolean readNumbers = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                String currentLineElements[] = line.split(" ");
                if(currentLineElements[0].contains("NAME")){
                    fileInfo.setName(currentLineElements[currentLineElements.length-1]);
                } else if(currentLineElements[0].contains("COMMENT")) {
                    String comment = "";
                    for (int i = 1; i < currentLineElements.length; i++) {
                        comment += currentLineElements[i] + " ";
                    }
                    fileInfo.setComment(comment);
                }else if(currentLineElements[0].contains("TYPE")) {
                    fileInfo.setType(currentLineElements[currentLineElements.length - 1]);
                }else if(currentLineElements[0].contains("DIMENSION")){
                    fileInfo.setDimension(Integer.parseInt(currentLineElements[currentLineElements.length-1]));
                    fileInfo.initializeMemory();
                }else if(currentLineElements[0].contains("BEST_KNOWN")) {
                            fileInfo.setBestKnown(Integer.parseInt(currentLineElements[currentLineElements.length - 1]));
                }else if(currentLineElements[0].contains("NODE_COORD_SECTION")){
                        readNumbers = true;
                }else if(currentLineElements[0].contains("EOF")){
                        readNumbers = false;
                        System.out.println("File letto correttamente");
                }

                if(readNumbers && !(currentLineElements[0].equals("NODE_COORD_SECTION")))
                    fileInfo.save(new Node(Integer.parseInt(currentLineElements[0]), Double.parseDouble(currentLineElements[1]), Double.parseDouble(currentLineElements[2])));
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startTimer(String[] args) {
        //Set up timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                tsp.setTime(false);
                System.out.println("Tempo esaurito");
            }
        };
        //3*60*1000 = 180000
        timer.schedule(tasknew, 179000);
    }
}
