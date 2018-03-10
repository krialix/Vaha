package com.vaha.server.question;

import com.google.api.server.spi.response.BadRequestException;
import com.vaha.server.category.entity.Category;
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

  @Before
  public void setUp() {
    fact().register(Account.class);
    fact().register(Question.class);
    fact().register(Category.class);
  }

  @Test
  public void testInsertQuestion() {
    Account account = newAccount();
    Category c1 = newCategory("c1");

    QuestionClient client =
        new InsertQuestionUseCase(account.getWebsafeId(), "testContent", c1.getId()).run();

    assertThat(client.getId()).isNotEmpty();
    assertThat(client.getUser())
        .isEqualTo(
            new QuestionClient.QuestionOwnerClient(
                account.getWebsafeId(), account.getUsername(), true));

    assertThat(client.getCategory().getId()).isEqualTo(c1.getId());
    assertThat(client.getContent()).isEqualTo("testContent");
    assertThat(client.getAnswerer()).isNull();
    assertThat(client.isRequestSent()).isFalse();
    assertThat(client.getPendingUserRequests()).isEmpty();
    assertThat(client.getCreatedAt()).isNotNull();

    ofy().clear();

    Category c1Now = ofy().load().key(c1.getKey()).now();

    assertThat(c1Now.getQuestionCount()).isEqualTo(1);

    Account accountNow = ofy().load().key(account.getKey()).now();

    assertThat(accountNow.getQuestionCount()).isEqualTo(1);
    assertThat(accountNow.getDailyQuestionCount()).isEqualTo(4);
  }

  @Test(expected = BadRequestException.class)
  public void testInsertQuestion_noDaily() {
    Account account = newAccount();
    Category c1 = newCategory("c1");

    new InsertQuestionUseCase(account.getWebsafeId(), "testContent", c1.getId()).run();
    new InsertQuestionUseCase(account.getWebsafeId(), "testContent2", c1.getId()).run();
    new InsertQuestionUseCase(account.getWebsafeId(), "testContent3", c1.getId()).run();
    new InsertQuestionUseCase(account.getWebsafeId(), "testContent4", c1.getId()).run();
    new InsertQuestionUseCase(account.getWebsafeId(), "testContent5", c1.getId()).run();
    new InsertQuestionUseCase(account.getWebsafeId(), "testContent6", c1.getId()).run();
  }
}
