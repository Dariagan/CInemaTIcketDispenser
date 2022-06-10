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
    private final MessageModeSelector.Builder builder = new MessageModeSelector.Builder(getDispenser(), getMultiplex());

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

    /**
     * <p>Handles the flow of the treatment of possible incidents during the beginning of the purchase
     * (communication with the bank not available, or the customer left)</p>
     * @return <p><code>true</code> if the subdelegate method <code>purchase()</code> managed to complete the purchase.</p>
     * <p><code>false</code> if the communication couldn't be established or was interrupted,
     * or if the subdelegate method couldn't complete the purchase.</p>
     */
    @Override
    public boolean doOperation() {
        language = getMultiplex().getLanguage();

        MessageModeSelector selector = createMenu(MenuType.INSERT_CARD);//createPurchaseSummaryMenu();

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
            displayMessage(Message.UNAVAILABLE, 3);
        }
        return false;
    }

    /**
     * <p>Handles flow of the ticket purchase from the point on when the communication with the bank is established.</p>
     * <p>Returns to the customer his previously inserted credit card if he can't cover the purchase with his balance, or,
     * if he completed the purchase successfully.</p>
     * @throws CommunicationException if the communication with the bank is interrupted
     * @return <p><code>true</code> if the purchase has been completed successfully</p>
     *         <p><code>false</code> if not</p>
     */
    private boolean purchase() throws CommunicationException {

        final boolean CUSTOMER_HAS_DISCOUNT = state.cardHasDiscount(getDispenser().getCardNumber());

        if (CUSTOMER_HAS_DISCOUNT) {
            totalPrice = (totalPrice * 7) / 10;
            displayMessage(Message.DISCOUNT, 3);
        }

        final boolean CARD_HAS_ENOUGH_BALANCE = bank.doOperation(getDispenser().getCardNumber(), totalPrice);

        if (CARD_HAS_ENOUGH_BALANCE) {

            MessageModeSelector selector = createMenu(MenuType.CONFIRMATION);//createConfirmPurchaseMenu();

            Object answer = selector.getPick();

            if (!isNull(answer) && (Boolean)answer) {

                for (ArrayList<String> ticket : makeTicketPack(selectedSeats, CUSTOMER_HAS_DISCOUNT))
                    getDispenser().print(ticket);

                getMultiplex().getCreditCardManager().returnCreditCard();

                displayMessage(Message.THANKS, 3);
                return true;
            }
        } else {
            displayMessage(Message.NO_BALANCE, 3);
        }
        getMultiplex().getCreditCardManager().returnCreditCard();
        return false;
    }

    private enum MenuType{
        INSERT_CARD{public String toString(){return "insertCreditCard";}},
        CONFIRMATION{public String toString(){return "confirm";}}
    }
    /**
     * <p>Internally builds an instance of <code>MessageModeSelector</code>, utilizing its class builder,
     * meant for handling the customer's paayment
     *
     * @return a constructed <code>MessageModeSelector</code> ready for display and handling dispenser events
     */
    private MessageModeSelector createMenu(MenuType menuType){
        builder.reset();
        builder.setTitle(this.toString());

        final String FORMATTED_PRICING = TicketFormatter.getFormattedPricing(totalPrice, language);
        final String DESCRIPTION = String.format("%s\n\n%s", FORMATTED_PRICING, language.getString(menuType.toString()));

        builder.setDescription(DESCRIPTION);
        builder.setCancelButton();

        switch (menuType) {
            case INSERT_CARD -> builder.setAcceptViaCreditCard();
            case CONFIRMATION -> builder.setAcceptButton();
            default -> throw new RuntimeException("MenuType not implemented");
        }
        return builder.build();
    }

    /**
     * <p>Returns an <code>ArrayList</code> of <code>ArrayList</code> of <code>String</code>, with the purpose of to later
     * on pass each <code>ArrayList</code> of <code>String</code> in the returned list to the <code>print()</code>
     * method of <code>CinemaTicketDispenser</code>.</p>
     * <p>This method extracts data from the attributes of the class it is contained in (<code>PerformPayment</code>),
     * and, for each seat in the passed <code>seats</code>, it formats all the necessary <code>String</code>'s required
     * for detailing the ticket, and adds them all to an <code>ArrayList</code> of <code>String</code>, which
     * is itself added to an <code>ArrayList</code> of <code>ArrayList</code> of <code>String</code>, which is finally
     * returned when the method is done iterating through the passed <code>seats</code></p>
     *
     * @param seats the list of seats the customer selected before for purchase
     * @param hasDiscount Whether the customer's credit card number was detected to have a discount, or not.
     * @return an <code>ArrayList</code> of n tickets to be passed to the dispenser, with the same size as the passed
     * <code>seats</code>
     */
    private ArrayList<ArrayList<String>> makeTicketPack(ArrayList<Seat> seats, boolean hasDiscount){

        final ArrayList<ArrayList<String>> ticketPack = new ArrayList<>();

        int i = 1;
        for (Seat seat:seats) {

            final Movie movie = selectedTheater.getMovie();
            final ArrayList<String> ticket = new ArrayList<>();

            ticket.add(TicketFormatter.getFormattedPurchase(selectedTheater.getMovie(), selectedSeats, language));
            ticket.add(TicketFormatter.getFormattedPricing(totalPrice, language));
            if (hasDiscount) ticket.add(language.getString("discount"));
            ticket.add(TicketFormatter.getFormattedDate(LocalDate.now(), language));
            ticket.add(TicketFormatter.getFormattedSession(selectedSession, language));
            ticket.add(TicketFormatter.getFormattedDuration(movie.getDuration(), language));
            ticket.add(TicketFormatter.getFormattedSeating(seat, language));
            ticket.add(TicketFormatter.getFormattedTicketNumber(i, seats.size(), language));
            ticket.add(TicketFormatter.getRandomBarcode());

            ticketPack.add(ticket);
            i++;
        }
        return ticketPack;
    }

    /**
     * <p><code>enum</code> used as a parameter for the <code>displayMessage</code> method.</p>
     * <p>When the <code>enum</code> value <code>UNAVAILABLE</code> is passed as a parameter for said method,
     * the built message notifies the customer that the service is unavailable.</p>
     * <p>When the <code>enum</code> value <code>NO_BALANCE</code> is passed as a parameter for said method,
     * the built message warns the customer of the lack of balance in his credit card to perform the operation.</p>
     * <p>When the <code>enum</code> value <code>DISCOUNT</code> is passed as a parameter for said method,
     * the built message notifies the customer that he gets a discount on his purchase.</p>
     * <p>When the <code>enum</code> value <code>THANKS</code> is passed as a parameter for said method,
     * the built message thanks the customer for his purchase.</p>
     */
    private enum Message{
        UNAVAILABLE, NO_BALANCE, DISCOUNT, THANKS
    }
    /**
     * <p>Internally builds a message, and then displays it on the dispenser screen for the passed <code>seconds</code>.</p>
     * @param message the message to internally build, and then display on the dispenser screen
     * @param seconds the number of seconds for which to display the message, and then continue
     */
    private void displayMessage(Message message, int seconds){
        if (seconds <= 0) throw new RuntimeException("Negative or zero number of seconds not allowed");
        builder.reset();
        switch (message){
            case UNAVAILABLE -> {
                builder.setTitle(language.getString("sorry"));
                builder.setDescription(language.getString("unavailable"));
            }
            case NO_BALANCE ->
                    builder.setDescription(language.getString("balance"));

            case DISCOUNT -> {
                builder.setTitle(this.toString());

                final String FORMATTED_PRICING = TicketFormatter.getFormattedPricing(totalPrice, language);

                builder.setDescription(String.format("%s\n\n%s", language.getString("discount"), FORMATTED_PRICING));
            }
            case THANKS ->
                    builder.setDescription(language.getString("thanks"));

            default -> throw new RuntimeException("Message not implemented");
        }
        MessageModeSelector selector = builder.build();
        selector.show(seconds);
    }

    /**
     * @param theater the theater from which the individual ticket price will be extracted
     * @param purchasedSeats the list of the seats which were selected for purchase
     * @return calculated total price for the purchase (not taking into account possible discounts)
     */
    private int computePrice(Theater theater, ArrayList<Seat> purchasedSeats){
        return theater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String toString() {
        return TicketFormatter.getFormattedPurchase(selectedTheater.getMovie(), selectedSeats, language);
    }
}
