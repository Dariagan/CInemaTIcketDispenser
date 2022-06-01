import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();

    @Override
    public void doOperation() {

    }

    @Override
    public String getTitle() {
        return null;
    }

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
        UrjcBankServer bank = new UrjcBankServer();
    }
}
