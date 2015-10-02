package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:02 PM
 */
class Statistics extends Page {
    static url = 'http://localhost:8080/bureau/statistics'
    static at = {
        assert title.equals('Занятия')
        !$('body').text().contains('exception')
    }
    static content = {
        nav { module NavigationModule }
        filterForm { $('#FilterForm') }
        dateField1 { filterForm.find('#datefield') }
        dateField2 { filterForm.find('#datefield_0')}
        submit { filterForm.find('#submit_0') }
    }
}
