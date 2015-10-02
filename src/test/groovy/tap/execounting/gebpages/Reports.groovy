package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:00 PM
 */
class Reports extends Page {
    static url = "http://localhost:8080/bureau/reports"
    static at = {
        title.equals "Отчеты"
        !$('body').text().contains('exception')
    }
    static content = {
        nav { module NavigationModule }
    }
}
