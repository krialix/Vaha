package com.vaha.server.session;

import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.vaha.server.category.entity.Category;
import com.vaha.server.notification.NotificationService;
import com.vaha.server.question.client.QuestionClient;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.EndSessionUseCase;
import com.vaha.server.question.usecase.FindActiveSessionUseCase;
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

@RunWith(JUnit4.class)
public class FindActiveSessionUseCaseTest {

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
  public void testFindActiveSession() throws Exception {
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

    QuestionClient client1 = new FindActiveSessionUseCase(questionOwner.getWebsafeId()).run();
    assertThat(client1).isNotNull();

    QuestionClient client2 = new FindActiveSessionUseCase(answerer.getWebsafeId()).run();
    assertThat(client2).isNotNull();
  }

  @Test
  public void testFindActiveSession_returnsNull() throws Exception {
    Account questionOwner = newAccount();
    Account answerer = newAccount();

    QuestionClient client1 = new FindActiveSessionUseCase(questionOwner.getWebsafeId()).run();
    assertThat(client1).isNull();

    QuestionClient client2 = new FindActiveSessionUseCase(answerer.getWebsafeId()).run();
    assertThat(client2).isNull();
  }

  @Test
  public void testFindActiveSession_ends() throws Exception {
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

    QuestionClient client1 = new FindActiveSessionUseCase(questionOwner.getWebsafeId()).run();
    assertThat(client1).isNotNull();

    QuestionClient client2 = new FindActiveSessionUseCase(answerer.getWebsafeId()).run();
    assertThat(client2).isNotNull();

    new EndSessionUseCase(question.getWebsafeId(), 2.0f, Question.QuestionStatus.COMPLETED).run();

    QuestionClient client3 = new FindActiveSessionUseCase(questionOwner.getWebsafeId()).run();
    assertThat(client3).isNull();

    QuestionClient client4 = new FindActiveSessionUseCase(answerer.getWebsafeId()).run();
    assertThat(client4).isNull();
  }
}
