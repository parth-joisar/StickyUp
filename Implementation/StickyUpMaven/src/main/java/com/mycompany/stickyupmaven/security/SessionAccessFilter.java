package com.mycompany.stickyupmaven.security;

import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class SessionAccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        HttpSession session = httpRequest.getSession(false);
        Object sessionUser = session == null ? null : session.getAttribute("user");
        if (!(sessionUser instanceof User)) {
            String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
            if ("/workspace.html".equals(path) && "GET".equalsIgnoreCase(httpRequest.getMethod())) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html?expired=true");
            } else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
