package cinema.menu_building;

import cinema.Multiplex;
import sienens.CinemaTicketDispenser;

import java.util.ArrayList;
import java.util.Locale;

public final class MenuModeSelector extends AbstractSelector {

    private final ArrayList optionList;
    private final String IMAGE;
    private final static int DISPENSER_OPTION_LIMIT = 6;

    private MenuModeSelector(Builder builder){
        super(builder);
        this.optionList = builder.optionList;
        this.IMAGE = builder.image;
    }

    public final static class Builder extends AbstractSelector.Builder{
        private ArrayList optionList;
        private String image = null;

        public Builder(CinemaTicketDispenser dispenser, Multiplex multiplex){
            super(dispenser, multiplex);
        }
        public Builder setOptionList(ArrayList optionList){
            this.optionList = optionList;
            return this;
        }
        public Builder setImage(String image){
            this.image = image;
            return this;
        }
        private void validate(){
            int cancelButtonSize = super.hasCancelButton() ? 1 : 0;
            if (this.optionList.size() > DISPENSER_OPTION_LIMIT - cancelButtonSize)
                throw new RuntimeException("Dispenser's maximum number of options surpassed");
        }
        public MenuModeSelector build(){
            validate();
            return new MenuModeSelector(this);
        }
        @Override
        public Builder reset(){
            super.reset();
            optionList = null;
            image = null;
            return this;
        }
    }

    @Override
    void changeDispenserMode() {
        getDispenser().setMenuMode();
    }

    @Override
    void setExtra() {
        getDispenser().setImage(IMAGE);
    }

    @Override
    void displayOptionButtons(){

        for (int i = 0; i < optionList.size(); i++)
            getDispenser().setOption(i, optionList.get(i).toString());

        int nextIndex = optionList.size();
        if(super.hasCancelButton()){
            getDispenser().setOption(optionList.size(), getMultiplex().getLanguage().getString(getCancelButtonKey()));
            nextIndex++;
        }
        for (int i = nextIndex; i <= DISPENSER_OPTION_LIMIT - 1; i++){
            getDispenser().setOption(i, null);
        }
    }

    @Override
    Object getIterationPick(char dispenserReturn) {

        Object pick;
        if (dispenserReturn != '1') {
            pick = getPickedListObject(dispenserReturn);
            super.endLoop();
        }else {
            pick = null;
            super.dealWithUnwantedCard();
        }
        return pick;
    }

    private Object getPickedListObject(char dispenserReturn){

        if(listElementWasPicked(dispenserReturn))
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
