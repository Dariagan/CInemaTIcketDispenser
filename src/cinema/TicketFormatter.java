package cinema;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public final class TicketFormatter {

    /**
     * @param movie movie from which to get the title
     * @param seats list of seats which were selected previously
     * @param language language to translate to
     * @return Formatted header for purchase
     */
    public static String getFormattedPurchase(Movie movie, ArrayList<Seat> seats, ResourceBundle language){

        final String PLURAL;

        if (seats.size()>1)
            PLURAL = "s";
        else
            PLURAL = "";

        Object[] messageArguments ={
                seats.size(),
                movie,
                PLURAL
        };
        MessageFormat purchases = new MessageFormat(language.getString("purchase"));
        purchases.setLocale(language.getLocale());

        return purchases.format(messageArguments);
    }

    public static String getFormattedPricing(int price, ResourceBundle language){
        return String.format("%s: %d€", language.getString("price"), price);
    }

    /**
     * @param language language to translate to
     * @return formatted and localized date
     */
    public static String getFormattedDate(LocalDate date, ResourceBundle language){

        Locale locale = language.getLocale();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);

        String locallyFormattedDate = date.format(dateFormatter);

        return String.format("%s: %s", language.getString("date"), locallyFormattedDate);
    }

    /**
     * @param duration duration in minutes
     * @param language language to translate to
     * @return formatted and localized duration
     */
    public static String getFormattedDuration(int duration, ResourceBundle language){

        return String.format("%s: %s %s",
                language.getString("duration"), duration, language.getString("minutes"));
    }

    /**
     * @param session selected session
     * @param language language to translate to
     * @return formatted session timing
     */
    public static String getFormattedSession(Session session, ResourceBundle language){

        return String.format("%s: %s", language.getString("session"), session);
    }

    /**
     * @param language language to translate to
     * @return formatted seat position to add to ticket
     */
    public static String getFormattedSeating(Seat seat, ResourceBundle language){

        return String.format("%s:\n  %s %d\n  %s %d\n",
                language.getString("seat"),
                language.getString("row"), seat.row(),
                language.getString("column"), seat.col());
    }

    /**
     * @param seatNumber number of the seat referred to, respective to the purchase
     * @param seats number of seats purchased
     * @param language language to translate to
     * @return formatted and localized ticket number respective to purchase
     */
    public static String getFormattedTicketNumber(int seatNumber, int seats, ResourceBundle language){
        Object[] numbers ={
                seatNumber,
                seats
        };
        MessageFormat ticketNumberFormatter = new MessageFormat(language.getString("ticketNumber"));
        ticketNumberFormatter.setLocale(language.getLocale());
        return  ticketNumberFormatter.format(numbers);
    }

    public static String getRandomBarcode(){
        final StringBuilder builder = new StringBuilder("\n");
        final Random random = new Random();

        for (float i = 0; i < 22; i++) {
            int pick = random.nextInt(3);
            switch (pick) {
                case 0-> {builder.append("❘"); i-=0.9;}
                case 1-> builder.append("❙");
                case 2-> {builder.append("❚"); i+=0.2;}
            }
        }
        return builder.toString();
    }
}
