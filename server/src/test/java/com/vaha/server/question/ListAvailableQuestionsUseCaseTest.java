package com.vaha.server.question;

import com.google.api.server.spi.response.CollectionResponse;
import com.vaha.server.category.entity.Category;
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
public class ListAvailableQuestionsUseCaseTest {

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

    long categoryId = category.getId();

    new InsertQuestionUseCase(a1.getWebsafeId(), "c1", categoryId).run();
    new InsertQuestionUseCase(a2.getWebsafeId(), "c2", categoryId).run();

    CollectionResponse<QuestionClient> response =
        new ListAvailableQuestionsUseCase(null, a1.getWebsafeId()).run();

    List<Boolean> isOwnerList =
        response
            .getItems()
            .stream()
            .map(questionClient -> questionClient.getUser().isOwner())
            .collect(Collectors.toList());

    assertThat(isOwnerList).containsExactly(false, true);
  }
}
