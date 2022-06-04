import sienens.CinemaTicketDispenser;

import java.util.*;

import static java.util.Objects.isNull;

public final class MainMenu extends Operation{
    private final List<Operation> operationList = new ArrayList<>();
    LanguageSelection language;
    MovieTicketSale sale;

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
            pickedOperation = ((Operation) DispenserMenu.getPickedObject(getDispenser(), operationList));
            pickedAnOption = !isNull(pickedOperation);
            if(pickedAnOption)
                pickedOperation.doOperation();
        } while(pickedAnOption);
        return true;
    }

    @Override
    public String toString() {
        return "elija una opción";
    }

    public void presentMenu(){

        DispenserMenu.configureMenu(getDispenser(), operationList, this.toString(), false);
    }

}
