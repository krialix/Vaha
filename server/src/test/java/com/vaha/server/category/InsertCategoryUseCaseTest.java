package com.vaha.server.category;

import com.google.appengine.api.images.ImagesServiceFactory;
import com.vaha.server.category.client.CategoryClient;
import com.vaha.server.category.entity.Category;
import com.vaha.server.category.usecase.InsertCategoryUseCase;
import com.vaha.server.rule.AppEngineRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static com.vaha.server.util.TestObjectifyService.fact;

@RunWith(JUnit4.class)
public class InsertCategoryUseCaseTest {

  @Rule
  public final AppEngineRule rule =
      new AppEngineRule.Builder().withDatastore().withImagesService().build();

  @Before
  public void setUp() throws Exception {
    fact().register(Category.class);
  }

  @Test
  public void testInsertCategory() throws Exception {
    CategoryClient client =
        new InsertCategoryUseCase("testName", ).run();

    assertThat(client.getDisplayName()).isEqualTo("testName");
  }
}
