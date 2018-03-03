package com.vaha.server.question.cron

import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.question.entity.Question.Status
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import org.joda.time.Days
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "DeleteExpiredQuestionsCron", urlPatterns = ["cron/deleteExpiredQuestions"])
class DeleteExpiredQuestionsCron : HttpServlet() {

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
            .type(Question::class.java)
            .iterable()
            .filter {
                it.status == Status.AVAILABLE ||
                        it.status == Status.IN_PROGRESS
            }
            .filter { Days.daysBetween(DateTime.now(UTC), it.createdAt).days == 1 }
            .map { it.copy(status = Status.AUTO_CLOSED) }
            .let { ofy().transact { ofy().save().entities(it) } }
    }
}
