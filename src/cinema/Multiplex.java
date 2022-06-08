package cinema;

import sienens.CinemaTicketDispenser;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Multiplex {

    private static final String RESOURCE_BUNDLE = "cinema.resource.resource";
    private ResourceBundle language = ResourceBundle.getBundle(RESOURCE_BUNDLE, new Locale("es","ES"));
    private final CinemaTicketDispenser dispenser = new CinemaTicketDispenser();
    private final CreditCardManager creditCardManager = new CreditCardManager(dispenser, this);

    public CreditCardManager getCreditCardManager() {
        return creditCardManager;
    }

    public ResourceBundle getLanguage() {
        return language;
    }

    public Multiplex() {}

    public void start(){

        MainMenu menu = new MainMenu(dispenser, this);

        while(true)
            menu.doOperation();
    }

    public void setLanguage(Locale locale){
        language = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
    }
}
