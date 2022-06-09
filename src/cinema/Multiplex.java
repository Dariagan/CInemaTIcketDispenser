package cinema;

import sienens.CinemaTicketDispenser;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Multiplex {

    private static final String RESOURCE_BUNDLE = "cinema.resource.resource";
    private ResourceBundle language = ResourceBundle.getBundle(RESOURCE_BUNDLE, new Locale("es","ES"));
    private final CinemaTicketDispenser dispenser = new CinemaTicketDispenser();
    private final CardReturner cardReturner = new CardReturner(dispenser, this);

    public CardReturner getCreditCardManager() {
        return cardReturner;
    }

    public ResourceBundle getLanguage() {
        return language;
    }

    public Multiplex() {}

    public void start(){

        MainMenu menu = new MainMenu(dispenser, this);

        menu.doOperation();
    }

    public void setLanguage(Locale locale){
        language = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
    }
}
