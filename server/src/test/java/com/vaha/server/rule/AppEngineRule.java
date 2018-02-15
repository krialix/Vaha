package com.vaha.server.rule;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalImagesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;
import com.googlecode.objectify.util.Closeable;
import com.vaha.server.util.TestObjectifyService;
import com.vaha.server.util.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static com.vaha.server.util.TestObjectifyService.fact;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.json.XML.toJSONObject;

/**
 * JUnit Rule for managing the App Engine testing environment.
 *
 * <p>Generally you'll want to configure the environment using only the services you need (because
 * each service is expensive to create).
 *
 * <p>This rule also resets global Objectify for the current thread.
 *
 * @see ExternalResource
 */
public final class AppEngineRule extends ExternalResource {

  /*
    The GAE testing library requires queue.xml to be a file, not a resource in a jar, so we read it
    in here and write it to a temporary file later.
   */
  /*private static final String QUEUE_XML =
      readResourceUtf8("WEB-INF/queue.xml");*/

  /** A parsed version of the indexes used in the prod code. */
  /*private static final Set<String> MANUAL_INDEXES =
      getIndexXmlStrings(readResourceUtf8("com/yoloo/server/env/default/WEB_INF/datastore-indexes.xml"));*/

  private LocalServiceTestHelper helper;

  /** A rule-within-a-rule to provide a temporary folder for AppEngineRule's internal temp files. */
  private TemporaryFolder temporaryFolder = new TemporaryFolder();

  private boolean withDatastore;
  private boolean withLocalModules;
  private boolean withTaskQueue;
  private boolean withUserService;
  private boolean withUrlFetch;
  private boolean withImages;
  private boolean withMemcache;
  private Clock clock;

  private String taskQueueXml;
  private UserInfo userInfo;

  private Closeable rootService;

  public static Builder builder() {
    return new Builder();
  }

