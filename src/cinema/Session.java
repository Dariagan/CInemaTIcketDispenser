package cinema;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;
import static java.util.Objects.isNull;

public final class Session implements Serializable, Comparable<Session>{

    private final LocalTime time;
    private final TreeSet<Seat> occupiedSeatSet = new TreeSet<>();

    public Session(LocalTime time) {this.time = time;}

    private LocalTime getTime() {return time;}

    public String toString() {
        return time.toString();
    }
    
    public boolean isOccupied(Seat seat){
        return occupiedSeatSet.contains(seat);
    }
        
    public void occupySeat(Seat seat){
        if (!isNull(seat))
            occupiedSeatSet.add(seat);
        else throw new RuntimeException("Attempt to occupy a null seat in a session");
    }
    
    public void unoccupySeat(Seat seat){
        occupiedSeatSet.remove(seat);
    }

    @Override
    public int compareTo(Session o) {
        if (this.getTime().isAfter(o.getTime())){
            return 1;
        }else if (this.getTime().equals(o.getTime())){
            return 0;
        } else return -1;
    }
}
