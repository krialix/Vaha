package com.vaha.server.question.cron

import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.user.entity.Account
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "ResetDailyQuestionCountCron", urlPatterns = ["cron/resetDailyQuestionCount"])
class ResetDailyQuestionCountCron : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        process()
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        process()
    }

    private fun process() {
        ofy()
            .load()
            .type(Account::class.java)
            .iterable()
            .map { it.copy(dailyQuestionCount = 5) }
            .let { ofy().save().entities(it) }
    }
}
