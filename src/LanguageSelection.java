import sienens.CinemaTicketDispenser;

public final class LanguageSelection extends Operation{

    public LanguageSelection(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
    }

    public enum Language{
        SPANISH, ENGLISH, CATALAN, BASQUE
    }

    @Override
    public boolean doOperation() {

        getMultiplex().setLanguage(null);//TODO
        return true;
    }

    public String toString(){
        return "cambiar idioma";
    }
}
