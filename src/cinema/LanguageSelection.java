package cinema;

import cinema.menu_building.MenuModeSelector;
import sienens.CinemaTicketDispenser;

import java.util.ArrayList;
import java.util.Locale;

import static java.util.Objects.isNull;

public final class LanguageSelection extends Operation{

    private final ArrayList<LocaleAdapter> adaptedLocales = new ArrayList<>();

    /**
     * <p>Constructs itself calling the constructor from its parent class <code>Operation</code></p>
     * <p>Creates and adapts all the wanted locales via the call of another method.</p>
     */
    public LanguageSelection(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);

        createLocale("es", "ES");
        createLocale("en");
        createLocale("ca", "ES");
        createLocale("eu", "ES");
    }

    private void createLocale(String language){
        Locale locale = new Locale(language);
        adaptedLocales.add(new LocaleAdapter(locale));
    }
    private void createLocale(String language, String country){
        Locale locale = new Locale(language, country);
        adaptedLocales.add(new LocaleAdapter(locale));
    }

    /**
     * <p>Creates a menu calling the <code>buildLanguageSelectionMenu</code> method.</p>
     * <p>Displays it and gets customer's response by executing <code>menu.getPick().</code></p>
     *
     * @return <p><code>true</code> if a language is selected.</p>
     * <p><code>false</code> if the there's a timeout or the cancel button is pressed by the customer.</p>
     */
    @Override
    public boolean doOperation() {

        MenuModeSelector menu = buildLanguageSelectionMenu();

        LocaleAdapter pickedLocaleAdapter = (LocaleAdapter)menu.getPick();
        if(!isNull(pickedLocaleAdapter)){
            getMultiplex().setLanguage(pickedLocaleAdapter.locale());
            return true;
        }
        else return false;
    }

    private MenuModeSelector buildLanguageSelectionMenu(){

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(adaptedLocales);
        builder.setTitle(this.toString()).setCancelButton();
        return builder.build();
    }


    public String toString(){
        return getMultiplex().getLanguage().getString("selectLanguage");
    }
}
