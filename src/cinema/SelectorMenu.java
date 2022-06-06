package cinema;

import sienens.CinemaTicketDispenser;

import java.util.ArrayList;

public class SelectorMenu {

    private final CinemaTicketDispenser dispenser;
    private final ArrayList optionList;
    private final String title;
    private final String description;
    private final String image;
    private final boolean hasCancelButton;

    private SelectorMenu(Builder builder){
        this.dispenser = builder.dispenser;
        this.optionList = builder.optionList;
        this.title = builder.title;
        this.description = builder.description;
        this.image = builder.image;
        this.hasCancelButton = builder.hasCancelButton;
    }

    public static class Builder {
        private final CinemaTicketDispenser dispenser;
        private final ArrayList optionList;
        private String title = "";
        private String description = "";
        private String image = "";
        private boolean hasCancelButton = false;

        public Builder(CinemaTicketDispenser dispenser, ArrayList optionList){
            this.dispenser = dispenser;
            this.optionList = optionList;
        }
        public Builder title(String title){
            this.title = title;
            return this;
        }
        public Builder description(String description){
            this.description = description;
            return this;
        }
        public Builder image(String image){
            this.image = image;
            return this;
        }
        public Builder addCancelButton(){
            this.hasCancelButton = true;
            return this;
        }
        public SelectorMenu build(){
            return new SelectorMenu(this);
        }
    }

    public Object getPick() {
        boolean tryAgain = false;
        Object pick;
        do {
            char dispenserReturn = dispenser.waitEvent(30);

            if (dispenserReturn != '1') {
                pick = getPickedListObject(dispenserReturn);

            }else {
                pick = null;
                if (CreditCardManager.returnUnwantedCard(dispenser)) {
                    tryAgain = true;
                    display();
                }
            }
        } while (tryAgain);

        return pick;
    }

    public void display(){
        dispenser.setMenuMode();
        setTextAndImage();
        setOptions();
    }

    private void setTextAndImage(){
        dispenser.setTitle(title);
        dispenser.setDescription(description);
        dispenser.setImage(image);
    }
    private void setOptions(){
        for (int i = 0; i < optionList.size(); i++){
            dispenser.setOption(i, optionList.get(i).toString());
        }
        int nextIndex = optionList.size();
        if(hasCancelButton){
            dispenser.setOption(optionList.size(), "CANCELAR");//todo translate
            nextIndex++;
        }
        for (int i = nextIndex; i <= 5; i++){
            dispenser.setOption(i, null);
        }
    }
    private Object getPickedListObject(char dispenserReturn){

        if (dispenserReturn == 0)
            return null;
        else if(listElementWasPicked(dispenserReturn))
            return (optionList.get(dispenserReturnToListIndex(dispenserReturn)));
        else return null;

    }
    private boolean listElementWasPicked(char dispenserReturn){
        return dispenserReturnToListIndex(dispenserReturn) < optionList.size();
    }
    private int dispenserReturnToListIndex(char dispenserReturn){
        return dispenserReturn - 'A';
    }

}
