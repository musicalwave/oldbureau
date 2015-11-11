package tap.execounting.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.annotations.AnonymousAccess;
import tap.execounting.services.Authenticator;


@AnonymousAccess
public class Index
{
    @Inject
    private Authenticator authenticator;

    public Object onActivate()
    {
        return authenticator.isLoggedIn() ? Home.class : Signin.class;
    }
}
