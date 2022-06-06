package cinema;

import sienens.CinemaTicketDispenser;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.util.Objects.isNull;

public final class MovieTicketSale extends Operation {
    private MultiplexState state;
    private final PerformPayment payment;

    public MovieTicketSale(CinemaTicketDispenser dispenser, Multiplex multi){
        super(dispenser, multi);

        try {
            if(deserializeMultiplexState()) {
                if (state.getDate().isBefore(LocalDate.now())) {
                    this.state = new MultiplexState();
                    System.out.println("Multiplex state reset due to new day");
                }
            }else{
                System.out.printf("%s not found, resetting multiplex state.%n", MultiplexState.getFileName());
                state = new MultiplexState();
            }
        }catch (Exception e){
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
            } catch (Exception e){
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

        SelectorMenu.Builder sBuilder = new SelectorMenu.Builder(getDispenser(), state.getTheaters());

        sBuilder.title(this.toString()).description("no se").addCancelButton();

        SelectorMenu theaterSelector = sBuilder.build();
        theaterSelector.display();

        return (Theater) theaterSelector.getPick();
    }

    private Session selectSession(Theater theater){

        SelectorMenu.Builder sBuilder = new SelectorMenu.Builder(getDispenser(), theater.getSessionList());

        sBuilder.title("elegir sesión");
        sBuilder.description(theater.getMovie().getDescription());
        sBuilder.image(theater.getMovie().getImage());
        sBuilder.addCancelButton();

        SelectorMenu sessionSelector = sBuilder.build();
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

                case '1' ->{
                    if (CreditCardManager.returnUnwantedCard(getDispenser())){
                        cancel = false;
                        presentSeats(theater, session);
                    } else cancel = true; accept = false;
                }

                case 'B' -> {cancel = false; accept = true;}

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
            if (!cancel && !selectedSeats.isEmpty()) getDispenser().setOption(1, "aceptar");
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

        getDispenser().setTitle("seleccione butacas");
        getDispenser().setOption(0, "cancelar");
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
        return "comprar ticket";
    }

}
