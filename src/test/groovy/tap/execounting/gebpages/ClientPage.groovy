package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:01 PM
 */
class ClientPage extends Page {
    static url = "http://localhost:8080/bureau/clientpage/2"
    static at = {
        title.contains "Клиенты : "
        !$('body').text().contains('exception')
    }
    static content = {
        nav { module NavigationModule }
    }
}
