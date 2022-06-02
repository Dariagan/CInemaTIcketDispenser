import sienens.CinemaTicketDispenser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public final class MovieTicketSale extends Operation{
    private final MultiplexState state;
    private final CinemaTicketDispenser dispenser;
    private final PerformPayment payment;

    public MovieTicketSale(CinemaTicketDispenser dispenser, Multiplex multi){
        super(dispenser, multi);
        this.dispenser = dispenser;
        //todo if (newDayOrRecovery)
            this.state = new MultiplexState();

        this.payment = new PerformPayment(dispenser, multi);
    }

    public boolean doOperation(){

        Theater selectedTheater = selectTheater();
        if(isNull(selectedTheater))
            return false;

        Session selectedSession = selectSession(selectedTheater);
        if(isNull(selectedSession))
            return false;

        ArrayList<Seat> selectedSeats = selectSeats(selectedTheater, selectedSession);
        if(isNull(selectedSeats))
            return false;

        performPayment(selectedTheater, selectedSeats);

        return true;
    }

    private int dispenserReturnToIndex(char dispenserReturn){
        return dispenserReturn - 'A';
    }

    private boolean listElementWasPicked(char dispenserReturn, List list){
            return dispenserReturnToIndex(dispenserReturn) < list.size();
    }

    private Object getPick(List list){

        char dispenserReturn = dispenser.waitEvent(30);

        if (dispenserReturn == 0)
            return null;
        else if (dispenserReturn == '1')
            if(CreditCardManager.retrievedCreditCard(dispenser))
                getPick(list);
            else return null;
        else if(listElementWasPicked(dispenserReturn, list))
            return (list.get(dispenserReturnToIndex(dispenserReturn)));
        else return null;

        return null;
    }

    private void setOptions(List list){
        for (int i = 0; i < list.size(); i++){
            dispenser.setOption(i, list.get(i).toString());
        }
        dispenser.setOption(list.size(), "CANCELAR");
        for (int i = list.size()+1; i <= 5; i++){
            dispenser.setOption(i, null);
        }
    }

    private Theater selectTheater(){

        dispenser.setTitle(this.getTitle());

        setOptions(state.getTheaters());

        return (Theater)getPick(state.getTheaters());
    }

    private Session selectSession(Theater theater){

        dispenser.setTitle("duración: x. seleccione sesión");
        dispenser.setImage(theater.getMOVIE().getIMAGE());
        dispenser.setDescription(theater.getMOVIE().getDESCRIPTION());

        setOptions(theater.getSessionList());

        return (Session)getPick(theater.getSessionList());
    }

    private ArrayList<Seat> selectSeats(Theater theater, Session session){

        dispenser.setTitle("seleccione butacas");
        dispenser.setOption(0, "cancelar");
        dispenser.setOption(1, null);

        presentSeats(theater, session);

        boolean cancel, accept;

        ArrayList<Seat> selectedSeats = new ArrayList<>();

        do{
            char dispenserReturn = dispenser.waitEvent(30);
            switch (dispenserReturn){
                case 0,'A' -> {
                    cancel = true; accept = false;
                    for (Seat seat:selectedSeats){
                        session.unoccupySeat(seat);
                        dispenser.markSeat(seat.row(), seat.col(), Seat.State.UNOCCUPIED.ordinal());
                    }
                    selectedSeats.clear();
                }

                //TODO case 1: TRATAR CON TARJETA METIDA
                case '1' ->{
                    if (CreditCardManager.retrievedCreditCard(dispenser)){
                        cancel = false;
                    } else cancel = true; accept = false;
                }

                case 'B' -> {cancel = false; accept = true;}
                default -> {
                    cancel = false; accept = false;
                    Seat pickedSeat = getSeatFromEncodedChar(dispenserReturn);
                    if (selectedSeats.contains(pickedSeat)) {
                        selectedSeats.remove(pickedSeat);
                        session.unoccupySeat(pickedSeat);
                        dispenser.markSeat(pickedSeat.row(), pickedSeat.col(), Seat.State.UNOCCUPIED.ordinal());
                    }
                    else if (theater.getSeatSet().contains(pickedSeat)
                            && !session.isOccupied(pickedSeat)
                            && selectedSeats.size() < 4){
                        selectedSeats.add(pickedSeat);
                        session.occupySeat(pickedSeat);
                        dispenser.markSeat(pickedSeat.row(), pickedSeat.col(), Seat.State.OCCUPIED.ordinal());
                    }
                }
            }
            if (!cancel && !selectedSeats.isEmpty()) dispenser.setOption(1, "aceptar");
            else dispenser.setOption(1, null);
        }while(!(accept||cancel));

        if (accept){
            return selectedSeats;
        }
        else return null;
    }

    private void presentSeats(Theater theater, Session session){

        final int MAX_ROWS = theater.getMaxRows();
        final int MAX_COLS = theater.getMaxCols();

        dispenser.setTheaterMode(MAX_ROWS, MAX_COLS);

        for (int i = 1; i <= MAX_ROWS; i++){
            for (int j = 1; j <= MAX_COLS; j++){
                final Seat.State state;
                Seat seatAtPos = new Seat(i, j);

                if (theater.getSeatSet().contains(seatAtPos)){
                    if (session.isOccupied(seatAtPos))
                        state = Seat.State.OCCUPIED;
                    else state = Seat.State.UNOCCUPIED;
                }
                else state = Seat.State.NOT_A_SEAT;

                dispenser.markSeat(i, j, state.ordinal());
            }
        }
    }

    private int extractByte(char encodedChar,int offsetFromRight) {
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

    public String getTitle(){
        return "comprar ticket";
    }

    private void serializeMultiplexState(){

    }

    private void deserializeMultiplexState(){

    }
}
