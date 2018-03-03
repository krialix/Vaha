package com.vaha.server.session;

import com.google.api.server.spi.response.BadRequestException;
import com.googlecode.objectify.Ref;
import com.vaha.server.category.entity.Category;
import com.vaha.server.question.entity.Question;
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
import static com.vaha.server.util.DatastoreHelper.newQuestion;
import static com.vaha.server.util.TestObjectifyService.fact;
import static com.vaha.server.util.TestObjectifyService.ofy;

@RunWith(JUnit4.class)
public class StartSessionUseCaseTest {

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
  public void testStartSession() {
    Account questionOwner = newAccount();
    Account answerer = newAccount();
    Category category = newCategory("c1");
    Question question = newQuestion(questionOwner, category);

    new RequestSessionUseCase(answerer.getWebsafeId(), question.getKey().toWebSafeString()).run();

    new StartSessionUseCase(
            question.getKey().toWebSafeString(),
        answerer.getWebsafeId())
        .run();

    Question questionNow = ofy().load().entity(question).now();
    Account questionOwnerNow = ofy().load().entity(questionOwner).now();
    Account answererNow = ofy().load().entity(answerer).now();

    assertThat(questionNow.getStatus()).isEqualTo(Question.Status.IN_PROGRESS);
    assertThat(questionNow.getAnswerer()).isEqualTo(Ref.create(answerer.getKey()));
    assertThat(questionNow.getPendingUserRequests())
        .doesNotContain(
            new Question.PendingUserRequest(
                answerer.getKey(), answerer.getUsername(), answerer.getUserRating().getRating()));

    assertThat(questionOwnerNow.getInProgressQuestionKeys()).containsExactly(question.getKey());

    assertThat(answererNow.getAnswerCount()).isEqualTo(1);
    assertThat(answererNow.getPendingQuestionKeys()).doesNotContain(question.getKey());
    assertThat(answererNow.getInProgressQuestionKeys()).hasSize(1);
  }

  @Test(expected = BadRequestException.class)
  public void testStartSession_hasSessionError() {
    Account questionOwner = newAccount();
    Account answerer = newAccount();
    Category category = newCategory("c1");
    Question question = newQuestion(questionOwner, category);

    new StartSessionUseCase(
            question.getKey().toWebSafeString(),
        answerer.getWebsafeId())
        .run();

    new StartSessionUseCase(
            question.getKey().toWebSafeString(),
        answerer.getWebsafeId())
        .run();
  }
}
