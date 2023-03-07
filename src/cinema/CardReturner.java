package cinema;

import sienens.CinemaTicketDispenser;

import java.io.Serializable;

public final class CardReturner extends Operation implements Serializable {

    public CardReturner(CinemaTicketDispenser dispenser, Multiplex multiplex) {
        super(dispenser, multiplex);
    }

    /**
     * @return <p><code>true</code> if the customer recovered his card</p>
     *         <p><code>false</code> if not</p>
     */
    @Override
    public boolean doOperation() {
        return returnCreditCard();
    }

    @Override
    public String toString() {
        return "Card returner";
    }

    /**
     * Displays a localized title on the dispenser which says that the customer's credit card is not wanted, and calls
     * <code>returnCreditCard()</code> to eject it, and to display a description which says to pick it up.
     * @return <p><code>true</code> if the customer recovered his card before <code>returnCreditCard()</code>'s timeout</p>
     *         <p><code>false</code> if not</p>
     */
    public boolean returnUnwantedCard() {

        getDispenser().setTitle(getMultiplex().getLanguage().getString("unwantedCard"));

        return returnCreditCard();
    }
    /**
     * Ejects the customer's credit card and displays a localized description on the dispenser which says to pick it up.
     * If the customer didn't recover it after thirty seconds, the card is indefinitely retained inside the machine.
     * @return <p><code>true</code> if the customer recovered his card before the timeout</p>
     *         <p><code>false</code> if not</p>
     */
    public boolean returnCreditCard(){
        getDispenser().setMessageMode();
        getDispenser().setOption(0, null);
        getDispenser().setOption(1, null);
        getDispenser().setDescription(getMultiplex().getLanguage().getString("withdrawCard"));

        boolean customerRecoveredCard;

        getDispenser().retainCreditCard(false);
        if (getDispenser().expelCreditCard(30)) {
            customerRecoveredCard = true;
        }
        else{
            getDispenser().retainCreditCard(true);
            customerRecoveredCard = false;
        }

        getDispenser().setDescription("");

        return customerRecoveredCard;
    }
}
