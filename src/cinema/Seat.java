package cinema;

import java.io.Serializable;


public record Seat(int row, int col) implements Serializable, Comparable<Seat> {

    public enum State{
        NOT_A_SEAT, OCCUPIED, UNOCCUPIED, SELECTED
    }

    @Override
    public int compareTo(Seat o) {

        final int ROW_COMPARISON = Integer.compare(this.row, o.row);

        if (ROW_COMPARISON == 0)
            return Integer.compare(this.col, o.col);
        else
            return ROW_COMPARISON;
    }
}
