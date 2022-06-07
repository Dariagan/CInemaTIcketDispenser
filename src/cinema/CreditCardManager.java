package cinema;

import sienens.CinemaTicketDispenser;

import java.io.Serializable;

public final class CreditCardManager extends Operation implements Serializable {

    public CreditCardManager(CinemaTicketDispenser dispenser, Multiplex multiplex) {
        super(dispenser, multiplex);
    }

    @Override
    public boolean doOperation() {
        return returnUnwantedCard();
    }

    @Override
    public String toString() {
        return "Credit card manager";
    }

    public boolean returnUnwantedCard() {

        getDispenser().setMessageMode();
        getDispenser().setTitle(getMultiplex().getLanguage().getString("unwantedCard"));

        return returnCreditCard();
    }

    public boolean returnCreditCard(){
        getDispenser().setMessageMode();
        getDispenser().setOption(0, null);
        getDispenser().setOption(1, null);
        getDispenser().setDescription(getMultiplex().getLanguage().getString("withdrawCard"));

        boolean customerRecoveredCard;

        getDispenser().retainCreditCard(false);//si no pongo esto no espera y se traga la tarjeta instantáneamente
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
