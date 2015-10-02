package tap.execounting.security;

import java.io.IOException;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Response;

import tap.execounting.annotations.Access;
import tap.execounting.annotations.AnonymousAccess;
import tap.execounting.pages.Home;
import tap.execounting.pages.Signin;
import tap.execounting.services.Authenticator;


/**
 * Intercepts the current page to redirect through the requested page or to the authentication page
 * if login is required. For more understanding read the following tutorial <a
 * href="http://tapestryjava.blogspot.com/2009/12/securing-tapestry-pages-with.html"> Securing
 * Tapestry Pages with annotations </a>
 *
 * Checks for user access level, to permit or restrict access to certain pages.
 * 
 * @author karesti
 * @version 2.0
 */
public class AuthenticationFilter implements ComponentRequestFilter
{

    private final PageRenderLinkSource renderLinkSource;

    private final ComponentSource componentSource;

    private final Response response;

    private final Authenticator authenticator;

    private String defaultPage = Home.class.getSimpleName();

    private String signinPage = Signin.class.getSimpleName();

    public AuthenticationFilter(PageRenderLinkSource renderLinkSource,
            ComponentSource componentSource, Response response, Authenticator authenticator)
    {
        this.renderLinkSource = renderLinkSource;
        this.componentSource = componentSource;
        this.response = response;
        this.authenticator = authenticator;
    }

    public void handleComponentEvent(ComponentEventRequestParameters parameters,
            ComponentRequestHandler handler) throws IOException
    {

        if (dispatchedToLoginPage(parameters.getActivePageName())) { return; }

        handler.handleComponentEvent(parameters);

    }

    public void handlePageRender(PageRenderRequestParameters parameters,
            ComponentRequestHandler handler) throws IOException
    {

        if (dispatchedToLoginPage(parameters.getLogicalPageName())) { return; }

        handler.handlePageRender(parameters);
    }

    private boolean dispatchedToLoginPage(String pageName) throws IOException
    {
        Component page = componentSource.getPage(pageName);
        Class pageClass = page.getClass();

        if (authenticator.isLoggedIn())
        {
            // Logged user should not go back to Signin or Signup
            boolean weShouldRedirectToDefaultPage = signinPage.equalsIgnoreCase(pageName);

            // We should check for security annotations on pageClass
            if (!weShouldRedirectToDefaultPage
                    && pageClass.isAnnotationPresent(Access.class)) {
                Access access = (Access) pageClass.getAnnotation(Access.class);
                AccessLevel userAccessLevel = authenticator.getLoggedUser().getAccessLevel();
                weShouldRedirectToDefaultPage |= access.level() != userAccessLevel;
            }

            if (weShouldRedirectToDefaultPage) {
                Link link = renderLinkSource.createPageRenderLink(defaultPage);
                response.sendRedirect(link);
                return true;
            }

            return false;
        }


        if (pageClass.isAnnotationPresent(AnonymousAccess.class)) { return false; }

        Link link = renderLinkSource.createPageRenderLink("Signin");

        response.sendRedirect(link);

        return true;
    }
}
