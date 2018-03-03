package com.vaha.server.category;

import com.vaha.server.category.client.CategoryClient;
import com.vaha.server.category.entity.Category;
import com.vaha.server.category.usecase.InsertCategoryUseCase;
import com.vaha.server.category.usecase.ListCategoriesUseCase;
import com.vaha.server.rule.AppEngineRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static com.vaha.server.util.TestObjectifyService.fact;

@RunWith(JUnit4.class)
public class ListCategoriesUseCaseTest {

  @Rule
  public final AppEngineRule rule =
      new AppEngineRule.Builder().withDatastore().withImagesService().build();

  @Before
  public void setUp() {
    fact().register(Category.class);

    for (int i = 1; i < 4; i++) {
      new InsertCategoryUseCase("test" + i).run();
    }
  }

  @Test
  public void testListCategories() {
    Collection<CategoryClient> clients = new ListCategoriesUseCase().run();

    assertThat(clients).hasSize(3);
  }
}
