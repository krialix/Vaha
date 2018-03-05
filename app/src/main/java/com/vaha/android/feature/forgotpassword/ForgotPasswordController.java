package com.vaha.android.feature.forgotpassword;

import android.app.ProgressDialog;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.vaha.android.R;
import com.vaha.android.feature.auth.ValidationUtils;
import com.vaha.android.feature.base.BaseController;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

public final class ForgotPasswordController extends BaseController {

  @BindView(R.id.til_forgot_password_email)
  TextInputLayout tilEmail;

  private ProgressDialog progressDialog;

  public static ForgotPasswordController create() {
    return new ForgotPasswordController();
  }

  @NotNull
  @Override
  protected View inflateView(@NotNull LayoutInflater inflater, @NotNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_forgot_password, container, false);
  }

  @OnClick(R.id.btn_forgot_password_reset)
  void passwordResetClick() {
    String email = tilEmail.getEditText().getText().toString();
    if (!ValidationUtils.isValidEmailAddress(email).isValid()) {
      showSnackbar(R.string.sign_in_error_invalid_email);
      return;
    }

    showProgressDialog();

    FirebaseAuth.getInstance()
        .sendPasswordResetEmail(email.trim().toLowerCase())
        .addOnCompleteListener(
            task -> {
              hideProgressDialog();

              if (task.isSuccessful()) {
                showSnackbar(R.string.forgot_password_instructions);
              } else {
                showSnackbar(R.string.forgot_password_failed_email_sent);
              }
            });
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
