package cinema.menu_building;

import cinema.Multiplex;
import sienens.CinemaTicketDispenser;

/**
 *
 *
 *
 * @author Stefano
 */
public final class MessageModeSelector extends AbstractSelector {

    private final boolean acceptsViaCreditCards;
    private final boolean hasAcceptButton;
    private final String acceptButtonKey;

    private MessageModeSelector(Builder builder){
        super(builder);
        this.acceptsViaCreditCards = builder.acceptsViaCreditCards;
        this.hasAcceptButton = builder.hasAcceptButton;
        this.acceptButtonKey = builder.acceptButtonKey;
    }

    /**
     * This inner class's purpose is to modularly construct each attribute of the class it is contained in.
     */
    @SuppressWarnings("UnusedReturnValue")
    public final static class Builder extends AbstractSelector.Builder{

        private boolean acceptsViaCreditCards = false;
        private boolean hasAcceptButton = false;
        private String acceptButtonKey = "accept";

        public Builder(CinemaTicketDispenser dispenser, Multiplex multiplex){
            super(dispenser, multiplex);
        }
        public Builder setAcceptButton(){
            this.hasAcceptButton = true;
            this.acceptsViaCreditCards = false;
            return this;
        }
        public Builder setAcceptViaCreditCard(){
            this.hasAcceptButton = false;
            this.acceptsViaCreditCards = true;
            return this;
        }
        public MessageModeSelector build(){
            return new MessageModeSelector(this);
        }

        /**
         * Resets the builder to its default settings.
         */
        @Override
        public Builder reset(){
            super.reset();
            acceptsViaCreditCards = false;
            hasAcceptButton = false;
            acceptButtonKey = "accept";
            return this;
        }
    }

    /**
     * gets the instance of <code>Object</code> corresponding to the customer's response
     * @param dispenserReturn char returned from <code>CinemaTicketDispenser</code>'s <code>waitEvent(int seconds)</code> method
     * @return <p><code>true Boolean</code> if customer accepts (can be via credit card)</p>
     *         <p><code>false Boolean</code> if customer presses the cancel button</p>
     *         <p><code>null</code> if a credit card is inserted with the <code>MessageModeSelector</code>
     *         being set to accept with a button</p>
     */
    @Override
    public Object getIterationPick(char dispenserReturn){

        Boolean accepted;

        if (hasAcceptButton)
            switch (dispenserReturn){
                case '1' ->{
                    accepted = null;
                    super.dealWithUnwantedCard();
                }
                case 'A' -> accepted = false;
                case 'B' -> accepted = true;
                default -> throw new RuntimeException();
            }
        else if(acceptsViaCreditCards)
            switch (dispenserReturn){
                case '1' -> accepted = true;
                case 'A' -> accepted = false;
                default -> throw new RuntimeException();
            }
        else
            throw new RuntimeException();

        return accepted;
    }

    @Override
    void changeDispenserMode() {
        getDispenser().setMessageMode();
    }

    @Override
    void displayOptionButtons(){
        if(super.hasCancelButton()){
            getDispenser().setOption(0, getMultiplex().getLanguage().getString("cancel"));
        }else{
            getDispenser().setOption(0, null);
        }

        if(hasAcceptButton){
            getDispenser().setOption(1, getMultiplex().getLanguage().getString(acceptButtonKey));
        }
        else{
            getDispenser().setOption(1, null);
        }
    }
}
