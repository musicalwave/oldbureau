package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:03 PM
 */
class CRUD extends Page {
    static url = "http://localhost:8080/bureau/crud"
    static at = { title.equals "Работа с БД" }
    static content = {
        nav { module NavigationModule }
    }
}
