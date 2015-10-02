package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:08 PM
 */
class Settings extends Page {
    static url = "http://localhost:8080/bureau/settings"
    static at = {
        title.equals 'Настройки'
        !$('body').text().contains('exception')
    }
    static content = {
        nav { module NavigationModule }
    }
}
