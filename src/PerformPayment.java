import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

import java.util.ArrayList;

public final class PerformPayment extends Operation{
    private final UrjcBankServer bank = new UrjcBankServer();
    private Theater selectedTheater;
    private ArrayList<Seat> purchasedSeats;

    public void setPurchase(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        this.selectedTheater = selectedTheater; this.purchasedSeats = purchasedSeats;
    }
    @Override
    public boolean doOperation() {
        getDispenser().setMessageMode();
        getDispenser().setTitle(this.getTitle());

        int totalPrice = computePrice(selectedTheater, purchasedSeats);

        String description = String.format("%d entradas para %s: %d €",
                purchasedSeats.size(), selectedTheater.getMOVIE().getTITLE(), totalPrice);//TODO falta el translate

        getDispenser().setDescription(description);
        getDispenser().setOption(0, "cancelar");
        getDispenser().setOption(1, null);


        switch (getDispenser().waitEvent(30)){
            case 0, 'A' -> {return false;}
            case '1' -> {
                if(bank.comunicationAvaiable())
                    try {
                        if(bank.doOperation(getDispenser().getCardNumber(), totalPrice)) {
                            getDispenser().setOption(1, "aceptar");


                            return true;
                        }else return false;
                    }
                    catch (Exception e){
                        //todo nase
                        return false;
                    }
            }
            default -> {return false;}
        }

        return false;
    }

    private int computePrice(Theater selectedTheater, ArrayList<Seat> purchasedSeats){
        return selectedTheater.getPRICE() * purchasedSeats.size();
    }

    @Override
    public String getTitle() {return null;}

    public PerformPayment(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
    }
}
