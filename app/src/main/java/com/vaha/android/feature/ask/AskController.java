package com.vaha.android.feature.ask;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.data.repository.SessionRepository;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.util.BundleBuilder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AskController extends BaseController {

  private static final String KEY_CATEGORY_ID = "CATEGORY_ID";

  private final CompositeDisposable disposable = new CompositeDisposable();

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.spinner)
  Spinner spinnerActiveTime;

  @BindView(R.id.et_ask_content)
  EditText etContent;

  @Inject SessionRepository sessionRepository;

  private ProgressDialog progressDialog;

  public AskController(@NonNull Bundle bundle) {
    super(bundle);
  }

  public static AskController create(@NonNull String categoryId) {
    return new AskController(new BundleBuilder().putString(KEY_CATEGORY_ID, categoryId).build());
  }

  @NotNull
  @Override
  protected View inflateView(@NotNull LayoutInflater inflater, @NotNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_ask, container, false);
  }

  @Override
  protected void onViewBound(@NotNull View view) {
    super.onViewBound(view);
    setupToolbar();
  }

  @Override
  protected void onDetach(@NonNull View view) {
    super.onDetach(view);
    disposable.dispose();
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().askBuilder().build().inject(this);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @OnClick(R.id.tv_ask_send)
  void sendQuestion() {
    final String content = etContent.getText().toString().trim();

    if (TextUtils.isEmpty(content)) {
      Snackbar.make(getView(), R.string.ask_question_error_empty_question, Snackbar.LENGTH_SHORT)
          .show();
      return;
    }

    showProgressDialog();

    final String categoryId = getArgs().getString(KEY_CATEGORY_ID, "");

    disposable.add(
        sessionRepository
            .insertQuestion(content, categoryId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnDispose(this::hideProgressDialog)
            .subscribe(
                () -> {
                  hideProgressDialog();
                  getRouter().handleBack();
                },
                throwable -> {
                  hideProgressDialog();

                  if (throwable.getMessage().contains("400")) {
                    Snackbar.make(
                            getView(),
                            R.string.ask_question_daily_limit_over,
                            Snackbar.LENGTH_SHORT)
                        .show();
                  }

                  Timber.e(throwable);
                }));
  }

  private void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getResources().getString(R.string.ask_question_sending));
      progressDialog.setIndeterminate(true);
    }

    progressDialog.show();
  }

  private void hideProgressDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }
}
