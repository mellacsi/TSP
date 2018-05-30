import java.util.Random;

public class Ant {
    private double[][] pheromone;
    private int[][] distances;
    private int[] tour;
    private int tourLenght;
    private int ants;
    private int bestKnown;
    private double currentCost;
    private double nnCost;
    private Random random;
    private TwoOpt twoOpt;
    private long seed;
    private volatile boolean time = true;
    private double threshold;

    public Ant(int[][] distances, int[] initialTour, int ants, int bestKnown, int currentCost, long seed, double threshold) {
        this.tour = initialTour;
        this.distances = distances;
        this.ants = ants;
        this.bestKnown = bestKnown;
        this.tourLenght = tour.length;
        this.pheromone = new double[tourLenght][tourLenght];
        this.random = new Random();

        //this.seed = random.nextInt();
        this.seed = seed;
        this.random.setSeed(seed);
        this.currentCost = currentCost + 0.;
        this.nnCost = currentCost + 0.;
        this.twoOpt = new TwoOpt(distances, null);
        this.threshold = threshold;
    }

    public int[] calculate() {
        initializePheromone();
        //fino a che errore != 0 o fino a che ho tempo
        //double errorPerc = Double.MAX_VALUE;
        //while(errorPerc > 0.){

        while(time){
            //ants on random city
            int[][] antTours = new int[ants][tourLenght];
            for (int i = 0; i < ants; i++) {
                int[] antTour = new int[tourLenght];
                int randomNumber = random.nextInt(tourLenght);
                antTour[0] = tour[randomNumber];
                antTours[i] = (antTour);
            }

            int idBest = -1;
            for (int n = 0; n < (tourLenght - 1); n++) {
                for (int m = 0; m < ants; m++) {
                    int[] antTour = antTours[m];
                    //select next city
                    Integer cityId = findNextCity(antTour, n); //exploitation

                    //random <= threshold : exploitation
                    if(random.nextDouble() > threshold){
                        cityId = findNextCityProportional(antTour, cityId, n); //altrimenti exploration
                    }

                    antTour[n + 1] = cityId;

                    //local update
                    int last = antTour[1 + n];
                    int beforeLast = antTour[n];
                    updateLocalPheromon(last, beforeLast);
                    if(!time)
                        return tour;
                }
                if(!time)
                    return tour;
            }
            if(!time)
                return tour;
            //last local update
            int[] currentTour;
            for (int m = 0; m < ants; m++) {
                currentTour = antTours[m];
                updateLocalPheromon(currentTour[tourLenght-1], currentTour[0]);
            }

            if(!time)
                return tour;

            for (int i = 0; i < ants; i++) {
                twoOpt.setTour(antTours[i]);
                antTours[i] =  twoOpt.calculate();

                if(!time)
                    return tour;
            }
            if(!time)
                return tour;

            idBest = findCheaper(antTours);

            //updating with best ant
            double newCost = calculateCost(antTours[idBest]);
            if(newCost < currentCost){
                this.tour = antTours[idBest];
                this.currentCost = newCost;
                //errorPerc = ((currentCost+(0.)) - (bestKnown+.0))/(bestKnown+0.);
                System.out.println("costo: " + newCost);
                if(currentCost == bestKnown)
                    return tour;
            }

            //global update
            updateglobalPheromon(antTours[idBest]);
        }

        System.out.println("Formiche terminate");
        return tour;
    }



    private void updateglobalPheromon(int[] bestTour) {
        //update best tour
        int tourSize = tourLenght;
        double alpha = 0.1;
        double delta = 1/currentCost;

        int a = -1;
        int b = -1;
        for (int z = 0; z < tourSize; z++) {
            a = bestTour[z] - 1;
            b = bestTour[(z + 1) % tourSize] - 1;

            pheromone[a][b] = (1 - alpha) * pheromone[a][b] + alpha * delta;
            pheromone[b][a] = pheromone[a][b];
        }
    }

