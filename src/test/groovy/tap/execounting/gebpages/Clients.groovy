package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:01 PM
 */
class Clients extends Page {
    static url = "http://localhost:8080/bureau/clients"
    static at = { title.equals "Клиенты" }
    static content = {
        nav { module NavigationModule }
    }
}
