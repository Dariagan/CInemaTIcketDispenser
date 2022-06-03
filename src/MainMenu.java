import sienens.CinemaTicketDispenser;

import java.util.*;

public final class MainMenu extends Operation{
    private final List<Operation> operationList = new ArrayList<>();//TODO algo
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
        presentMenu();

        return true;
    }

    @Override
    public String toString() {
        return "elija una opción";
    }

    public void presentMenu(){

        DispenserMenu.configureMenu(getDispenser(), operationList, this.toString(), false);

        ((Operation)DispenserMenu.getPickedObject(getDispenser(), operationList)).doOperation();

    }

}
