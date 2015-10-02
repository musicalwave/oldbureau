package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:25 PM
 */
class Home extends Page {
    static url = "http://localhost:8080/bureau/home"
    static at = {
        title == "Бюро"
        !$('body').text().contains('exception')
    }
    static content = {
        btnTeachers (to: Teachers) { btn('Учителя') }
        btnReports (to: Reports) { btn('Отчеты') }
        btnClients (to: Clients) { btn('Клиенты') }
        btnStatistics (to: Statistics) { btn('Занятия') }
        btnSchedules (to: Schedules) { btn('Расписания') }
        btnCRUD (to: CRUD) { btn('База данных') }
        btnSettings (to: Settings) { btn('Настройки') }
        nav { module NavigationModule }
    }

    def btn(string){
        $('a.homelink', text: contains(string))
    }
}
