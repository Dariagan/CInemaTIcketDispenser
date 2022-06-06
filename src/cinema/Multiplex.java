package cinema;

import sienens.CinemaTicketDispenser;

public final class Multiplex {
    private LanguageSelection.Language language;

    public Multiplex(LanguageSelection.Language language) {
        this.language = language;
    }

    public void start(){
        CinemaTicketDispenser dispenser = new CinemaTicketDispenser();
        MainMenu menu = new MainMenu(dispenser, this);

        while(true)
            menu.doOperation();
    }

    public LanguageSelection.Language getLanguage(){
        return language;
    }

    public void setLanguage(LanguageSelection.Language language){
        this.language = language;
    }

}
