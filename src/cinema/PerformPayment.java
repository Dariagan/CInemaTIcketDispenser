package cinema;

import cinema.menu_building.MessageModeSelector;
import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import javax.naming.CommunicationException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
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

        //todo mover a método
        builder.reset();
        builder.setTitle(this.toString());

        String desc = String.format("%s\n\n%s", getFormattedPricing(), language.getString("insertCreditCard"));

        builder.setDescription(desc);
        builder.setCancelButton();
        builder.setAcceptViaCreditCard();
        MessageModeSelector selector = builder.build();

        selector.display();
        //todo mover a método

        Object answer = selector.getPick();

        if (!isNull(answer) && (Boolean)answer) {
            getDispenser().retainCreditCard(false);
            if (bank.comunicationAvaiable()) {
                try {
                    boolean customerIsAssociate = state.cardHasDiscount(getDispenser().getCardNumber());

                    if (customerIsAssociate)
                        displayDiscount();

                    boolean cardHasEnoughBalance = bank.doOperation(getDispenser().getCardNumber(), totalPrice);

                    if (cardHasEnoughBalance) {//todo partir en varios métodos
                        getDispenser().setOption(1, "pagar");

                        ArrayList<ArrayList<String>> ticketPack;

                        ticketPack = makeTicketPack(selectedSeats);

                        for (ArrayList<String> ticket : ticketPack)
                            getDispenser().print(ticket);

                        //todo meter los respectivos nºs de asientos entre los n tickets

                        //TODO HAY QUE IMPRIMIR n TICKETS
                        getMultiplex().getCreditCardManager().returnCreditCard();
                        getDispenser().setDescription(language.getString("thanks"));//todo cambiar

                        return true;
                    }
                    else {
                        getMultiplex().getCreditCardManager().returnCreditCard();
                        printInsufficientBalanceMessage();
                        return false;
                    }
                } catch (CommunicationException ignored) {}
            }
            getMultiplex().getCreditCardManager().returnCreditCard();
            printUnavailabilityMessage();
        }
        return false;
    }

    private void displayDiscount(){

        totalPrice = (totalPrice * 7) / 10;
        builder.reset();
        builder.setTitle(this.toString());

        String desc = String.format("%s\n\n%s", language.getString("discount"), getFormattedPricing());

        builder.setDescription(desc);

        MessageModeSelector selector = builder.build();
        selector.display();
        selector.wait(3);
    }

    private ArrayList<ArrayList<String>> makeTicketPack(ArrayList<Seat> seats){

        ArrayList<ArrayList<String>> ticketPack = new ArrayList<>();

        int i = 1;
        for (Seat seat:seats) {

            Movie movie = selectedTheater.getMovie();

            ArrayList<String> ticket = new ArrayList<>();

            String header = this.toString();
            String price = getFormattedPricing();
            String session = String.format("%s: %s", language.getString("session"), selectedSession);

            String duration = String.format("%s: %s %s",
                    language.getString("duration"), movie.getDuration(), language.getString("minutes"));

            String date = getFormattedDate(LocalDate.now());

            String seating = getFormattedSeating(seat);

            String ticketNumber = getFormattedTicketNumber(i, seats.size());

            ticket.add(header);
            ticket.add(price);
            ticket.add(date);
            ticket.add(session);
            ticket.add(duration);
            ticket.add(seating);
            ticket.add(ticketNumber);
            ticket.add(getRandomBarcode());

            ticketPack.add(ticket);
            i++;
        }
        return ticketPack;
    }

    private String getFormattedPricing(){//todo mover los métodos formater a una clase?
        return String.format("%s: %d€", language.getString("price"), totalPrice);
    }

    private String getFormattedDate(LocalDate date){

        Locale locale = language.getLocale();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);

        String locallyFormattedDate = date.format(dateFormatter);

        return String.format("%s: %s", language.getString("date"), locallyFormattedDate);
    }

    private String getFormattedSeating(Seat seat){

        return String.format("%s:\n  %s %d\n  %s %d\n",
                language.getString("seat"),
                language.getString("row"), seat.row(),
                language.getString("column"), seat.col());
    }

    private String getFormattedTicketNumber(int i, int seats){
        Object[] numbers ={
                i,
                seats
        };
        MessageFormat ticketNumberFormatter = new MessageFormat(language.getString("ticketNumber"));
        ticketNumberFormatter.setLocale(language.getLocale());
        return  ticketNumberFormatter.format(numbers);
    }

    private String getRandomBarcode(){
        StringBuilder builder = new StringBuilder("\n");
        Random random = new Random();

        for (float i=0;i<22;i++) {
            int pick = random.nextInt(3);
            switch (pick) {
                case 0-> {builder.append("❘"); i-=0.9;}
                case 1-> builder.append("❙");
                case 2-> {builder.append("❚"); i+=0.2;}
            }
        }
        return builder.toString();
    }

    private void printUnavailabilityMessage(){
        builder.reset();
        builder.setTitle(language.getString("sorry"));

        builder.setDescription(language.getString("unavailable"));

        MessageModeSelector selector = builder.build();
        selector.display();
        selector.wait(3);
    }

    private void printInsufficientBalanceMessage(){
        builder.reset();
        builder.setDescription(language.getString("balance"));

        MessageModeSelector selector = builder.build();

        selector.display();
        selector.wait(3);

        getMultiplex().getCreditCardManager().returnCreditCard();
    }

    private int computePrice(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        return selectedTheater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String toString() {

        String plural;

        if (selectedSeats.size()>1)
            plural = "s";
        else
            plural = "";

        Object[] messageArguments ={
                selectedSeats.size(),
                selectedTheater.getMovie(),
                plural
        };
        MessageFormat purchases = new MessageFormat(language.getString("purchase"));
        purchases.setLocale(language.getLocale());

        return purchases.format(messageArguments);
    }
}
