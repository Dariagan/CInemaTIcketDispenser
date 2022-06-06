package cinema;

import sienens.CinemaTicketDispenser;

import java.util.*;

import static java.util.Objects.isNull;

public final class MainMenu extends Operation{
    private final ArrayList<Operation> operationList = new ArrayList<>();
    private LanguageSelection language;
    private MovieTicketSale sale;
    private SelectorMenu selectorMenu;

    public MainMenu(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
        this.language = new LanguageSelection(dispenser, multi);
        this.sale = new MovieTicketSale(dispenser, multi);
        operationList.add(language);
        operationList.add(sale);
    }

    @Override
    public boolean doOperation() {

        Operation pickedOperation;
        boolean pickedAnOption;
        do {
            presentMenu();
            pickedOperation = (Operation) selectorMenu.getPick();
            pickedAnOption = !isNull(pickedOperation);
            if(pickedAnOption)
                pickedOperation.doOperation();
        } while(true);
    }

    @Override
    public String toString() {
        return "elija una opción";
    }

    public void presentMenu(){
        SelectorMenu.Builder sBuilder = new SelectorMenu.Builder(getDispenser(), operationList);
        sBuilder.title(this.toString()).description("nose");
        this.selectorMenu = sBuilder.build();
        selectorMenu.display();
    }

}
