import java.util.Comparator;

class ModifiedComparator implements Comparator<Node> {
    public int compare(Node x, Node y)
    {

        return x.frequency - y.frequency;
    }
}