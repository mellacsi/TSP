public class TwoOpt {
    private int[][] distances;
    private int[] tour;
    private int size;

    public TwoOpt(int[][] distances, int[] initialTour) {
        this.distances = distances;
        this.tour = initialTour;
    }

    public void setTour(int[] tour) {
        this.tour = tour;
    }

    public int[] calculate() {
        int bestGain = Integer.MAX_VALUE, currentGain = 0, bestI = 0, bestJ = 0;
        size = tour.length;

        while (bestGain != 0) {
            bestGain = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if(i == j)
                        continue;
                    currentGain = computeGain(i, j);
                    if (currentGain < bestGain) {
                        bestGain = currentGain;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestGain < 0)
                exchange(bestI, bestJ);
        }
        return tour;
    }

    private void exchange(int bestI, int bestJ) {
        int[] newTour = new int[size];
        for (int i = 0; i < bestI + 1; i++) {
            newTour[i] = tour[i];
        }

        for (int i = bestJ+1; i < size; i++) {
            newTour[i] = tour[i];
        }

        newTour[(bestI + 1) % size] = bestJ;
        int j = bestJ;
        for (int i = bestI+1; i < bestJ+1; i++) {
            newTour[i % size] = tour[j % size];
            j--;
        }
        tour = newTour;
    }

    private int computeGain(int i, int j) {
        int removedArch = - distances[tour[i % size] -1][tour[(i + 1) % size] -1] - distances[tour[j % size] -1][tour[(j + 1) % size] -1];
        int addedArch = distances[tour[i % size] -1][tour[j % size] -1] + distances[tour[(i + 1) % size] -1][tour[(j + 1) % size] -1];

        return removedArch + addedArch;
    }
}
