package org.vaadin.artur.simplechat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.ServletHelper.RequestType;

@WebServlet(value = "/*", asyncSupported = true)
public class CorsServlet extends VaadinServlet {
    private Set<String> allowedOrigins = new HashSet<>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // "*" allows ALL WEB SITES to embed components from this application
        allowedOrigins.add("*");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (needsCorsHeaders(request) && isAllowedRequestOrigin(origin)) {
            System.out.println("CORS request from " + origin + " for " + request.getPathInfo());
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");

            // Handle a preflight "option" requests
            if ("options".equalsIgnoreCase(request.getMethod())) {
                response.addHeader("Access-Control-Allow-Methods", "GET, POST");
                response.addHeader("Access-Control-Allow-Headers", "content-type");

                response.setContentType("text/plain");
                response.setCharacterEncoding("utf-8");
                response.getWriter().flush();
                return;
            } else {
                response.addHeader("Vary", "Origin");
            }

        }
        super.service(request, response);
    }

    private boolean needsCorsHeaders(HttpServletRequest request) {
        VaadinServletRequest vaadinRequest = new VaadinServletRequest(request, getService());
        String path = vaadinRequest.getPathInfo();
        if (ServletHelper.isRequestType(vaadinRequest, RequestType.UIDL)) {
            return true;
        } else if (path.startsWith("/build/")) {
            return true;
        }
        return false;
    }

    private boolean isAllowedRequestOrigin(String origin) {
        System.out.println("Checking if origin is ok: " + origin);
        if (origin == null)
            return false;
        if (allowedOrigins.contains("*")) {
            return true;
        }
        return allowedOrigins.contains(origin);
    }

}