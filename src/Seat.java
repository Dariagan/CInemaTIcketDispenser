import java.io.Serializable;

/**
 * @author Stefano
 */
public record Seat(int row, int col) implements Serializable, Comparable<Seat> {

    public enum State{
        NOT_A_SEAT, OCCUPIED, UNOCCUPIED, SELECTED
    }

    @Override
    public int compareTo(Seat o) {

        int rowComparison = Integer.compare(this.row, o.row);

        if (rowComparison == 0)
            return Integer.compare(this.col, o.col);
        else
            return rowComparison;
    }
}
