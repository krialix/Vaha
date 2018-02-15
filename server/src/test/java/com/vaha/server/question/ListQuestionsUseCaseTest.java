package com.vaha.server.question;

import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.vaha.server.category.entity.Category;
import com.vaha.server.notification.NotificationService;
import com.vaha.server.question.client.QuestionClient;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.InsertQuestionUseCase;
import com.vaha.server.question.usecase.ListAvailableQuestionsUseCase;
import com.vaha.server.rule.AppEngineRule;
import com.vaha.server.user.entity.Account;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static com.vaha.server.util.DatastoreHelper.newAccount;
import static com.vaha.server.util.DatastoreHelper.newCategory;
import static com.vaha.server.util.TestObjectifyService.fact;

@RunWith(JUnit4.class)
public class ListQuestionsUseCaseTest {

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
  public void testListQuestions() throws Exception {
    Account a1 = newAccount();
    Account a2 = newAccount();
    Category c1 = newCategory("c1");

    new InsertQuestionUseCase(
            a1.getWebsafeId(), "content1", 20, c1.getWebsafeId(), notificationService)
        .run();
    new InsertQuestionUseCase(
            a1.getWebsafeId(), "content2", 20, c1.getWebsafeId(), notificationService)
        .run();
    new InsertQuestionUseCase(
            a2.getWebsafeId(), "content3", 20, c1.getWebsafeId(), notificationService)
        .run();

    CollectionResponse<QuestionClient> response =
        new ListAvailableQuestionsUseCase(null, a1.getWebsafeId()).run();

    assertThat(response.getItems()).hasSize(3);

    List<Boolean> responseOwnerStatuses =
        response.getItems().stream().map(QuestionClient::getOwner).collect(Collectors.toList());

    assertThat(responseOwnerStatuses).containsExactly(true, true, false);
  }
}
