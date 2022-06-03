import sienens.CinemaTicketDispenser;

import java.util.List;

public class DispenserMenu {

    public static void configureMenu(
            CinemaTicketDispenser dispenser, List list, String title, String description, String imagePath
    ){
        configureMenu(dispenser, list, title, description);
        dispenser.setImage(imagePath);
    }
    public static void configureMenu(CinemaTicketDispenser dispenser, List list, String title, String description){
        configureMenu(dispenser, list, title, true);
        dispenser.setDescription(description);

    }
    public static void configureMenu(CinemaTicketDispenser dispenser, List list, String title, boolean hasCancelOption){
        dispenser.setMenuMode();
        dispenser.setTitle(title);
        dispenser.setDescription("");
        dispenser.setImage("");

        for (int i = 0; i < list.size(); i++){
            dispenser.setOption(i, list.get(i).toString());
        }
        int nextIndex = list.size();
        if(hasCancelOption){
            dispenser.setOption(list.size(), "CANCELAR");
             nextIndex++;
        }

        for (int i = nextIndex; i <= 5; i++){
            dispenser.setOption(i, null);
        }
    }

    public static Object getPickedObject(CinemaTicketDispenser dispenser, List list){

        char dispenserReturn = dispenser.waitEvent(30);

        if (dispenserReturn == 0)
            return null;
        else if (dispenserReturn == '1') {
            if (CreditCardManager.rejectCreditCard(dispenser))
                return getPickedObject(dispenser, list);
            else return null;
        }else if(listElementWasPicked(dispenserReturn, list))
            return (list.get(dispenserReturnToListIndex(dispenserReturn)));
        else return null;

    }
    private static boolean listElementWasPicked(char dispenserReturn, List list){
        return dispenserReturnToListIndex(dispenserReturn) < list.size();
    }
    private static int dispenserReturnToListIndex(char dispenserReturn){
        return dispenserReturn - 'A';
    }
}
