package tap.execounting.pages;

import tap.execounting.annotations.AnonymousAccess;
import tap.execounting.services.Authenticator;

import org.apache.tapestry5.ioc.annotations.Inject;


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
