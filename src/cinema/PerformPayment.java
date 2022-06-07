package cinema;

import cinema.menu_building.AbstractSelector;
import cinema.menu_building.MessageModeSelector;
import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();
    private Theater selectedTheater;
    private ArrayList<Seat> purchasedSeats;
    private int totalPrice;
    private final MultiplexState state;
    private ResourceBundle language;

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi, MultiplexState state) {
        super(dispenser, multi);
        this.state = state;
    }

    public void setPurchase(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        this.selectedTheater = selectedTheater; this.purchasedSeats = purchasedSeats;
        this.totalPrice = computePrice(selectedTheater, purchasedSeats);
    }
    @Override
    public boolean doOperation() {
        language = getMultiplex().getLanguage();

        MessageModeSelector.Builder builder = new MessageModeSelector.Builder(getDispenser(), getMultiplex());

        builder.setTitle(this.toString());
        builder.setDescription(language.getString("insertCreditCard"));
        builder.setCancelButton();
        builder.setAcceptViaCreditCard();

        MessageModeSelector selector = builder.build();

        selector.display();

        if ((Boolean) selector.getPick()) {
            getDispenser().retainCreditCard(false);
            if (bank.comunicationAvaiable()) {
                try {
                    if (state.cardHasDiscount(getDispenser().getCardNumber())) {
                        getDispenser().setDescription(language.getString("discount"));
                        getDispenser().waitEvent(2);
                        totalPrice = (totalPrice * 7) / 10;
                        getDispenser().setTitle(this.toString());
                    }

                    if (bank.doOperation(getDispenser().getCardNumber(), totalPrice)) {
                        getDispenser().setOption(1, "pagar");

                        ArrayList<String> ticket = new ArrayList<>();

                        Object[] messageArguments ={
                                purchasedSeats.size(),
                                selectedTheater.getMovie()
                        };
                        MessageFormat formatter = new MessageFormat("ticket");
                        formatter.setLocale(language.getLocale());
                        String header = formatter.format(messageArguments);
                        ticket.add(header);

                        ticket.add(String.valueOf(totalPrice));

                        getDispenser().print(ticket);//TODO HAY QUE IMPRIMIR n TICKETS
                        getMultiplex().getCreditCardManager().returnCreditCard();
                        getDispenser().setDescription(language.getString("thanks"));

                        return true;
                    }
                    else {
                        //decir algo
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

        int nSeats = purchasedSeats.size();

        Object[] messageArguments ={
                nSeats,
                selectedTheater.getMovie()
        };
        MessageFormat formatter = new MessageFormat(language.getString("purchase"));
        formatter.setLocale(language.getLocale());

        return formatter.format(messageArguments);
    }
}
