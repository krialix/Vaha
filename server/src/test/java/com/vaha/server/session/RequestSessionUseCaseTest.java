package com.vaha.server.session;

import com.vaha.server.category.entity.Category;
import com.vaha.server.question.entity.Question;
import com.vaha.server.question.usecase.RequestSessionUseCase;
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
public class RequestSessionUseCaseTest {

  @Rule
  public final AppEngineRule rule =
      new AppEngineRule.Builder().withDatastore().withImagesService().build();

  private Account requester;
  private Question question;

  @Before
  public void setUp() {
    fact().register(Account.class);
    fact().register(Question.class);
    fact().register(Category.class);

    Category category = newCategory("c1");
    Account questionOwner = newAccount();
    requester = newAccount();
    question = newQuestion(questionOwner, category);
  }

  @Test
  public void givenCorrectParams_returnsSuccess() {
    new RequestSessionUseCase(requester.getWebsafeId(), question.getKey().toWebSafeString()).run();

    Account accountNow = ofy().load().key(requester.getKey()).now();
    Question questionNow = ofy().load().key(question.getKey()).now();

    assertThat(accountNow.getPendingQuestionKeys()).containsExactly(question.getKey());

    assertThat(questionNow.getPendingUserRequests())
        .containsExactly(
            new Question.PendingUserRequest(
                requester.getKey(),
                requester.getUsername(),
                requester.getUserRating().getRating()));
  }
}
