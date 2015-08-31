package org.vaadin.artur.simplechat;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Push
@Theme("SimpleChat")
public class SimpleChatUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = SimpleChatUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        createUI();
        setLocale(Locale.ENGLISH);
    }

    private void createUI() {
        setContent(new SimpleChat());
    }

}