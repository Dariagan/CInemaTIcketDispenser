import sienens.CinemaTicketDispenser;

public final class CreditCardManager {

    public static boolean rejectCreditCard(CinemaTicketDispenser dispenser) {

        dispenser.setMessageMode();
        dispenser.setTitle("TARJETA INDEBIDAMENTE INTRODUCIDA");
        dispenser.setDescription("RETIRE LA TARJETA INTRODUCIDA");//todo translate
        dispenser.setOption(0, null);
        dispenser.setOption(1, null);

        dispenser.retainCreditCard(false);

        if (dispenser.expelCreditCard(30)) {
            return true;
        }
        else {
            dispenser.retainCreditCard(true);
            return false;
        }
    }

}
