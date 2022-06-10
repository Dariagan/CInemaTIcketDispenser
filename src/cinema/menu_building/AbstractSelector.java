package cinema.menu_building;

import cinema.Multiplex;
import sienens.CinemaTicketDispenser;

import static java.util.Objects.isNull;

/**
 * Abstract class designed for the inheritance of the common attributes and methods among the selectors.
 */
public abstract class AbstractSelector {

    private final CinemaTicketDispenser dispenser;
    private final Multiplex multiplex;
    private final String TITLE;
    private final String DESCRIPTION;
    private final boolean HAS_CANCEL_BUTTON;
    private final String CANCEL_BUTTON_KEY = "cancel";

    CinemaTicketDispenser getDispenser() {return dispenser;}
    Multiplex getMultiplex() {return multiplex;}
    boolean hasCancelButton() {return HAS_CANCEL_BUTTON;}
    String getCANCEL_BUTTON_KEY() {return CANCEL_BUTTON_KEY;}

    AbstractSelector(Builder builder){
        this.dispenser = builder.dispenser;
        this.multiplex = builder.multiplex;
        this.TITLE = builder.title;
        this.DESCRIPTION = builder.description;
        this.HAS_CANCEL_BUTTON = builder.hasCancelButton;
    }

    /**
     * This inner class's purpose is to modularly construct each attribute of the class it is contained in.
     */
    public abstract static class Builder {
        private final CinemaTicketDispenser dispenser;
        private final Multiplex multiplex;
        private String title = "";
        private String description = "";
        private boolean hasCancelButton = false;

        boolean hasCancelButton() {
            return hasCancelButton;
        }

        public Builder(CinemaTicketDispenser dispenser, Multiplex multiplex){
            this.dispenser = dispenser;
            this.multiplex = multiplex;
        }
        public Builder setTitle(String title){
            this.title = title;
            return this;
        }
        public Builder setDescription(String description){
            this.description = description;
            return this;
        }
        public Builder setCancelButton(){
            this.hasCancelButton = true;
            return this;
        }
        public abstract AbstractSelector build();

        /**
         * Resets the builder to its default settings.
         */
        public Builder reset(){
            this.title = "";
            this.description = "";
            this.hasCancelButton = false;
            return this;
        }
    }

    /**
     * Displays the constructed menu for the passed number of <code>seconds</code>, while also handling unwanted inserted
     * credit cards in the process.
     *
     * @return <p><code>true</code> if no credit cards are inserted, or if the inserted credit card was recovered.</p>
     *         <p><code>false</code> if an inserted credit card is not recovered (meaning the customer left during the wait).</p>
     */
    public boolean show(int seconds){
        display();
        char dispenserReturn = getDispenser().waitEvent(seconds);

        if (dispenserReturn == '1')
            return dealWithUnwantedCard();
        else
            return true;
    }

    public Object getPick(){
        display();
        return doGetPickLoop();
    }

    /**
     *
     */
    private Object doGetPickLoop() {
        Object pick;
        continueLoop = true;
        do {
            char dispenserReturn = getDispenser().waitEvent(30);
            if (dispenserReturn == 0){
                endLoop();
                pick = null;
            }else {
                pick = getIterationPick(dispenserReturn);
                if (!isNull(pick)){
                    endLoop();
                }
            }
        } while (continueLoop);

        return pick;
    }

    /**
     * Template method to be implemented in subclasses of <code>AbstractSelector</code>
     * @return <code>Object</code> which customer picked from option menu, or <code>null</code> if nothing was picked
     */
    abstract Object getIterationPick(char dispenserReturn);

    private boolean continueLoop;

    void endLoop(){continueLoop = false;}

    boolean dealWithUnwantedCard(){
        if (getMultiplex().getCreditCardManager().returnUnwantedCard()) {
            display();
            return true;
        }
        else {
            endLoop();
            return false;
        }
    }

    private void display(){
        changeDispenserMode();
        displayTextAndExtra();
        displayOptionButtons();
    }

    abstract void changeDispenserMode();

    private void displayTextAndExtra(){
        dispenser.setTitle(TITLE);
        dispenser.setDescription(DESCRIPTION);
        displayExtra();
    }

    /**
     * Meant to be optionally overridden by the subclass if needed, but it's not required.
     */
    void displayExtra(){}

    abstract void displayOptionButtons();

}
