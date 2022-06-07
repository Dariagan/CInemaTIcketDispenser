package cinema;

import sienens.CinemaTicketDispenser;

public abstract class Operation {

    private final CinemaTicketDispenser dispenser;
    private final Multiplex multi;

    public Operation(CinemaTicketDispenser dispenser, Multiplex multi) {
        this.dispenser = dispenser;
        this.multi = multi;
    }

    public abstract boolean doOperation();
    
    public abstract String toString();
    
    public Multiplex getMultiplex(){return multi;}
    
    public CinemaTicketDispenser getDispenser(){return dispenser;}
}
