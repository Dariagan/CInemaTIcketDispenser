import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import java.util.ArrayList;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();
    private Theater selectedTheater;
    private ArrayList<Seat> purchasedSeats;
    private int totalPrice;

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

        switch (getDispenser().waitEvent(30)){
            case 0, 'A' -> {return false;}
            case '1' -> {
                if(bank.comunicationAvaiable())
                    try {
                        if(bank.doOperation(getDispenser().getCardNumber(), totalPrice)) {
                            getDispenser().setOption(1, "aceptar");
                            //TODO comprobar que agarre su tarjeta de vuelta, ah y expulsarla

                            return true;
                        }else return false;
                    }
                    catch (Exception e){
                        printUnavailabilityMessage();
                        return false;
                    }
                else {
                    printUnavailabilityMessage();
                    //preguntar si sucede esto se resetea el ticket-making o se espera hasta q este available y se continúa
                    return false;
                }
            }
            default -> {return false;}
        }

    }

    private void printUnavailabilityMessage(){
        getDispenser().setDescription("en estos momentos no podemos atenderte");//TODO translate
    }

    private int computePrice(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        return selectedTheater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String toString() {
        String title = String.format("%d entradas para %s: %d €",//TODO falta el translate
                purchasedSeats.size(), selectedTheater.getMovie().getTITLE(), totalPrice);
        return title;
    }

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
    }
}
