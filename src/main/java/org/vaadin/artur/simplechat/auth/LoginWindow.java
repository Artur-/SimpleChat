package org.vaadin.artur.simplechat.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.vaadin.artur.simplechat.SimpleChatUI;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import elemental.json.Json;
import elemental.json.JsonObject;

public class LoginWindow extends Window implements RequestHandler {

    private OAuthService service;
    private Token token = new Token("login", UUID.randomUUID().toString());

    public LoginWindow() {

        setModal(true);
        setClosable(false);
        setResizable(false);

        VaadinSession.getCurrent().addRequestHandler(this);
        service = createService();

        Link link = new Link("Login with Google", new ExternalResource(
                service.getAuthorizationUrl(token)));
        link.addStyleName(ValoTheme.LINK_LARGE);

        VerticalLayout layout = new VerticalLayout(link);
        layout.setMargin(true);

        setContent(layout);
    }

    private OAuthService createService() {
        Properties p = new Properties();
        try (InputStream is = getClass().getResourceAsStream(
                "google-auth.properties")) {
            p.load(is);
        } catch (IOException e) {
            return null;
        }

        ServiceBuilder sb = new ServiceBuilder();
        sb.provider(Google2Api.class);
        sb.apiKey(p.getProperty("apiKey"));
        sb.apiSecret(p.getProperty("apiSecret"));
        sb.scope("email");

        URI loc = Page.getCurrent().getLocation();
        String callbackUrl = loc.getScheme() + "://" + loc.getHost();
        if (loc.getPort() != -1) {
            callbackUrl += ":" + loc.getPort();
        }
        callbackUrl += loc.getPath();
        callbackUrl += "login";
        sb.callback(callbackUrl);

        return sb.build();
    }

    @Override
    public SimpleChatUI getUI() {
        return (SimpleChatUI) super.getUI();
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (request.getPathInfo() != null
                && request.getPathInfo().equals("/login")
                && request.getParameter("code") != null) {

            // Auth response
            String code = request.getParameter("code");
            Verifier v = new Verifier(code);
            Token t = service.getAccessToken(token, v);

            OAuthRequest r = new OAuthRequest(Verb.GET,
                    "https://www.googleapis.com/oauth2/v1/userinfo");
            service.signRequest(t, r);
            Response resp = r.send();
            JsonObject responseJson = Json.parse(resp.getBody());
            getUI().access(
                    () -> {
                        getUI().login(responseJson.getString("name"),
                                responseJson.getString("picture"));
                    });

            session.removeRequestHandler(this);

            ((VaadinServletResponse) response).getHttpServletResponse()
            .sendRedirect(request.getContextPath());
            return true;
        }
        return false;
    }
}
