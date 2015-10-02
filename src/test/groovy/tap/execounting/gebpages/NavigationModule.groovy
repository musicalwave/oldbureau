package tap.execounting.gebpages

import geb.Module

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 10:51 PM
 */
class NavigationModule extends Module {
    static content = {
        navHome(to: Home) { btn('home') }
        navTeachers(to: Teachers) { btn('teachers') }
        navClients(to: Clients) { btn('clients') }
        navReports(to: Reports) { btn('reports') }
        navStatistics(to: Statistics) { btn('statistics') }
        navSchedules(to: Schedules) { btn('schedules') }
        navCRUD(to: CRUD) { btn('crud') }
        navSettings(to: Settings) { btn('settings') }
        navSignout(to: Signin) { btn('home.layout.logout') }
    }

    def btn(href){
        $("ul#mainmenu a", href: contains(href))
    }
}
