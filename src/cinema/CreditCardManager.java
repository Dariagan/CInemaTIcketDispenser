package cinema;

import sienens.CinemaTicketDispenser;

import java.util.HashSet;

public final class CreditCardManager {

    HashSet<Long> allAssociates;

    public CreditCardManager(HashSet<Long> allAssociates) {
        this.allAssociates = allAssociates;
    }

    public static boolean returnUnwantedCard(CinemaTicketDispenser dispenser) {

        dispenser.setMessageMode();
        dispenser.setTitle("TARJETA INDEBIDAMENTE INTRODUCIDA");//todo translate

        return returnCreditCard(dispenser);
    }

    public static boolean returnCreditCard(CinemaTicketDispenser dispenser){
        dispenser.setMessageMode();
        dispenser.setOption(0, null);
        dispenser.setOption(1, null);
        dispenser.setDescription("retire su tarjeta");//todo translate
        return recCreditCardPickedUp(dispenser, 3);
    }

    private static boolean recCreditCardPickedUp(CinemaTicketDispenser dispenser, int nAttempts){

        if (dispenser.expelCreditCard(3)) {
            dispenser.setDescription("");
            return true;
        }
        else if (nAttempts > 0){
            return recCreditCardPickedUp(dispenser, nAttempts - 1);
        }
        else{
            dispenser.retainCreditCard(true);
            dispenser.setDescription("");
            return false;
        }
    }

    public boolean cardHasDiscount(Long card) {
        return allAssociates.contains(card);
    }

}
