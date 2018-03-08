package com.vaha.android.feature.questionpanel;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.vaha.android.R;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.feature.questionlist.QuestionListController;
import com.vaha.android.feature.questionlist.SortType;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class QuestionPagerController extends BaseController {

  @BindView(R.id.tabLayout_question_pager)
  TabLayout tabLayout;

  @BindView(R.id.viewPager_question_pager)
  ViewPager viewPager;

  @NotNull
  @Override
  protected View inflateView(@NotNull LayoutInflater inflater, @NotNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_question_pager, container, false);
  }

  @Override
  protected void onViewBound(@NotNull View view) {
    super.onViewBound(view);

    Resources res = getResources();

    List<Pair<String, Controller>> pairs = Arrays.asList(
        Pair.create(
            res.getString(R.string.pager_available_questions),
            QuestionListController.create(SortType.AVAILABLE)),
        Pair.create(
            res.getString(R.string.pager_pending_questions),
            QuestionListController.create(SortType.PENDING_SESSION)),
        Pair.create(
            res.getString(R.string.pager_active_questions),
            QuestionListController.create(SortType.ACTIVE_SESSION)),
        Pair.create(
            res.getString(R.string.pager_completed_questions),
            QuestionListController.create(SortType.COMPLETED_SESSION)));

    QuestionPager questionPager = new QuestionPager(this, pairs);
    viewPager.setAdapter(questionPager);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  protected void onDestroyView(@NotNull View view) {
    if (getActivity() != null) {
      if (!getActivity().isChangingConfigurations()) {
        viewPager.setAdapter(null);
      }
      tabLayout.setupWithViewPager(null);
    }

    super.onDestroyView(view);
  }

  static final class QuestionPager extends RouterPagerAdapter {

    final List<Pair<String, Controller>> pairs;

    QuestionPager(@NonNull Controller host, List<Pair<String, Controller>> pairs) {
      super(host);
      this.pairs = pairs;
    }

    @Override
    public void configureRouter(@NonNull Router router, int position) {
      if (!router.hasRootController()) {
        Pair<String, Controller> pair = pairs.get(position);
        if (pair.second != null) {
          router.setRoot(RouterTransaction.with(pair.second));
        }
      }
    }

    @Override
    public int getCount() {
      return pairs.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return pairs.get(position).first;
    }
  }
}
