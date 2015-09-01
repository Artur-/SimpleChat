package org.vaadin.artur.simplechat;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vaadin.artur.simplechat.auth.LoginWindow;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Push
@Theme("SimpleChat")
@PreserveOnRefresh
public class SimpleChatUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = SimpleChatUI.class)
    public static class Servlet extends VaadinServlet {
        @Override
        protected void service(HttpServletRequest request,
                HttpServletResponse response) throws ServletException,
                IOException {
            // System.out.println("Request: " + request.getRequestURI());
            super.service(request, response);
        }
    }

    private LoginWindow loginWindow;
    private SimpleChat chat;

    @Override
    protected void init(VaadinRequest request) {
        loginWindow = new LoginWindow();
        chat = new SimpleChat();
        setContent(chat);
        addWindow(loginWindow);
        setLocale(Locale.ENGLISH);
    }

    public void login(String name, String imageUrl) {
        chat.login(name, imageUrl);
        loginWindow.close();
    }
}