package myApp.util;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * User: eluibon
 * Date: 21/08/12
 * Time: 10.30
 * Listener utilizzato per inizializzare la funzionalita' di risoluzione dei messaggi fornita da Omnifaces
 * ref : http://showcase-omnifaces.rhcloud.com/showcase/utils/Messages.xhtml
 * in pratica utilizza il resources bundle bundles.myApp (dichiarato anche nel faces-confix.xml)
 * per risolvere i messaggi tramite una message key.
 * se ad una funzione tipo Messages.addGlobalWarn(<string>) viene passato un valore che matcha una key nel bundle
 * viene risolta la stringa nel bundle stesso come testo del messaggio, altrimenti si usa la stringa passato come parametro come testo del messaggio
 */
@WebListener
public class CustomServletConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Messages.setResolver(new Messages.Resolver() {
            private static final String BASE_NAME = "bundles.myApp";

            public String getMessage(String message, Object... params) {
                ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, Faces.getLocale());
                if (bundle.containsKey(message)) {
                    message = bundle.getString(message);
                }
                return MessageFormat.format(message, params);
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Do stuff during server shutdown.
    }

}
