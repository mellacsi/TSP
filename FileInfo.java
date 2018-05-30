import java.util.ArrayList;

public class FileInfo {
    private String name;
    private String comment;
    private String type;
    private int dimension;
    private int bestKnown;
    ArrayList<Node> coordinates = null;

    public void initializeMemory() {
        coordinates = new ArrayList<>(dimension);
    }

    public void save(Node node) {
        coordinates.add(node);
    }

    public void printCoordinates() {
        for (Node current: coordinates) {
            System.out.println("id:" + current.getId() + " x: " + current.getX() + " y: " + current.getY());
        }
    }

    public ArrayList<Node> getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public int getBestKnown() {
        return bestKnown;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public int getDimension() {
        return dimension;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setBestKnown(int bestKnown) {
        this.bestKnown = bestKnown;
    }
}
