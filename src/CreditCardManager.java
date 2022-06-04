import sienens.CinemaTicketDispenser;

import java.util.Arrays;

public final class CreditCardManager {

    private final long[] allAssociatesArray;

    public CreditCardManager(long[] allAssociatesArray) {
        Arrays.sort(allAssociatesArray);
        this.allAssociatesArray = allAssociatesArray;
    }

    public static boolean rejectCreditCard(CinemaTicketDispenser dispenser) {

        dispenser.setMessageMode();
        dispenser.setTitle("TARJETA INDEBIDAMENTE INTRODUCIDA");//todo translate

        dispenser.retainCreditCard(false);

        return returnCreditCard(dispenser);
    }

    public static boolean returnCreditCard(CinemaTicketDispenser dispenser){
        dispenser.setMessageMode();
        dispenser.setOption(0, null);
        dispenser.setOption(1, null);
        dispenser.setDescription("retire su tarjeta");//todo translate
        return recCreditCardPickedUp(dispenser, 10);
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
        int l = 0;
        int r = allAssociatesArray.length-1;

        while (l <= r) {
            int mid = (l + r)/2;

            if (card == allAssociatesArray[mid])
                return true;
            else if (card < allAssociatesArray[mid])
                r = mid - 1;
            else
                l = mid + 1;
        }
        return false;
    }

}
