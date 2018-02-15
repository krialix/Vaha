package com.vaha.android.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.epoxy.CallbackProp;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.vaha.android.R;
import com.vaha.server.vahaApi.model.QuestionClient;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class SessionView extends ConstraintLayout {

  @BindView(R.id.tv_session_content)
  TextView tvContent;

  @BindView(R.id.tv_session_category)
  TextView tvCategory;

  @BindView(R.id.tv_session_username)
  TextView tvUsername;

  @BindView(R.id.tv_session_time)
  TextView tvTime;

  @BindView(R.id.tv_session_status)
  TextView tvSessionStatus;

  public SessionView(Context context) {
    super(context);
    init();
  }

  public SessionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SessionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.layout_session, this);
    final int padding = getResources().getDimensionPixelSize(R.dimen._16asdp);
    setPadding(padding, padding, padding, padding);
    ButterKnife.bind(this);
  }

  @ModelProp
  public void setSession(@NonNull QuestionClient session) {
    tvContent.setText(session.getContent());
    tvCategory.setText(session.getCategoryNameEn());
    tvUsername.setText(session.getUsername());

    boolean isTurkish = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

    final String status = session.getStatus();

    switch (status) {
      case "DISPUTE":
        tvSessionStatus.setText(isTurkish ? "ANLAŞMAZLIK" : "DISPUTE");
        tvSessionStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.accent_lighter));
        break;
      case "COMPLETED":
        tvSessionStatus.setText(isTurkish ? "BAŞARILI" : "COMPLETED");
        tvSessionStatus.setTextColor(
            ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
        break;
      case "IN_PROGRESS":
        tvSessionStatus.setText(isTurkish ? "DEVAM EDİYOR" : "IN PROGRESS");
        tvSessionStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        break;
    }
  }

  @CallbackProp
  public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
    super.setOnClickListener(onClickListener);
  }
}
