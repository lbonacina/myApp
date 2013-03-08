package myApp.security;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * allow for Shiro session expiration to work on a Ajax POST
 * see : http://balusc.blogspot.it/2013/01/apache-shiro-is-it-ready-for-java-ee-6.html
 */
public class FacesAjaxAwareUserFilter extends PassThruAuthenticationFilter {

    private static final String FACES_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

    @Override
    protected void redirectToLogin(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        /*
            PROBLEM : this function is called in at least two scenarios :
                - user tries to access a page directly without logging in and is redirected to the login page
                - session has expired, user need to login again

            problem is in the second case i want a "Session Expired" message to popup on login page, while in the first
            case i don't want it
            triggering the message is easy, we use a url parameter, but to discriminate the two scenarios the best solution
            i've found i the line below
            the theory is that on session expiration Shiro detects the condition, create a new session and use redirectToLogin()
            to forward the user to the login page, so the session.isNew will be true
            quite a hack, maybe do not cover even all scenarios... i'll ask on the forum
        */
        boolean sessionExpired = ((HttpServletRequest) req).getSession().isNew() ;
        String urlParam = (sessionExpired) ? "?reason=expired" : "" ;

        if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
            res.setContentType("text/xml");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().printf(FACES_REDIRECT_XML, request.getContextPath() + getLoginUrl() + urlParam);
        }
        else {
            //super.redirectToLogin(req,res);
            String loginUrl = getLoginUrl();
            WebUtils.issueRedirect(req, res, loginUrl + urlParam);
        }
    }

}