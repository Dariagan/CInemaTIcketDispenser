import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

import static java.util.Objects.isNull;

public final class Session implements Serializable, Comparable<Session>{

    private final LocalTime TIME;
    private final TreeSet<Seat> occupiedSeatSet = new TreeSet<>();

    public Session(LocalTime time) {
        this.TIME = time;
    }

    private LocalTime getTIME() {return TIME;}

    public String toString() {
        return TIME.toString();
    }
    
    public boolean isOccupied(Seat seat){
        return occupiedSeatSet.contains(seat);
    }
        
    public void occupySeat(Seat seat){
        if (!isNull(seat))
            occupiedSeatSet.add(seat);
        else throw new RuntimeException("Passed null seat to session");
    }
    
    public void unoccupySeat(Seat seat){
        occupiedSeatSet.remove(seat);
    }

    @Override
    public int compareTo(Session o) {
        if (this.getTIME().isAfter(o.getTIME())){
            return 1;
        }else if (this.getTIME().equals(o.getTIME())){
            return 0;
        } else return -1;
    }
}