  /** Read a Datastore index file, and parse the indexes into individual strings. */
  private static Set<String> getIndexXmlStrings(String indexFile) {
    ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
    try {
      // To normalize the indexes, we are going to pass them through JSON and then rewrite the xml.
      JSONObject datastoreIndexes = new JSONObject();
      Object indexes = toJSONObject(indexFile).get("datastore-indexes");
      if (indexes instanceof JSONObject) {
        datastoreIndexes = (JSONObject) indexes;
      }
      for (JSONObject index : getJsonAsArray(datastoreIndexes.opt("datastore-index"))) {
        builder.add(getIndexXmlString(index));
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }

  /**
   * Normalize a value from JSONObject that represents zero, one, or many values. If there were zero
   * values this will be null or an empty JSONArray, depending on how the field was represented in
   * JSON. If there was one value, the object passed in will be that value. If there were more than
   * one values, the object will be a JSONArray containing those values. We will return a list in
   * all cases.
   */
  private static List<JSONObject> getJsonAsArray(@Nullable Object object) throws JSONException {
    ImmutableList.Builder<JSONObject> builder = new ImmutableList.Builder<>();
    if (object instanceof JSONArray) {
      for (int i = 0; i < ((JSONArray) object).length(); ++i) {
        builder.add(((JSONArray) object).getJSONObject(i));
      }
    } else if (object instanceof JSONObject) {
      // When there's only a single entry it won't be wrapped in an array.
      builder.add((JSONObject) object);
    }
    return builder.build();
  }

  /** Turn a JSON representation of an index into xml. */
  private static String getIndexXmlString(JSONObject source) throws JSONException {
    StringBuilder builder = new StringBuilder();
    builder.append(
        String.format(
            "<datastore-index kind=\"%s\" ancestor=\"%s\" source=\"manual\">\n",
            source.getString("kind"), source.get("ancestor").toString()));
    for (JSONObject property : getJsonAsArray(source.get("property"))) {
      builder.append(
          String.format(
              "  <property name=\"%s\" direction=\"%s\"/>\n",
              property.getString("name"), property.getString("direction")));
    }
    return builder.append("</datastore-index>").toString();
  }

  /** Create some fake registrars. */
  public static void loadInitialData() {
    /*persistSimpleResources(
    ImmutableList.of(
        makeRegistrar1(), makeRegistrarContact1(), makeRegistrar2(), makeRegistrarContact2()));*/
  }

  /** Hack to make sure AppEngineRule is always wrapped in a TemporaryFolder rule. */
  @Override
  public Statement apply(Statement base, Description description) {
    return RuleChain.outerRule(temporaryFolder)
        .around((base1, description1) -> AppEngineRule.super.apply(base1, null))
        .apply(base, description);
  }

  @Override
  protected void before() throws Throwable {
    Set<LocalServiceTestConfig> configs = new HashSet<>();
    if (withUrlFetch) {
      configs.add(new LocalURLFetchServiceTestConfig());
    }
    if (withImages) {
      configs.add(new LocalImagesServiceTestConfig());
    }
    if (withDatastore) {
      configs.add(
          new LocalDatastoreServiceTestConfig()
              // We need to set this to allow cross entity group transactions.
              .setApplyAllHighRepJobPolicy()
              // This causes unit tests to write a file containing any indexes the test required. We
              // can use that file below to make sure we have the right indexes in our prod code.
              .setNoIndexAutoGen(false));
      // This forces app engine to write the generated indexes to a usable location.
      System.setProperty("appengine.generated.dir", temporaryFolder.getRoot().getAbsolutePath());
    }
    if (withLocalModules) {
      configs.add(
          new LocalModulesServiceTestConfig()
              .addBasicScalingModuleVersion("default", "1", 1)
              .addBasicScalingModuleVersion("tools", "1", 1)
              .addBasicScalingModuleVersion("backend", "1", 1));
    }
    if (withTaskQueue) {
      File queueFile = temporaryFolder.newFile("queue.xml");
      Files.asCharSink(queueFile, UTF_8).write(taskQueueXml);
      configs.add(new LocalTaskQueueTestConfig().setQueueXmlPath(queueFile.getAbsolutePath()));
    }
    if (withUserService) {
      configs.add(new LocalUserServiceTestConfig());
    }

    helper = new LocalServiceTestHelper(configs.toArray(new LocalServiceTestConfig[] {}));

    if (withUserService) {
      // Set top-level properties on LocalServiceTestConfig for user login.
      helper
          .setEnvIsLoggedIn(userInfo.isLoggedIn())
          // This envAttributes thing is the only way to set userId.
          // see https://code.google.com/p/googleappengine/issues/detail?id=3579
          .setEnvAttributes(
              ImmutableMap.of(
                  "com.google.appengine.api.users.UserService.user_id_key",
                  userInfo.getGaeUserId()))
          .setEnvAuthDomain(userInfo.getAuthDomain())
          .setEnvEmail(userInfo.getEmail())
          .setEnvIsAdmin(userInfo.isAdmin());
    }

    if (clock != null) {
      helper.setClock(() -> clock.millis());
    }

    if (withLocalModules) {
      helper.setEnvInstance("0");
    }

    helper.setUp();

    if (withDatastore) {
      if (rootService != null) {
        rootService.close();
      }

      TestObjectifyService.initialize();
      rootService = TestObjectifyService.begin();

      JodaTimeTranslators.add(fact());

      loadInitialData();
    }

    if (withMemcache) {
      configs.add(new LocalMemcacheServiceTestConfig());
    }
  }

  @Override
  protected void after() {
    if (rootService != null) {
      rootService.close();
      rootService = null;
    }

    helper.tearDown();
    helper = null;

    // Test that Datastore didn't need any indexes we don't have listed in our index file.
    /*try {
      Set<String> autoIndexes =
          getIndexXmlStrings(
              Files.asCharSource(
                      new File(temporaryFolder.getRoot(), "datastore-indexes-auto.xml"), UTF_8)
                  .read());
      Set<String> missingIndexes = Sets.difference(autoIndexes, MANUAL_INDEXES);
      if (!missingIndexes.isEmpty()) {
        assert_().fail("Missing indexes:\n%s", Joiner.on('\n').join(missingIndexes));
      }
    } catch (IOException e) { // This is fine; no indexes were written.
    }*/
  }

  /** Builder for {@link AppEngineRule}. */
  public static class Builder {

    private AppEngineRule rule = new AppEngineRule();

    /** Turn on the Datastore service. */
    public Builder withDatastore() {
      rule.withDatastore = true;
      return this;
    }

    /** Turn on the use of local modules. */
    public Builder withLocalModules() {
      rule.withLocalModules = true;
      return this;
    }

    /** Turn on the task queue service. */
    public Builder withTaskQueue() {
      return withTaskQueue("src/main/webapp/WEB-INF/queue.xml");
    }

    /** Turn on the task queue service with a specified set of queues. */
    public Builder withTaskQueue(String taskQueueXml) {
      rule.withTaskQueue = true;
      rule.taskQueueXml = taskQueueXml;
      return this;
    }

    /** Turn on the URL Fetch service. */
    public Builder withUrlFetch() {
      rule.withUrlFetch = true;
      return this;
    }

    public Builder withClock(Clock clock) {
      rule.clock = clock;
      return this;
    }

    public Builder withUserService(UserInfo userInfo) {
      rule.withUserService = true;
      rule.userInfo = userInfo;
      return this;
    }

    public Builder withImagesService() {
      rule.withImages = true;
      return this;
    }

    public Builder withMemcacheService() {
      rule.withMemcache = true;
      return this;
    }

    public AppEngineRule build() {
      return rule;
    }
  }
}
