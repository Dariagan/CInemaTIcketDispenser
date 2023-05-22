package cinema;

import cinema.menu_building.MenuModeSelector;
import sienens.CinemaTicketDispenser;

import java.util.*;

import static java.util.Objects.isNull;

public final class MainMenu extends Operation{
    private final ArrayList<Operation> operationList = new ArrayList<>();
    private MenuModeSelector menuSelector;

    public MainMenu(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
        LanguageSelection language = new LanguageSelection(dispenser, multi);
        MovieTicketSale sale = new MovieTicketSale(dispenser, multi);
        operationList.add(language);
        operationList.add(sale);
    }

    /**
     * <p>Handles the option selection for the main menu and executes the operation the customer picked in the main menu.</p>
     * <p>Displays the menu when <code>menuSelector.getPick()</code> is executed internally.</p>
     * <p>If the picked operation returns a false (meaning a timeout or a cancel button being pressed), the language is
     * restored to the default: Spanish.</p>
     * @return nothing because it loops indefinitely
     */
    @Override
    public boolean doOperation() {

        Operation pickedOperation;
        boolean pickedAnOption;
        while(true) {
            createMenu();
            pickedOperation = (Operation) menuSelector.getPick();
            pickedAnOption = !isNull(pickedOperation);
            if(pickedAnOption && !pickedOperation.doOperation())
                    getMultiplex().setLanguage(new Locale("es","ES"));
        } 
    }

    @Override
    public String toString() {
        return getMultiplex().getLanguage().getString("selectOption");
    }

    /**
     * Internally builds the main menu utilizing a builder from the <code>MenuModeSelector</code> class, and
     * sets this class's attribute <code>menuSelector</code> to the built result.
     */
    public void createMenu(){
        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(operationList);
        builder.setTitle(this.toString());
        this.menuSelector = builder.build();
    }
}
