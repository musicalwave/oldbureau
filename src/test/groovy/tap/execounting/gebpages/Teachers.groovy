package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:00 PM
 */
class Teachers extends Page {
    static url = 'http://localhost:8080/bureau/teachers'
    static at = {
        assert title .equals('Учителя')
        !$('body').text().contains('Exception')
    }
    static content = {
        nav { module NavigationModule }
        teacherLinks { $('.entry ul li a') }
        teacherLink { idx -> teacherLinks[idx] }
    }
}
