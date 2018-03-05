package com.vaha.android.feature.questionlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.data.entity.Question;
import com.vaha.android.data.repository.SessionRepository;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.util.BundleBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class QuestionListController extends BaseController {

  private static final String KEY_SORT_TYPE = "SORT_TYPE";

  private final CompositeDisposable disposable = new CompositeDisposable();

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.view_empty_state)
  ViewGroup emptyState;

  @Inject SessionRepository sessionRepository;

  private QuestionListEpoxyController epoxyController;

  private String cursor;

  public QuestionListController(@NotNull Bundle args) {
    super(args);
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);
  }

  public static QuestionListController create(SortType sortType) {
    return new QuestionListController(
        new BundleBuilder().putInt(KEY_SORT_TYPE, sortType.ordinal()).build());
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_question_list, container, false);
  }

  @Override
  protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);

    SortType type = SortType.values()[getArgs().getInt(KEY_SORT_TYPE)];

    setupRecyclerView();

    disposable.add(
        sessionRepository
            .listQuestions(cursor, type.name())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                response -> {
                  cursor = response.getNextPageToken();

                  List<Question> items = response.getItems();

                  emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

                  epoxyController.setData(items);
                },
                Timber::e));
  }

  @Override
  protected void onDetach(@NonNull View view) {
    super.onDetach(view);
    disposable.clear();
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().questionListComponent().build().inject(this);
  }

  private void setupRecyclerView() {
    epoxyController = new QuestionListEpoxyController();
    epoxyController.setOnStartSessionClickListener(
        (v, questionClient) ->
            disposable.add(
                sessionRepository
                    .sendRequest(questionClient.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        () -> {},
                        throwable -> {
                          Timber.e(throwable);

                          if (throwable
                              .getMessage()
                              .contains("Only question owner can start a session")) {
                            Toast.makeText(
                                    getActivity(),
                                    R.string.notification_error_active_session,
                                    Toast.LENGTH_LONG)
                                .show();
                          } else {
                            Toast.makeText(
                                    getActivity(),
                                    R.string.notification_error_start_session,
                                    Toast.LENGTH_LONG)
                                .show();
                          }
                        })));

    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(epoxyController.getAdapter());
  }
}
