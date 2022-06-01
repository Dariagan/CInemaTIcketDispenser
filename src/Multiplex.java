import sienens.CinemaTicketDispenser;

public class Multiplex {
    private String language;

    public Multiplex(String language) {
        this.language = language;
    }

    public void start(){
        CinemaTicketDispenser dispenser = new CinemaTicketDispenser();
        MainMenu menu = new MainMenu(dispenser, this);

        while(true)
            menu.doOperation();
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language=language;
    }

}
