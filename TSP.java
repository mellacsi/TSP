import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TSP {
    private int dimension;
    private int distances[][];
    private List<Node> coordinates = null;
    private int[] tour;
    private int bestKnow;
    private long seed;
    private int ants;
    private NN nn;
    private Ant ant;
    private double threshold;
    private String type;
    private String problemName;
    private int bestCost;

    public TSP(String problemName, int dimension, List<Node> coordinates, int bestKnow, String type, long seed, int ants, double threshold, int receivedCost) {
        this.dimension = dimension;
        this.distances = new int[this.dimension][this.dimension];
        this.coordinates = new ArrayList<>(coordinates);
        this.bestKnow = bestKnow;
        this.seed = seed;
        this.ants = ants;
        this.threshold = threshold;
        this.type = type;
        this.problemName = problemName;
        this.bestCost = receivedCost;
    }

    public void calculateDistances(){
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                //simmetrix matrix
                if(j > i) {
                    double distance, xDistance, yDistance;
                    xDistance = Math.pow(coordinates.get(i).getX() - coordinates.get(j).getX(), 2);
                    yDistance = Math.pow(coordinates.get(i).getY() - coordinates.get(j).getY(), 2);
                    distance = Math.sqrt(xDistance + yDistance);
                    distances[i][j] = (int) (distance + 0.5);
                    distances[j][i] = distances[i][j];
                }
            }
        }
    }

    public int calculateCost(int[] tour){
        int cost = 0;
        int currentNode = -1, nextNode = -1;
        int tourSize = tour.length;
        for (int i = 0; i < tour.length; i++) {
            currentNode = tour[i];
            nextNode = tour[(i + 1) % tourSize];
            cost += distances[currentNode - 1][nextNode - 1];
        }

        return cost;
    }

    public int[] calculateNN(int seed) {
        nn = new NN(distances, seed);
        return nn.calculate();
    }

    private int[] calculateAnt(int[] initialTour, int currentCost) {
        ant = new Ant(distances, initialTour, ants, bestKnow, currentCost, seed, threshold);
        return ant.calculate();
    }


    public void start(int nnSeed){
        calculateDistances();
        //tsp.printDistances();
        tour = calculateNN(nnSeed);
        int currentCost = calculateCost(tour);
        System.out.println("% dopo nn:" + ((currentCost+ (0.1)) - (bestKnow+(.0)))/(bestKnow+0.)*100.);
        System.out.println("Costo nn:" + currentCost);
        System.out.println("seed: " + seed);
        //System.out.println(tour);
        tour = calculateAnt(tour, currentCost);
        currentCost = calculateCost(tour);

        if(currentCost < bestCost) {
            bestCost = currentCost;
            System.out.println("Costo: " + bestCost);
            System.out.println("Nuova soluzione:");
            for (int i = 0; i < tour.length; i++) {
                System.out.print(tour[i] + " ");
            }
            System.out.println();
            //saveSolution();
        }
        System.out.println("% Ant:" + (calculateCost(tour)+(0.) - bestKnow+.0)/(bestKnow+0.));
        //System.exit(0);
    }

    public void saveSolution() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./src/ALGO_cup_2018_solutions/" + problemName + ".opt.tour", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        writer.println("NAME : " + problemName);
        writer.println("COMMENT : " + seed);
        writer.println("TYPE : " + type);
        writer.println("DIMENSION : " + bestKnow);
        writer.println("TOUR_SECTION");
        for (int i = 0; i < tour.length; i++) {
            writer.println(tour[i]);
        }
        writer.println("-1");
        writer.println("EOF");
        writer.close();
        System.out.println();
        System.out.println("File scritto");
    }

    public void printDistances(){
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                System.out.print( distances[i][j] +" ");
            }
            System.out.println();
        }
    }

    public void setTime(boolean time) {
        this.ant.setTime(time);
    }

    public int getBestCost(){
        return bestCost;
    }
}
