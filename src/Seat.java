import java.io.Serializable;

/**
 * @author Stefano
 */
public record Seat(int row, int col) implements Serializable, Comparable<Seat> {

    public static enum State{
        NOT_A_SEAT, OCCUPIED, UNOCCUPIED
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
