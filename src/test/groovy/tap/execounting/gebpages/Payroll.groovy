package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/23/13
 * Time: 12:30 PM
 */
class Payroll extends Page {
    static url = "http://localhost:8080/bureau/payroll/68/01.01.2013/31.03.2013/false"
    static at = {
        title.equals 'Ведомость'
        !$('body').text().contains('Exception')
    }
}
