package com.vaha.android.feature.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.data.repository.UserRepository;
import com.vaha.android.fcm.FirebaseBackgroundService;
import com.vaha.android.feature.auth.signin.SignInController;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.feature.topicpreferences.TopicPreferencesController;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ProfileController extends BaseController {

  private final CompositeDisposable disposable = new CompositeDisposable();

  @Inject UserRepository userRepository;

  @Inject SharedPreferences sharedPreferences;

  @BindView(R.id.tv_profile_username)
  TextView tvUsername;

  @BindView(R.id.tv_profile_rating)
  TextView tvRating;

  @BindView(R.id.tv_profile_available_questions)
  TextView tvAvailableQuestions;

  @BindView(R.id.tv_profile_answer_count)
  TextView tvAnswerCount;

  @BindView(R.id.tv_profile_question_count)
  TextView tvQuestionCount;

  public ProfileController() {
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);
  }

  public static ProfileController create() {
    return new ProfileController();
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_profile, container, false);
  }

  @Override
  protected void onViewBound(@NotNull View view) {
    super.onViewBound(view);

    disposable.add(
        userRepository
            .getMe()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                user -> {
                  tvUsername.setText(user.getUsername());

                  final double rating = Double.parseDouble(user.getRating());

                  tvRating.setText(
                      getResources()
                          .getString(
                              R.string.profile_rating,
                              String.format(Locale.getDefault(), "%1$,.2f", rating)));
                  tvAvailableQuestions.setText(String.valueOf(user.getAvailableQuestionCount()));
                  tvQuestionCount.setText(String.valueOf(user.getQuestionCount()));
                  tvAnswerCount.setText(String.valueOf(user.getAnswerCount()));
                },
                Timber::e));
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().profileComponent().build().inject(this);
  }

  @OnClick(R.id.btn_profile_set_category_notifications)
  void navigateToPrefScreen() {
    getParentController()
        .getRouter()
        .pushController(
            RouterTransaction.with(new TopicPreferencesController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
  }

  @OnClick(R.id.btn_profile_log_out)
  void logOut() {
    sharedPreferences.edit().clear().apply();

    FirebaseAuth.getInstance().signOut();

    getActivity().stopService(new Intent(getActivity(), FirebaseBackgroundService.class));

    getParentController()
        .getRouter()
        .setRoot(
            RouterTransaction.with(SignInController.create())
                .pushChangeHandler(new VerticalChangeHandler()));
  }
}
