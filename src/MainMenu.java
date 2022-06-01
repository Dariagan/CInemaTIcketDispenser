import sienens.CinemaTicketDispenser;

import java.util.*;

public class MainMenu extends Operation{
    private List<Operation> operationList = new ArrayList<Operation>();
    LanguageSelection language;
    MovieTicketSale sale;

    public MainMenu(CinemaTicketDispenser dispenser, Multiplex multi) {
        super(dispenser, multi);
        this.language = new LanguageSelection(dispenser, multi);
        this.sale = new MovieTicketSale(dispenser, multi);
    }

    @Override
    public void doOperation() {
        presentMenu();
    }

    @Override
    public String getTitle() {
        return null;
    }

    public void presentMenu(){
        String optionA = language.getTitle();
        getDispenser().setOption(0, optionA);

        String optionB = sale.getTitle();
        getDispenser().setOption(1, optionB);

        for (int i = 2; i <= 5; i++){
            getDispenser().setOption(i, null);
        }

        switch(getDispenser().waitEvent(30)){

            case 'A'->{
                language.doOperation();
            }
            case 'B'->{
                sale.doOperation();
            }
        }
    }

}
