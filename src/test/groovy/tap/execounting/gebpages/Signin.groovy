package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:00 PM
 */
class Signin extends Page {
    static url = "http://localhost:8080/bureau/signin"
    static at = {
        title == "Вход"
        !$('body').text().contains('exception')
    }
    static content = {
        loginForm { $("form#loginForm") }
        loginButton(to: Home) { $('input', type: 'submit') }
        username { $('input#username') }
        password { $('input#password') }
    }
}
