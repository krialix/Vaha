package com.vaha.server.session;

import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.vaha.server.category.entity.Category;
import com.vaha.server.notification.NotificationService;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.StartSessionUseCase;
import com.vaha.server.rule.AppEngineRule;
import com.vaha.server.user.entity.Account;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static com.vaha.server.util.DatastoreHelper.newAccount;
import static com.vaha.server.util.DatastoreHelper.newCategory;
import static com.vaha.server.util.DatastoreHelper.newQuestion;
import static com.vaha.server.util.TestObjectifyService.fact;
import static com.vaha.server.util.TestObjectifyService.ofy;

@RunWith(JUnit4.class)
public class StartSessionUseCaseTest {

  @Rule
  public final AppEngineRule rule =
      new AppEngineRule.Builder().withDatastore().withImagesService().withUrlFetch().build();

  private NotificationService notificationService;

  @Before
  public void setUp() throws Exception {
    notificationService = new NotificationService(URLFetchServiceFactory.getURLFetchService());

    fact().register(Account.class);
    fact().register(Question.class);
    fact().register(Category.class);
  }

  @Test
  public void testStartSession() throws Exception {
    Account questionOwner = newAccount();
    Account answerer = newAccount();
    Category category = newCategory("c1");
    Question question = newQuestion(questionOwner, category);

    new StartSessionUseCase(
            question.getWebsafeId(),
            questionOwner.getWebsafeId(),
            answerer.getWebsafeId(),
            notificationService)
        .run();

    Question questionNow = ofy().load().entity(question).now();
    Account questionOwnerNow = ofy().load().entity(questionOwner).now();
    Account answererNow = ofy().load().entity(answerer).now();

    assertThat(questionNow.getQuestionStatus())
        .isEqualTo(Question.QuestionStatus.IN_PROGRESS);
    assertThat(questionNow.getAnswerer().getKey()).isEqualTo(answerer.getKey());

    assertThat(questionOwnerNow.getActiveQuestionKeys()).isEqualTo(question.getKey());
    assertThat(answererNow.getActiveQuestionKeys()).isEqualTo(question.getKey());

    assertThat(questionOwnerNow.getQuestionKeys()).hasSize(1);
    assertThat(answererNow.getQuestionKeys()).hasSize(1);
  }

  @Test(expected = ConflictException.class)
  public void testStartSession_hasSessionError() throws Exception {
    Account questionOwner = newAccount();
    Account answerer = newAccount();
    Category category = newCategory("c1");
    Question question = newQuestion(questionOwner, category);

    new StartSessionUseCase(
        question.getWebsafeId(),
        questionOwner.getWebsafeId(),
        answerer.getWebsafeId(),
        notificationService)
        .run();

    new StartSessionUseCase(
        question.getWebsafeId(),
        questionOwner.getWebsafeId(),
        answerer.getWebsafeId(),
        notificationService)
        .run();
  }
}
