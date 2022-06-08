package cinema;

import cinema.menu_building.MenuModeSelector;
import sienens.CinemaTicketDispenser;

import java.util.ArrayList;
import java.util.Locale;

import static java.util.Objects.isNull;

public final class LanguageSelection extends Operation{

    private final ArrayList<Locale> locales = new ArrayList<>();

    public LanguageSelection(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
        Locale esLocale = new Locale("es", "ES");
        locales.add(esLocale);
        Locale enLocale = new Locale("en");
        locales.add(enLocale);
        Locale caLocale = new Locale("ca", "ES");
        locales.add(caLocale);
        Locale euLocale = new Locale("eu", "ES");
        locales.add(euLocale);
    }

    @Override
    public boolean doOperation() {

        MenuModeSelector.Builder builder = new MenuModeSelector.Builder(getDispenser(), getMultiplex());
        builder.setOptionList(locales);
        builder.setTitle(this.toString()).setCancelButton();

        MenuModeSelector sMenu = builder.build();

        Locale pickedLocale = (Locale)sMenu.getPick();
        if(!isNull(pickedLocale)){
            getMultiplex().setLanguage(pickedLocale);
            return true;
        }
        else return false;
    }

    public String toString(){
        return getMultiplex().getLanguage().getString("selectLanguage");
    }
}
