import java.util.ArrayList;
import java.util.List;

public class NN {
    private int[][] distances;
    private int seed;
    private int[] tour;

    public NN(int[][] distances, int seed) {
        this.distances = distances;
        this.seed = seed;
        this.tour = new int[distances[0].length];
    }

    public int[] calculate(){
        tour[0] = seed;

        //avaiable Node id list
        List<Integer> available = new ArrayList<>(distances[0].length);
        for (int i = 0; i < distances[0].length; i++) {
            available.add(i+1);
        }
        available.remove(seed-1);

        int currentId = seed;
        int receivedValue = -1;
        int availableSize = available.size();
        int i = 1;
        while (availableSize > 0) {
            receivedValue = searchMin(available, currentId);
            if(receivedValue != -1){
                tour[i] = receivedValue;
                available = removeFromId(available, receivedValue);
                availableSize = available.size();
                currentId = receivedValue;
                i++;
            }else//tour not found
                return null;
        }
        return tour;
    }

    private List<Integer> removeFromId(List<Integer> available, int idToDelete) {
        for (int i = 0; i < available.size(); i++) {
            if(available.get(i) == idToDelete){
                available.remove(i);
                return available;
            }
        }
        return null;
    }

    //given an id node it return the id of the next nearest Node
    private int searchMin(List<Integer> available, int currentNode) {
        int minValue = Integer.MAX_VALUE;
        int minId = -1;
        int rowCurrent = currentNode -1;

        for (int i = 0; i < distances[0].length; i++) {
            //don't use the current node
            if(i != rowCurrent && available.contains(i+1)) {
                if (distances[rowCurrent][i] < minValue) {
                    minId = i+1;
                    minValue = distances[rowCurrent][i];
                }
            }
        }

        return minId;
    }
}
