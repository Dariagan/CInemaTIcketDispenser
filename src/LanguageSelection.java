import sienens.CinemaTicketDispenser;

public final class LanguageSelection extends Operation{

    public LanguageSelection(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
    }

    @Override
    public boolean doOperation() {

        return true;
    }

    public String getTitle(){//título para la pantalla.
        return "cambiar idioma";
    }


}
