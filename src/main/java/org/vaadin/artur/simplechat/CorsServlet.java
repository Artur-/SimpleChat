package org.vaadin.artur.simplechat;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true, initParams = {
        @WebInitParam(name = "pushURL", value = "//artur.app.fi/simplechat-v14/") })
public class CorsServlet extends VaadinServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (request.getRequestURI().startsWith("/web-component/")) {

            if (origin != null && isAllowedRequestOrigin(origin)) {
                // Handle a preflight "option" requests
                if ("options".equalsIgnoreCase(request.getMethod())) {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");

                    // allow the requested method
                    String method = request.getHeader("Access-Control-Request-Method");
                    response.addHeader("Access-Control-Allow-Methods", method);

                    // allow the requested headers
                    String headers = request.getHeader("Access-Control-Request-Headers");
                    response.addHeader("Access-Control-Allow-Headers", headers);

                    response.addHeader("Access-Control-Allow-Credentials", "true");
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().flush();
                    return;
                } // Handle UIDL post requests
                else {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.addHeader("Access-Control-Allow-Credentials", "true");
                    super.service(request, response);
                    return;
                }
            }
        }
        super.service(request, response);

    }

    private boolean isAllowedRequestOrigin(String origin) {
        // TODO: Remember to limit the origins.
        return origin.matches(".*");
    }

}