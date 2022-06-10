package cinema;

import cinema.menu_building.MenuModeSelector;
import sienens.CinemaTicketDispenser;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.util.Objects.isNull;

public final class MovieTicketSale extends Operation {
    private MultiplexState state;
    private final PerformPayment payment;
    private ResourceBundle language;


    public MovieTicketSale(CinemaTicketDispenser dispenser, Multiplex multi){
        super(dispenser, multi);

       instantiateState();

       this.payment = new PerformPayment(dispenser, multi, state);
    }

    /**
     * Instantiates the attribute <code>state</code> from this class, accordingly to the state of the "state.dat" file in
     * the project directory.
     */
    private void instantiateState(){

        String usingNewState = ", using new multiplex state.\n";
        try {
            if (deserializeMultiplexState()) {
                if (state.getCreationDate().isBefore(LocalDate.now())) {
                    System.out.printf("state.dat is from yesterday or older%s", usingNewState);
                    this.state = new MultiplexState();
                }else{
                    System.out.println("state.dat loaded");
                }
            }else {
                System.out.printf("%s not found%s", MultiplexState.getFileName(), usingNewState);
                this.state = new MultiplexState();
            }

        }catch (IOException|ClassNotFoundException e){
            if (e instanceof InvalidClassException){
                System.out.printf("state.dat's file structure is obsolete%s", usingNewState);
                this.state = new MultiplexState();
            }else
                throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current multiplex state in a file called "state.dat" in the project directory.
     */
    public void serializeMultiplexState() throws IOException {
        FileOutputStream fOut = new FileOutputStream(MultiplexState.getFileName());
        ObjectOutputStream out = new ObjectOutputStream(fOut);
        out.writeObject(state);
        out.flush();
        out.close();
    }

    /**
     * Instantiates the <code>state</code> attribute of this class from the "state.dat" file in the project directory,
     * if it's found.
     *
     * @return <p><code>true</code> if the state.dat file is found and successfully loaded</p>
     *         <p><code>false</code> if the file isn't found</p>
     */
    public boolean deserializeMultiplexState() throws IOException, ClassNotFoundException {
        if (new File(MultiplexState.getFileName()).isFile()) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(MultiplexState.getFileName()));
            state = (MultiplexState) in.readObject();
            in.close();
            return true;
        } else
            return false;
    }

