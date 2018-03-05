package com.vaha.android.feature.auth.signin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.data.repository.UserRepository;
import com.vaha.android.fcm.FirebaseBackgroundService;
import com.vaha.android.feature.BottomNavigationController;
import com.vaha.android.feature.auth.ValidationUtils;
import com.vaha.android.feature.auth.signup.SignUpController;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.feature.forgotpassword.ForgotPasswordController;
import com.vaha.android.util.KeyboardUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SignInController extends BaseController {

  private final CompositeDisposable disposable = new CompositeDisposable();

  @BindView(R.id.til_sign_in_email)
  TextInputLayout tilEmail;

  @BindView(R.id.til_sign_in_password)
  TextInputLayout tilPassword;

  @Inject UserRepository userRepository;

  @Inject SharedPreferences sharedPreferences;

  private ProgressDialog progressDialog;

  public static SignInController create() {
    return new SignInController();
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_sign_in, container, false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    disposable.clear();
  }

  @OnClick(R.id.tv_sign_in_action)
  void signInWithEmail() {
    tilEmail.setError(null);
    tilPassword.setError(null);

    final String email = tilEmail.getEditText().getText().toString();
    final String password = tilPassword.getEditText().getText().toString();

    boolean cancel = false;
    View focusView = null;

    Resources res = getResources();

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      tilEmail.setError(res.getString(R.string.sign_in_error_field_required));
      focusView = tilEmail;
      cancel = true;
    } else if (!ValidationUtils.isValidEmailAddress(email).isValid()) {
      tilEmail.setError(res.getString(R.string.sign_in_error_invalid_email));
      focusView = tilEmail;
      cancel = true;
    }

    // Check for a valid password, if the user entered one.
    if (TextUtils.isEmpty(password)) {
      tilPassword.setError(res.getString(R.string.sign_in_error_field_required));
      focusView = tilPassword;
      cancel = true;
    } else if (!ValidationUtils.isValidPassword(password).isValid()) {
      tilPassword.setError(res.getString(R.string.sign_in_error_invalid_password));
      focusView = tilPassword;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      if (getView() != null) {
        KeyboardUtil.hideKeyboard(getView());
      }

      showProgressDialog();

      disposable.add(
          RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
              .subscribeOn(AndroidSchedulers.mainThread())
              .observeOn(Schedulers.io())
              .flatMapCompletable(
                  firebaseUser ->
                      userRepository.updateFcmToken(FirebaseInstanceId.getInstance().getToken()))
              .andThen(userRepository.getMe())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(
                  userClient -> {
                    sharedPreferences
                        .edit()
                        .putString("userId", userClient.getId())
                        .putString("username", userClient.getUsername())
                        .apply();

                    hideProgressDialog();

                    Intent intent = new Intent(getActivity(), FirebaseBackgroundService.class);
                    getActivity().startService(intent);

                    getRouter()
                        .setRoot(
                            RouterTransaction.with(BottomNavigationController.create())
                                .pushChangeHandler(new VerticalChangeHandler()));
                  },
                  throwable -> {
                    hideProgressDialog();

                    Timber.e(throwable);

                    if (throwable instanceof FirebaseNetworkException) {
                      showSnackbar(R.string.common_error_no_connection);
                    } else if (throwable instanceof FirebaseAuthInvalidUserException
                        || throwable.getMessage().contains("401")) {
                      showSnackbar(R.string.sign_in_error_user_not_available);
                    } else {
                      showSnackbar(R.string.common_error_unknown);
                    }
                  }));
    }
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().signInControllerComponent().build().inject(this);
  }

  @OnClick(R.id.tv_sign_in_register)
  void navigateSignUpScreen() {
    Controller controller = SignUpController.create();
    getRouter()
        .pushController(
            RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
  }

  @OnClick(R.id.tv_sign_in_forgot_password)
  void navigateForgotPasswordScreen() {
    Controller controller = ForgotPasswordController.create();
    getRouter()
        .pushController(
            RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
  }

  private void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getResources().getString(R.string.sign_in_label_loading));
      progressDialog.setIndeterminate(true);
    }

    progressDialog.show();
  }

  private void hideProgressDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void showSnackbar(@StringRes int stringRes) {
    Snackbar.make(getView(), stringRes, Snackbar.LENGTH_SHORT).show();
  }
}
