import sienens.CinemaTicketDispenser;

public final class CreditCardManager {

    public static boolean retrievedCreditCard(CinemaTicketDispenser dispenser) {

        boolean cardPickedUp = dispenser.expelCreditCard(30);

        if (!cardPickedUp) {
            dispenser.retainCreditCard(true);
            return false;
        }
        else return true;
    }

}
