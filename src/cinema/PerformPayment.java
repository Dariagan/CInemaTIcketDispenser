package cinema;

import cinema.menu_building.MessageModeSelector;
import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import javax.naming.CommunicationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.util.Objects.isNull;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();
    private Theater selectedTheater;
    private ArrayList<Seat> selectedSeats;
    private Session selectedSession;
    private int totalPrice;
    private final MultiplexState state;
    private ResourceBundle language;
    MessageModeSelector.Builder builder = new MessageModeSelector.Builder(getDispenser(), getMultiplex());

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi, MultiplexState state) {
        super(dispenser, multi);
        this.state = state;
    }

    public void setPurchase(Theater selectedTheater, Session selectedSession, ArrayList<Seat> purchasedSeats){
        this.selectedTheater = selectedTheater;
        this.selectedSession = selectedSession;
        this.selectedSeats = purchasedSeats;
        this.totalPrice = computePrice(selectedTheater, purchasedSeats);
    }

    @Override
    public boolean doOperation() {
        language = getMultiplex().getLanguage();

        MessageModeSelector selector = createPurchaseSummaryMenu();

        Object answer = selector.getPick();

        if (!isNull(answer) && (Boolean)answer) {
            getDispenser().retainCreditCard(false);
            if (bank.comunicationAvaiable()) {
                try {
                    return purchase();
                }
                catch (CommunicationException ignored) {}
            }
            getMultiplex().getCreditCardManager().returnCreditCard();
            displayUnavailabilityMessage();
        }
        return false;
    }

    private MessageModeSelector createPurchaseSummaryMenu(){
        builder.reset();
        builder.setTitle(this.toString());

        String formattedPricing = TicketFormatter.getFormattedPricing(totalPrice, language);
        String desc = String.format("%s\n\n%s", formattedPricing, language.getString("insertCreditCard"));

        builder.setDescription(desc);
        builder.setCancelButton();
        builder.setAcceptViaCreditCard();
        return builder.build();
    }

    private boolean purchase() throws CommunicationException {
        if (state.cardHasDiscount(getDispenser().getCardNumber()))
            displayDiscount();

        boolean cardHasEnoughBalance = bank.doOperation(getDispenser().getCardNumber(), totalPrice);

        if (cardHasEnoughBalance) {

            MessageModeSelector selector = createConfirmPurchaseMenu();

            Object answer = selector.getPick();

            if (!isNull(answer) && (Boolean)answer) {

                for (ArrayList<String> ticket : makeTicketPack(selectedSeats))
                    getDispenser().print(ticket);

                getMultiplex().getCreditCardManager().returnCreditCard();

                giveThanks();
                return true;
            }
            else return false;
        } else {
            getMultiplex().getCreditCardManager().returnCreditCard();
            displayInsufficientBalanceMessage();
            return false;
        }
    }

    private void displayDiscount(){

        totalPrice = (totalPrice * 7) / 10;
        builder.reset();
        builder.setTitle(this.toString());

        String formattedPricing = TicketFormatter.getFormattedPricing(totalPrice, language);

        String desc = String.format("%s\n\n%s", language.getString("discount"), formattedPricing);

        builder.setDescription(desc);

        MessageModeSelector selector = builder.build();

        selector.show(3);
    }

    private MessageModeSelector createConfirmPurchaseMenu(){
        builder.reset();
        builder.setTitle(this.toString());

        String desc = language.getString("confirm");

        builder.setDescription(desc);
        builder.setCancelButton();
        builder.setAcceptButton();
        return builder.build();
    }

    private void giveThanks(){
        builder.reset();

        builder.setDescription(language.getString("thanks"));

        MessageModeSelector selector = builder.build();

        selector.show(3);
    }

    private ArrayList<ArrayList<String>> makeTicketPack(ArrayList<Seat> seats){

        ArrayList<ArrayList<String>> ticketPack = new ArrayList<>();

        int i = 1;
        for (Seat seat:seats) {

            Movie movie = selectedTheater.getMovie();
            ArrayList<String> ticket = new ArrayList<>();

            String purchaseHeader = this.toString();
            String price = TicketFormatter.getFormattedPricing(totalPrice, language);
            String session = TicketFormatter.getFormattedSession(selectedSession, language);
            String duration =  TicketFormatter.getFormattedDuration(movie.getDuration(), language);
            String date = TicketFormatter.getFormattedDate(LocalDate.now(), language);
            String seating = TicketFormatter.getFormattedSeating(seat, language);
            String ticketNumber = TicketFormatter.getFormattedTicketNumber(i, seats.size(), language);

            ticket.add(purchaseHeader);
            ticket.add(price);
            ticket.add(date);
            ticket.add(session);
            ticket.add(duration);
            ticket.add(seating);
            ticket.add(ticketNumber);
            ticket.add(TicketFormatter.getRandomBarcode());

            ticketPack.add(ticket);
            i++;
        }
        return ticketPack;
    }

    private void displayUnavailabilityMessage(){
        builder.reset();
        builder.setTitle(language.getString("sorry"));
        builder.setDescription(language.getString("unavailable"));

        MessageModeSelector selector = builder.build();

        selector.show(3);
    }

    private void displayInsufficientBalanceMessage(){
        builder.reset();
        builder.setDescription(language.getString("balance"));

        MessageModeSelector selector = builder.build();

        selector.show(3);

        getMultiplex().getCreditCardManager().returnCreditCard();
    }

    private int computePrice(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        return selectedTheater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String toString() {
        return TicketFormatter.getFormattedPurchase(selectedTheater.getMovie(), selectedSeats, language);
    }
}
