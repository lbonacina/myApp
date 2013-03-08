package myApp.util;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: eluibon
 * Date: 06/08/12
 * Time: 10.47
 * Disable caching for JSF pages, pages are dynamic so caching has no sense
 */
@WebFilter(servletNames = {"Faces Servlet"})
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        if (!httpReq.getRequestURI().startsWith(httpReq.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
            httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            httpRes.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            httpRes.setDateHeader("Expires", 0); // Proxies.
        }

        chain.doFilter(request, response);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // NOP
    }

    @Override
    public void destroy() {
        // NOP
    }
}