import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import java.util.ArrayList;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();
    private Theater selectedTheater;
    private ArrayList<Seat> purchasedSeats;
    private int totalPrice;
    private final MultiplexState state;

    public void setPurchase(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        this.selectedTheater = selectedTheater; this.purchasedSeats = purchasedSeats;
        this.totalPrice = computePrice(selectedTheater, purchasedSeats);
    }
    @Override
    public boolean doOperation() {
        getDispenser().setMessageMode();
        getDispenser().setTitle(this.toString());

        getDispenser().setDescription("introduzca su tarjeta de crédito");
        getDispenser().setOption(0, "cancelar");
        getDispenser().setOption(1, null);

        if (getDispenser().waitEvent(30) == '1') {
            getDispenser().retainCreditCard(false);
            if (bank.comunicationAvaiable()) {
                try {
                    if (state.getCreditCardManager().cardHasDiscount(getDispenser().getCardNumber())) {
                        getDispenser().setDescription("tiene un descuento del 30% por ser un socio");
                        getDispenser().waitEvent(2);
                        totalPrice = (totalPrice * 7) / 10;
                        getDispenser().setTitle(this.toString());
                    }

                    if (bank.doOperation(getDispenser().getCardNumber(), totalPrice)) {
                        getDispenser().setOption(1, "pagar");

                        ArrayList<String> ticket = new ArrayList<>();

                        String movie = String.format("todo");
                        ticket.add(selectedTheater.getMovie().getTITLE());//TODO

                        //todo agregar el resto

                        getDispenser().print(ticket);//TODO
                        CreditCardManager.returnCreditCard(getDispenser());
                        getDispenser().setDescription("gracias por su compra");

                        return true;
                    }
                    else {
                        //nase
                        return false;
                    }
                } catch (Exception ignored) {}
            }
            printUnavailabilityMessage();
            //preguntar si sucede esto se resetea el ticket-making o se espera hasta q este available y se continúa
        }
        return false;
    }


    private void printUnavailabilityMessage(){
        getDispenser().setDescription("en estos momentos no podemos atenderte");//TODO translate
    }

    private int computePrice(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        return selectedTheater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String toString() {
        return String.format("%d entradas para %s: %d €",//TODO falta el translate
                purchasedSeats.size(), selectedTheater.getMovie().getTITLE(), totalPrice);
    }

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi, MultiplexState state) {
        super(dispenser, multi);
        this.state = state;
    }
}
