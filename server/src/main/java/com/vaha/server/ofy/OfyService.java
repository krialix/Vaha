package com.vaha.server.ofy;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.opt.joda.JodaMoneyTranslators;
import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;
import com.vaha.server.category.entity.Category;
import com.vaha.server.question.entity.Question;
import com.vaha.server.user.entity.Account;

public final class OfyService {

  static {
    JodaTimeTranslators.add(factory());
    JodaMoneyTranslators.add(factory());

    factory().register(Account.class);
    factory().register(Category.class);
    factory().register(Question.class);
  }

  public static Objectify ofy() {
    return ObjectifyService.ofy();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}
