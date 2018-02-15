package com.vaha.android.feature.questionlist;

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
import com.vaha.android.data.repository.QuestionRepository;
import com.vaha.android.data.repository.SessionRepository;
import com.vaha.android.feature.base.BaseController;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class QuestionListController extends BaseController {

  private final CompositeDisposable disposable = new CompositeDisposable();

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.view_empty_state)
  ViewGroup emptyState;

  @Inject QuestionRepository questionRepository;

  @Inject SessionRepository sessionRepository;

  private QuestionListEpoxyController epoxyController;

  private String cursor;

  public QuestionListController() {
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);
  }

  public static QuestionListController create() {
    return new QuestionListController();
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_question_list, container, false);
  }

  @Override
  protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);

    setupRecyclerView();

    disposable.add(
        questionRepository
            .listQuestions(cursor)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                response -> {
                  // cursor = response.getNextPageToken();

                  /*List<QuestionClient> clients =
                  response.getItems() == null ? Collections.emptyList() : response.getItems();*/

                  emptyState.setVisibility(response.isEmpty() ? View.VISIBLE : View.GONE);

                  epoxyController.setData(response);
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
                    .sendAnswererAvailable(questionClient.getId())
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
