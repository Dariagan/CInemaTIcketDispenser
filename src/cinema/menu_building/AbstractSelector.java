package cinema.menu_building;

import cinema.Multiplex;
import sienens.CinemaTicketDispenser;

import static java.util.Objects.isNull;

public abstract class AbstractSelector {

    private final CinemaTicketDispenser dispenser;
    private final Multiplex multiplex;
    private final String TITLE;
    private final String DESCRIPTION;
    private final boolean HAS_CANCEL_BUTTON;
    private final String cancelButtonKey;


    String getTITLE() {return TITLE;}
    String getDESCRIPTION() {return DESCRIPTION;}
    CinemaTicketDispenser getDispenser() {return dispenser;}
    Multiplex getMultiplex() {return multiplex;}
    boolean hasCancelButton() {return HAS_CANCEL_BUTTON;}
    String getCancelButtonKey() {return cancelButtonKey;}

    AbstractSelector(Builder builder){
        this.dispenser = builder.dispenser;
        this.multiplex = builder.multiplex;
        this.TITLE = builder.title;
        this.DESCRIPTION = builder.description;
        this.HAS_CANCEL_BUTTON = builder.hasCancelButton;
        this.cancelButtonKey = builder.cancelButtonKey;
    }

    public abstract static class Builder {
        private final CinemaTicketDispenser dispenser;
        private final Multiplex multiplex;
        private String title = "";
        private String description = "";
        private boolean hasCancelButton = false;
        private String cancelButtonKey = "cancel";

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
        public Builder setCancelButtonKey(String key){
            this.cancelButtonKey = key;
            return this;
        }
        public abstract AbstractSelector build();
    }

    public void display(){
        changeDispenserMode();
        displayTextAndExtra();
        displayOptionButtons();
    }

    public Object getPick(){
        return doGetPickLoop();
    }

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
    abstract Object getIterationPick(char dispenserReturn);

    private boolean continueLoop;

    void endLoop(){continueLoop = false;}

    void dealWithUnwantedCard(){
        if (getMultiplex().getCreditCardManager().returnUnwantedCard()) {
            display();
        }
        else endLoop();
    }

    abstract void changeDispenserMode();

    private void displayTextAndExtra(){
        dispenser.setTitle(TITLE);
        dispenser.setDescription(DESCRIPTION);
        setExtra();
    }
    void setExtra(){}

    abstract void displayOptionButtons();

}
