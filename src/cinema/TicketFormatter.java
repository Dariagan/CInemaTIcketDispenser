package cinema;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public final class TicketFormatter {//static

    public static String getFormattedPurchase(Movie movie, ArrayList<Seat> seats, ResourceBundle language){

        String plural;

        if (seats.size()>1)
            plural = "s";
        else
            plural = "";

        Object[] messageArguments ={
                seats.size(),
                movie,
                plural
        };
        MessageFormat purchases = new MessageFormat(language.getString("purchase"));
        purchases.setLocale(language.getLocale());

        return purchases.format(messageArguments);
    }

    public static String getFormattedPricing(int price, ResourceBundle language){
        return String.format("%s: %d€", language.getString("price"), price);
    }

    public static String getFormattedDate(LocalDate date, ResourceBundle language){

        Locale locale = language.getLocale();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);

        String locallyFormattedDate = date.format(dateFormatter);

        return String.format("%s: %s", language.getString("date"), locallyFormattedDate);
    }

    public static String getFormattedDuration(int duration, ResourceBundle language){

        return String.format("%s: %s %s",
                language.getString("duration"), duration, language.getString("minutes"));
    }

    public static String getFormattedSession(Session session, ResourceBundle language){

        return String.format("%s: %s", language.getString("session"), session);
    }

    public static String getFormattedSeating(Seat seat, ResourceBundle language){

        return String.format("%s:\n  %s %d\n  %s %d\n",
                language.getString("seat"),
                language.getString("row"), seat.row(),
                language.getString("column"), seat.col());
    }

    public static String getFormattedTicketNumber(int i, int seats, ResourceBundle language){
        Object[] numbers ={
                i,
                seats
        };
        MessageFormat ticketNumberFormatter = new MessageFormat(language.getString("ticketNumber"));
        ticketNumberFormatter.setLocale(language.getLocale());
        return  ticketNumberFormatter.format(numbers);
    }

    public static String getRandomBarcode(){
        StringBuilder builder = new StringBuilder("\n");
        Random random = new Random();

        for (float i = 0; i < 22; i++) {
            int pick = random.nextInt(3);
            switch (pick) {
                case 0-> {builder.append("❘"); i-=0.9;}
                case 1-> builder.append("❙");
                case 2-> {builder.append("❚"); i+=0.3;}
            }
        }
        return builder.toString();
    }
}
