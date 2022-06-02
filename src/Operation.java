import sienens.CinemaTicketDispenser;

public abstract class Operation {
    
    private final Multiplex multi;
    private final CinemaTicketDispenser dispenser;

    public Operation(CinemaTicketDispenser dispenser, Multiplex multi) {
        this.dispenser = dispenser;
        this.multi = multi;
    }

    public abstract boolean doOperation();
    
    public abstract String getTitle();
    
    public Multiplex getMultiplex(){return multi;}
    
    public CinemaTicketDispenser getDispenser(){return dispenser;}
}
