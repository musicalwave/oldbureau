package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:03 PM
 */
class Schedules extends Page {
    static url = "http://localhost:8080/bureau/schedules"
    static at = { title.equals "Расписания" }
    static content = {
        nav { module NavigationModule }
    }
}
