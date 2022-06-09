package cinema;

import java.util.Locale;

/**
 * <p>This class's purpose is to adapt <code>Locale</code> to the <code>displayOptionButtons</code> method of
 * the <code>MenuModeSelector</code> class. In said method, the first <code>for</code> loop utilizes the
 * <code>toString()</code> method of <code>Object</code>, and <code>Locale</code>'s implementation of this method
 * is not suitable in this context.</p>
 *
 * <p>For example, if the <code>toString()</code> method of a <code>Locale</code> object constructed as:
 * <code>new Locale("ca","ES)</code> were called, it would return the <code>String</code> <code>"ca_ES"</code>,
 * instead of <code>"Catalán"</code></p>
 *
 * <p>When an <code>ArrayList&lt;LocaleAdapter&gt;</code> is passed to the <code>setOptionList(ArrayList optionList)</code>
 * method of <code>MenuModeSelector.Builder</code>, instead of an <code>ArrayList&lt;Locale&gt;</code>, then when
 * <code>displayOptionButtons()</code> is called, this class's <code>toString()</code> method is called, instead of
 * the <code>Locale</code>'s, which is not suited for display.</p>
 *
 * @param locale adaptee
 */
record LocaleAdapter(Locale locale) {

    @Override
    public String toString() {

        String language = locale.getDisplayLanguage(locale);
        return language.substring(0, 1).toUpperCase() + language.substring(1);
    }
}
