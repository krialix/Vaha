package com.vaha.server.question;

import com.google.api.server.spi.response.CollectionResponse;
import com.vaha.server.category.entity.Category;
import com.vaha.server.question.client.QuestionClient;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.InsertQuestionUseCase;
import com.vaha.server.question.usecase.ListInProgressQuestionsUseCase;
import com.vaha.server.question.usecase.RequestSessionUseCase;
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
import static com.vaha.server.util.TestObjectifyService.fact;

@RunWith(JUnit4.class)
public class ListInProgressQuestionsUseCaseTest {

  @Rule
  public final AppEngineRule rule =
      new AppEngineRule.Builder().withDatastore().withImagesService().build();

  @Before
  public void setUp() {
    fact().register(Account.class);
    fact().register(Question.class);
    fact().register(Category.class);
  }

  @Test
  public void testListQuestions() {
    Account a1 = newAccount();
    Account a2 = newAccount();
    Category category = newCategory("c1");

    String categoryId = category.getKey().toWebSafeString();

    QuestionClient c1 = new InsertQuestionUseCase(a2.getWebsafeId(), "c1", categoryId).run();
    QuestionClient c2 = new InsertQuestionUseCase(a2.getWebsafeId(), "c2", categoryId).run();

    new RequestSessionUseCase(a1.getWebsafeId(), c1.getId()).run();
    new RequestSessionUseCase(a1.getWebsafeId(), c2.getId()).run();

    new StartSessionUseCase(c1.getId(), a1.getWebsafeId()).run();
    new StartSessionUseCase(c2.getId(), a1.getWebsafeId()).run();

    CollectionResponse<QuestionClient> response1 =
        new ListInProgressQuestionsUseCase(null, a1.getWebsafeId()).run();

    assertThat(response1.getItems()).hasSize(2);

    CollectionResponse<QuestionClient> response2 =
        new ListInProgressQuestionsUseCase(null, a1.getWebsafeId()).run();

    assertThat(response2.getItems()).hasSize(2);
  }
}