    /**
     * Handles the general flow of the sale
     * @return <p><code>true</code> if the sale was completed successfully in the method <code>performPayment</code>.</p>
     *         <p><code>false</code> if:
     *         <ul>
     *             <li>cancel button is pressed by the customer at any given time</li>
     *             <li>no movie (theater) was selected by the customer until timeout</li>
     *             <li>no session was selected by the customer until timeout</li>
     *             <li>no seat was selected by the customer until timeout</li>
     *             <li><code>performPayment<code>'s call returns <code>false</code></li>
     *         </ul>
     *         </p>
     */
    public boolean doOperation(){

        language = getMultiplex().getLanguage();

        Theater selectedTheater = selectTheater();
        if(isNull(selectedTheater))
            return false;

        Session selectedSession = selectSession(selectedTheater);
        if(isNull(selectedSession))
            return false;

        ArrayList<Seat> selectedSeats = selectSeats(selectedTheater, selectedSession);
        if(isNull(selectedSeats))
            return false;

        boolean successfulPayment = performPayment(selectedTheater, selectedSession, selectedSeats);

        if (successfulPayment){
            try{
                serializeMultiplexState();
                return true;
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        else{
            for (Seat seat : selectedSeats)
                selectedSession.unoccupySeat(seat);
            return false;
        }
    }

    /**
     * Builds a selector menu via <code>MenuModeSelector.Builder</code>, which handles the display and events.
     * Presents on the dispenser screen the list of available movies to select when calling
     * <code>theaterSelector.getPick()</code>.
     * @return <p>the theater which contains the movie title the customer selected</p>
     *         <p><code>null</code> if no movie was selected by the customer.</p>
     */
    private Theater selectTheater(){

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());

        builder.setOptionList(state.getTheaters());
        builder.setTitle(language.getString("selectMovie"));
        builder.setCancelButton();

        MenuModeSelector theaterSelector = builder.build();

        return (Theater) theaterSelector.getPick();
    }

    /**
     * @param theater theater (movie title) selected by the customer
     * @return session which was selected by the customer, or null if no session was selected.
     */
    private Session selectSession(Theater theater){

        Movie movie = theater.getMovie();

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(theater.getSessionList());
        builder.setTitle(language.getString("selectSession"));

        String duration = TicketFormatter.getFormattedDuration(movie.getDuration(), language);
        String pricing = TicketFormatter.getFormattedPricing(theater.getPRICE(), language);
        String description = String.format("%s\n%s\n%s", movie.getDescription(), duration, pricing);

        builder.setDescription(description);
        builder.setImage(movie.getImage());
        builder.setCancelButton();

        MenuModeSelector sessionSelector = builder.build();

        return (Session) sessionSelector.getPick();
    }

    /**
     * Handles the flow accordingly to the obtained customer input after presenting to him the theater onscreen.
     * @param theater theater which was previously selected, contains the set of existent seats.
     * @param session session which was previously selected, contains the set of already occupied seats.
     * @return list of seats selected from the dispenser screen
     */
    private ArrayList<Seat> selectSeats(Theater theater, Session session){

        boolean cancel, accept;

        ArrayList<Seat> selectedSeats = new ArrayList<>();

        presentSeats(theater, session);
        do{
            char dispenserReturn = getDispenser().waitEvent(30);
            switch (dispenserReturn){

                case 0,'A' -> {//(timeout o botón cancelar)
                    cancel = true; accept = false;
                    unocuppyAllSelectedSeats(selectedSeats, session);
                }
                case '1' ->{//tarjeta introducida
                    if (getMultiplex().getCreditCardManager().returnUnwantedCard()){
                        cancel = false;
                        presentSeats(theater, session);
                    } else cancel = true; accept = false;
                }
                //botón aceptar
                case 'B' -> {cancel = false; accept = true;}

                //click en mapa del teatro
                default -> {
                    cancel = false; accept = false;
                    handleClick(dispenserReturn, theater, selectedSeats, session);
                }
            }
            if (!cancel && !selectedSeats.isEmpty())
                getDispenser().setOption(1, language.getString("accept"));
            else
                getDispenser().setOption(1, null);
        }while(!(accept || cancel));

        if (accept){
            return selectedSeats;
        }
        else return null;
    }

    /**
     * Presents on the dispenser screen the seating from the passed <code>theater</code> in the state they are in the
     * passed <code>session</code>.
     * @param theater theater from which the seats will be extracted and presented on screen
     * @param session contains the state of the seats, which is represented on screen
     */
    private void presentSeats(Theater theater, Session session){

        final int MAX_ROWS = theater.getMaxRows();
        final int MAX_COLS = theater.getMaxCols();

        getDispenser().setTitle(language.getString("selectSeats"));
        getDispenser().setOption(0, language.getString("cancel"));
        getDispenser().setOption(1, null);
        getDispenser().setTheaterMode(MAX_ROWS, MAX_COLS);

        for (int i = 1; i <= MAX_ROWS; i++){
            for (int j = 1; j <= MAX_COLS; j++){

                final Seat.State state;

                Seat seatAtPos = new Seat(i, j);

                if (theater.hasSeat(seatAtPos)){
                    if (session.isOccupied(seatAtPos))
                        state = Seat.State.OCCUPIED;
                    else
                        state = Seat.State.UNOCCUPIED;
                }
                else
                    state = Seat.State.NOT_A_SEAT;

                getDispenser().markSeat(i, j, state.ordinal());
            }
        }
    }

    /**
     *  Handles a click on a seat from the displayed theater in the screen,
     *  accordingly to its state (selected, occupied, not a seat).
     * @param encodedDispenserReturn encoded char returned from the dispenser when a seat on the dispenser screen is clicked on
     * @param theater currently shown theater
     * @param selectedSeats currently selected seats
     * @param session currently shown session
     */
    private void handleClick(char encodedDispenserReturn, Theater theater, ArrayList<Seat> selectedSeats, Session session){

        Seat pickedSeat = getSeatFromEncodedChar(encodedDispenserReturn);

        if (selectedSeats.contains(pickedSeat))
            unocuppyReClickedSeat(pickedSeat, selectedSeats, session);

        else if (validPick(pickedSeat, theater, selectedSeats, session))
            occupySeat(pickedSeat, selectedSeats, session);
    }

    private final int MAX_PURCHASABLE_SEATS = 4;

    /**
     * Checks whether the (previously unselected) selected seat is valid for occupation, or not.
     * @param pickedSeat clicked seat
     * @param theater currently displayed theater
     * @param selectedSeats contains the currently selected seats by the customer
     * @param session contains the state of the displayed theater's seats
     * @return <p><code>true</code> if the seat is not an empty space, isn't occupied by someone else,
     * and if the maximum number of selected seats is not surpassed.</p>
     * <p><code>false</code> if otherwise.</p>
     */
    private boolean validPick(Seat pickedSeat, Theater theater, ArrayList<Seat> selectedSeats , Session session){
        boolean isNotEmptySpace = theater.hasSeat(pickedSeat);

        boolean isOccupied = session.isOccupied(pickedSeat);

        boolean isBelowMaxNumOfSelectableSeats = selectedSeats.size() < MAX_PURCHASABLE_SEATS;

        return isNotEmptySpace && !isOccupied && isBelowMaxNumOfSelectableSeats;
    }
    private void occupySeat(Seat seat, ArrayList<Seat> selectedSeats, Session session){
        selectedSeats.add(seat);
        session.occupySeat(seat);
        getDispenser().markSeat(seat.row(), seat.col(), Seat.State.SELECTED.ordinal());
    }
    private void unocuppyReClickedSeat(Seat seat, ArrayList<Seat> selectedSeats, Session session){
        unoccupySeat(seat, session);
        selectedSeats.remove(seat);
    }
    private void unocuppyAllSelectedSeats(ArrayList<Seat> selectedSeats, Session session){
        for (Seat seat:selectedSeats)
            unoccupySeat(seat, session);
        selectedSeats.clear();
    }
    private void unoccupySeat(Seat seat, Session session){
        session.unoccupySeat(seat);
        getDispenser().markSeat(seat.row(), seat.col(), Seat.State.UNOCCUPIED.ordinal());
    }

    private int extractByte(char encodedDispenserReturn, int offsetFromRight) {
        return ((1 << 8) - 1) & (encodedDispenserReturn >> (offsetFromRight));
    }
    private Seat getSeatFromEncodedChar(char in) {

        int row = extractByte(in, 8);
        int col = extractByte(in, 0);

        return new Seat(row, col);
    }

    /**
     * Delegates the handling of the payment to the <code>PerformPayment</code> class's method <code>doOperation</code>
     * @return <p><code>true</code> if the payment is completed successfully</p>
     *         <p><code>false</code> if the payment is not completed, cancelled, or interrupted</p>
     */
    private boolean performPayment(Theater selectedTheater, Session selectedSession, ArrayList<Seat> selectedSeats) {

        payment.setPurchase(selectedTheater, selectedSession, selectedSeats);

        return payment.doOperation();
    }

    public String toString(){
        return getMultiplex().getLanguage().getString("buyTicket");
    }

}
