package com.vaha.server.question;

import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.vaha.server.category.entity.Category;
import com.vaha.server.notification.NotificationService;
import com.vaha.server.question.client.QuestionClient;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.InsertQuestionUseCase;
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
import static com.vaha.server.util.TestObjectifyService.fact;
import static com.vaha.server.util.TestObjectifyService.ofy;

@RunWith(JUnit4.class)
public class InsertQuestionUseCaseTest {

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
  public void testInsertQuestion() throws Exception {
    Account account = newAccount();
    Category c1 = newCategory("c1");

    QuestionClient client =
        new InsertQuestionUseCase(
                account.getWebsafeId(), "testContent", 20, c1.getWebsafeId(), notificationService)
            .run();

    assertThat(client.getUsername()).isEqualTo(account.getUsername());
    assertThat(client.getBounty()).isEqualTo(20);
    assertThat(client.getCategoryId()).isEqualTo(c1.getWebsafeId());
    assertThat(client.getCategoryNameEn()).isEqualTo(c1.getDisplayName());
    assertThat(client.getContent()).isEqualTo("testContent");
    assertThat(client.getStatus()).isEqualTo(Question.QuestionStatus.AVAILABLE.name());
    assertThat(client.getOwner()).isEqualTo(true);
    assertThat(client.getOwnerId()).isEqualTo(account.getWebsafeId());

    ofy().clear();

    Category c1Now = ofy().load().key(c1.getKey()).now();

    assertThat(c1Now.getQuestionCount()).isEqualTo(1);

    Account accountNow = ofy().load().key(account.getKey()).now();

    assertThat(accountNow.getQuestionCount()).isEqualTo(1);
    assertThat(accountNow.getDailyQuestionCount()).isEqualTo(1);
  }

  @Test(expected = BadRequestException.class)
  public void testInsertQuestion_noDaily() throws Exception {
    Account account = newAccount();
    Category c1 = newCategory("c1");

    new InsertQuestionUseCase(
            account.getWebsafeId(), "testContent", 20, c1.getWebsafeId(), notificationService)
        .run();
    new InsertQuestionUseCase(
            account.getWebsafeId(), "testContent2", 20, c1.getWebsafeId(), notificationService)
        .run();
    new InsertQuestionUseCase(
            account.getWebsafeId(), "testContent3", 20, c1.getWebsafeId(), notificationService)
        .run();
  }
}
