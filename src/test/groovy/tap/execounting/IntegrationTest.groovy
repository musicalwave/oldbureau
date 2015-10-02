package tap.execounting

import geb.spock.GebSpec
import spock.lang.Ignore
import tap.execounting.gebpages.ClientPage
import tap.execounting.gebpages.Clients
import tap.execounting.gebpages.Home
import tap.execounting.gebpages.Payroll
import tap.execounting.gebpages.Reports
import tap.execounting.gebpages.Settings
import tap.execounting.gebpages.Signin
import tap.execounting.gebpages.Statistics
import tap.execounting.gebpages.TeacherPage
import tap.execounting.gebpages.Teachers

class IntegrationTest extends GebSpec{
    def uname = 'Ivan'
    def passwd = 'DraGmar91'
    def 'simple navigation using links'(){
        when: 'user goes to signin page'
        to Signin
        then: 'page loaded and everything is fine'
        at Signin

        when: 'user signs in'
        username << uname
        password << passwd
        loginButton.click()
        then: 'user is on the Home page'
        at Home

        when: 'user goes to teachers'
        to Teachers
        then: 'he is at teachers'
        at Teachers

        when: 'user goes to the page of specific teacher'
        to TeacherPage
        then: 'it loads'
        at TeacherPage

        when: 'user tries to access payroll'
        to Payroll
        then: 'it loads'
        at Payroll

        when: 'user loads reports'
        to Reports
        then: 'they"re loaded'
        at Reports

        when: 'user goes to the clients page'
        to Clients
        then: 'it is loaded'
        at Clients

        when: 'user goes to the page of specific client'
        to ClientPage
        then: 'it loads'
        at ClientPage

        when: 'user goes to statistics'
        to Statistics
        then: 'it loads'
        at Statistics

        when: 'user goes to settings'
        to Settings
        then: 'it loads'
        at Settings
    }

    def 'every page is accessible and navigation is ok'() {
        when: 'user goes to signin page'
        to Signin
        then: 'page loaded and everything is fine'
        at Signin

        when: 'user signs in'
        username << uname
        password << passwd
        loginButton.click()
        then: 'user is on the Home page'
        at Home

        when: 'user goes to the "Teachers" page via navigation bar button'
        nav.navTeachers.click()
        then: 'he is on "Teachers" page'
        at Teachers

        when: 'user goes to Clients page'
        nav.navClients.click()
        then: 'it opens like a boss'
        at Clients

        when: 'user clicks on reports button'
        nav.navReports.click()
        report "Reports at ${new Date().format('dd.MM.YY')}"
        then: 'he is on reports page'
        at Reports
    }

    def 'every teacher page is accessible'(){
        when: 'user goes to signin page'
        to Signin
        then: 'page loaded and everything is fine'
        at Signin

        when: 'user signs in'
        username << uname
        password << passwd
        loginButton.click()
        then: 'user is on the Home page'
        at Home

        when: 'user goes to the "Teachers" page via navigation bar button'
        nav.navTeachers.click()
        then: 'he is on "Teachers" page'
        at Teachers

        (teacherLinks.size()/5).times { i ->
            to Teachers
            when: 'user clicks on some link'
            teacherLinks[i*4].click()
            then: 'it loads'
            at TeacherPage

            when: 'user tries to access the payroll'
            payrollDateOne.clear()
            payrollDateOne << '01.01.2013'
            payrollDateTwo.clear()
            payrollDateTwo << '30.03.2013'
            payrollSubmit.click()
            then: 'he will get it'
            at Payroll
        }
    }

    def 'statistics page could be clicked and updated'(){
        to Signin
        username << uname
        password << passwd
        loginButton.click()
        to Statistics

        when: 'user on statistics page'
        dateField1 << '01.01.2013'
        dateField2 << '30.03.2013'
        submit.click()
        then:
        at Statistics
    }

    def 'clients page could be clicked and updated'(){

    }

    def 'CRUD sections are accessible, and you could create anything'(){

    }
}