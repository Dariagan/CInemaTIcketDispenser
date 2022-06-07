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
        String usingNewState = ", using new multiplex state.\n";
        try {
            if (deserializeMultiplexState()) {
                if (state.getDate().isBefore(LocalDate.now())) {
                    System.out.printf("state.dat is a day old%s", usingNewState);
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
                System.out.printf("state.dat file-format is obsolete%s", usingNewState);
                this.state = new MultiplexState();
            }else
                throw new RuntimeException(e);
        }
        this.payment = new PerformPayment(dispenser, multi, state);
    }

    public void serializeMultiplexState() throws IOException {
        FileOutputStream fOut = new FileOutputStream(MultiplexState.getFileName());
        ObjectOutputStream out = new ObjectOutputStream(fOut);
        out.writeObject(state);
        out.flush();
        out.close();
    }
    public boolean deserializeMultiplexState() throws IOException, ClassNotFoundException {
        if (new File(MultiplexState.getFileName()).isFile()) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(MultiplexState.getFileName()));
            state = (MultiplexState) in.readObject();
            in.close();
            return true;
        } else return false;
    }

    public boolean doOperation(){

        language = getMultiplex().getLanguage();

        Theater selectedTheater = selectTheater();
        if(isNull(selectedTheater)) return false;

        Session selectedSession = selectSession(selectedTheater);
        if(isNull(selectedSession)) return false;

        ArrayList<Seat> selectedSeats = selectSeats(selectedTheater, selectedSession);
        if(isNull(selectedSeats)) return false;

        if (performPayment(selectedTheater, selectedSeats)){
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

    private Theater selectTheater(){

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());

        builder.setOptionList(state.getTheaters());
        builder.setTitle(language.getString("selectMovie"));
        builder.setCancelButton();

        MenuModeSelector theaterSelector = builder.build();
        theaterSelector.display();

        return (Theater) theaterSelector.getPick();
    }

    private Session selectSession(Theater theater){

        Movie movie = theater.getMovie();

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(theater.getSessionList());
        builder.setTitle(language.getString("selectSession"));
        String description = String.format("%s\n%s: %d %s",
                movie.getDescription(), language.getString("duration"),
                movie.getDuration(), language.getString("minutes"));

        builder.setDescription(description);
        builder.setImage(theater.getMovie().getImage());
        builder.setCancelButton();

        MenuModeSelector sessionSelector = builder.build();
        sessionSelector.display();

        return (Session) sessionSelector.getPick();
    }

    private ArrayList<Seat> selectSeats(Theater theater, Session session){

        presentSeats(theater, session);

        boolean cancel, accept;

        ArrayList<Seat> selectedSeats = new ArrayList<>();

        do{
            char dispenserReturn = getDispenser().waitEvent(30);
            switch (dispenserReturn){

                case 0,'A' -> {//(timeout o cancel)
                    cancel = true; accept = false;
                    for (Seat seat:selectedSeats){
                        session.unoccupySeat(seat);
                        getDispenser().markSeat(seat.row(), seat.col(), Seat.State.UNOCCUPIED.ordinal());
                    }
                    selectedSeats.clear();
                }

                case '1' ->{//tarjeta introducida
                    if (getMultiplex().getCreditCardManager().returnUnwantedCard()){
                        cancel = false;
                        presentSeats(theater, session);
                    } else cancel = true; accept = false;
                }

                case 'B' -> {cancel = false; accept = true;}//"aceptar"

                default -> {
                    cancel = false; accept = false;
                    Seat pickedSeat = getSeatFromEncodedChar(dispenserReturn);
                    if (selectedSeats.contains(pickedSeat)) {
                        selectedSeats.remove(pickedSeat);
                        session.unoccupySeat(pickedSeat);
                        getDispenser().markSeat(pickedSeat.row(), pickedSeat.col(), Seat.State.UNOCCUPIED.ordinal());
                    }
                    else if (theater.hasSeat(pickedSeat)
                            && !session.isOccupied(pickedSeat)
                            && selectedSeats.size() < 4){
                        selectedSeats.add(pickedSeat);
                        session.occupySeat(pickedSeat);
                        getDispenser().markSeat(pickedSeat.row(), pickedSeat.col(), Seat.State.SELECTED.ordinal());
                    }
                }
            }
            if (!cancel && !selectedSeats.isEmpty()) getDispenser().setOption(1, language.getString("accept"));
            else getDispenser().setOption(1, null);
        }while(!(accept || cancel));

        if (accept){
            return selectedSeats;
        }
        else return null;
    }

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
                    else state = Seat.State.UNOCCUPIED;
                }
                else state = Seat.State.NOT_A_SEAT;

                getDispenser().markSeat(i, j, state.ordinal());
            }
        }
    }

    private int extractByte(char encodedChar, int offsetFromRight) {
        return ((1 << 8) - 1) & (encodedChar >> (offsetFromRight));
    }
    private Seat getSeatFromEncodedChar(char in) {

        int row = extractByte(in, 8);
        int col = extractByte(in, 0);

        return new Seat(row, col);
    }

    private boolean performPayment(Theater selectedTheater, ArrayList<Seat> selectedSeats) {

        payment.setPurchase(selectedTheater, selectedSeats);

        return payment.doOperation();
    }

    public String toString(){
        return getMultiplex().getLanguage().getString("buyTicket");
    }

}