    private void updateLocalPheromon(int a, int b) {
        //update this tour
        double p = 0.1;

        pheromone[a-1][b-1] = (1 - p) * pheromone[a-1][b-1] + p /  ((nnCost + 0.) * (tourLenght + 0.));
        pheromone[b-1][a-1] = pheromone[a-1][b-1];
    }

    private Integer findNextCityProportional(int[] antTour, int bestCity, int lastIndex) {
        int antTourIndex = antTour[lastIndex] - 1;
        int tourSize = tourLenght;

        double sumCosts = 0;
        for (int j = 0; j < tourSize; j++) {
            if (j+1 == bestCity) continue;

            if (!contains(antTour, j + 1)) {
                //skip last city
                if(j != antTourIndex) {
                    sumCosts += pheromone[antTourIndex][j] / (distances[antTourIndex][j] + 0.);
                }
            }
        }

        int size = tourLenght - lastIndex - 1;
        double[] costs = new double[size];
        int[] ids = new int[size];

        int index = 0;
        for (int j = 0; j < tourSize; j++) {
            if (j+1 == bestCity) continue;

            if (!contains(antTour, j + 1)){
                if(j != antTourIndex) {
                    costs[index] = (pheromone[antTourIndex][j] / distances[antTourIndex][j]) / sumCosts;
                    ids[index] = j + 1;
                    index++;
                }
            }
        }
        //unique city is the best
        if(costs.length == 1 && costs[0] == 0)
            return bestCity;

        double randValue = random.nextDouble();
        int j = 0;
        double sum = costs[j];

        while(sum < randValue){
            j++;
            sum += costs[j];
        }
        return ids[j];
    }

    private Integer findNextCity(int[] antTour, int lastIndex) {
        //find max rapp
        //find next city with pheromone/distance
        int bestCityId = -5;
        double maxCost= -1;
        int antTourIndex = antTour[lastIndex] - 1;
        double currentCost = -2;
        for (int j = 0; j < tourLenght; j++) {
            if(!contains(antTour, j+1)){
               currentCost  = pheromone[antTourIndex][j] / distances[antTourIndex][j];
                if (currentCost > maxCost) {
                    maxCost = currentCost;
                    bestCityId = j + 1;
                }
            }
        }

        return bestCityId;
    }

    private boolean contains(int[] antTour, int i) {
        for (int j = 0; j < tourLenght; j++) {
            if(antTour[j] == i)
                return true;
        }
        return false;
    }

    public int calculateCost(int[] receivedTour){
        int cost = 0;
        int currentNode = -1, nextNode = -1;
        for (int i = 0; i < tourLenght; i++) {
            currentNode = receivedTour[i];
            nextNode = receivedTour[(i + 1) % tourLenght];
            cost += distances[currentNode - 1][nextNode - 1];
        }

        return cost;
    }

    public int findCheaper(int[][] tour){
        int tourId = -1;
        int bestCost = Integer.MAX_VALUE;
        int currentCost;
        for (int i = 0; i < ants; i++) {
            currentCost = calculateCost(tour[i]);
            if(currentCost < bestCost){
                tourId = i;
                bestCost = currentCost;
            }
        }

        return tourId;
    }

    private void initializePheromone() {
        int tourSize = tourLenght;
        for (int i = 0; i < tourSize; i++) {
            for (int j = 0; j < tourSize; j++) {
                //symmetrix matrix
                if(j > i) {
                    pheromone[i][j] = 1/(nnCost*tourSize);
                    pheromone[j][i] = pheromone[i][j];
                }
            }
        }
    }

    public int[] getTour() {
        return tour;
    }

    public boolean getTime() {
        return time;
    }

    public void setTime(boolean time) {
        this.time = time;
    }

    public long getSeed() {
        return seed;
    }

    public double getCurrentCost() {
        return currentCost;
    }
}
