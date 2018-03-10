package com.vaha.android.feature.sessionlist;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.data.repository.SessionRepository;
import com.vaha.android.feature.base.BaseController;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

public class SessionListController extends BaseController {

  private final CompositeDisposable disposable = new CompositeDisposable();

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.view_empty_state)
  ViewGroup emptyState;

  @Inject SessionRepository sessionRepository;

  @Inject SharedPreferences sharedPreferences;

  //private SessionListEpoxyController epoxyController;

  public static SessionListController create() {
    return new SessionListController();
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_session_list, container, false);
  }

  @Override
  protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);

    //setupRecyclerView();

    /*disposable.add(
    sessionRepository
        .getSessions()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            response -> {
              List<QuestionClient> clients =
                  response.getItems() == null ? Collections.emptyList() : response.getItems();

              emptyState.setVisibility(clients.isEmpty() ? View.VISIBLE : View.GONE);

              epoxyController.setData(clients);
            },
            Timber::e));*/
  }

  @Override
  protected void onDetach(@NonNull View view) {
    super.onDetach(view);
    disposable.clear();
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().sessionListComponent().build().inject(this);
  }

  /*private void setupRecyclerView() {
    epoxyController = new SessionListEpoxyController();
    epoxyController.setOnClickListener(
        (v, questionClient) -> {
          boolean enableWriting = questionClient.getStatus().equals("IN_PROGRESS");

          String localUserId = sharedPreferences.getString("userId", "");
          String answererId = questionClient.getAnswererId();
          String targetUserId =
              localUserId.equals(answererId) ? questionClient.getOwnerId() : answererId;

          Controller controller =
              SessionController.create(
                  questionClient.getId(),
                  sharedPreferences.getString("userId", ""),
                  targetUserId,
                  sharedPreferences.getString("username", ""),
                  questionClient.getOwnerId().equals(sharedPreferences.getString("userId", "")),
                  enableWriting);

          getParentController()
              .getRouter()
              .pushController(
                  RouterTransaction.with(controller)
                      .popChangeHandler(new VerticalChangeHandler())
                      .pushChangeHandler(new VerticalChangeHandler()));
        });

    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(epoxyController.getAdapter());
  }*/
}
