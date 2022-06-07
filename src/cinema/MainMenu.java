package cinema;

import cinema.menu_building.MenuModeSelector;
import sienens.CinemaTicketDispenser;

import java.util.*;

import static java.util.Objects.isNull;

public final class MainMenu extends Operation{
    private final ArrayList<Operation> operationList = new ArrayList<>();
    private LanguageSelection language;
    private MovieTicketSale sale;
    private MenuModeSelector menuSelector;

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
            pickedOperation = (Operation) menuSelector.getPick();
            pickedAnOption = !isNull(pickedOperation);
            if(pickedAnOption)
                if(!pickedOperation.doOperation()){
                    getMultiplex().setLanguage(new Locale("es","ES"));
                }
        } while(true);
    }

    @Override
    public String toString() {
        return getMultiplex().getLanguage().getString("selectOption");
    }

    public void presentMenu(){
        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(operationList);
        builder.setTitle(this.toString());
        this.menuSelector = builder.build();
        menuSelector.display();
    }
}
