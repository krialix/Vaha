package com.vaha.android.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.epoxy.CallbackProp;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.vaha.android.R;
import com.vaha.android.ui.RequestView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT, saveViewState = true)
public class QuestionView extends ConstraintLayout {

  @BindView(R.id.tv_question_content)
  TextView tvContent;

  @BindView(R.id.tv_question_asked_by)
  TextView tvAskedBy;

  @BindView(R.id.tv_question_answered_by)
  TextView tvAnsweredBy;

  @BindView(R.id.tv_question_send_session_request)
  TextView tvSendSessionRequest;

  @BindView(R.id.tv_question_category)
  TextView tvCategory;

  @BindView(R.id.tv_question_pending_requests)
  ViewGroup vgPendingRequests;

  @BindView(R.id.layout_question_expansion_content_wrapper)
  ViewGroup vgExpansionContentWrapper;

  public QuestionView(Context context) {
    super(context);
    init();
  }

  public QuestionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public QuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.layout_question, this);
    ButterKnife.bind(this);
  }

  @ModelProp
  public void setCategoryName(String categoryName) {
    tvCategory.setText(categoryName);
  }

  @ModelProp
  public void setContent(String content) {
    tvContent.setText(content);
  }

  @ModelProp
  public void setAskedBy(String askedBy) {
    tvAskedBy.setText(getResources().getString(R.string.question_asked_by, askedBy));
  }

  @ModelProp
  public void setAnsweredBy(@Nullable String answeredBy) {
    tvAnsweredBy.setVisibility(answeredBy == null ? GONE : VISIBLE);
    tvAnsweredBy.setText(getResources().getString(R.string.question_answered_by, answeredBy));
  }

  @ModelProp
  public void isOwner(boolean owner) {
    tvSendSessionRequest.setVisibility(owner ? GONE : VISIBLE);
  }

  @ModelProp
  public void setPendingRequests(@Nullable List<PendingRequest> requests) {
    vgPendingRequests.setVisibility(requests == null ? GONE : VISIBLE);

    if (requests != null && vgExpansionContentWrapper.getChildCount() == 0) {
      for (PendingRequest request : requests) {
        RequestView view = new RequestView(getContext().getApplicationContext());
        view.setContent(request.displayName, request.rating);
        vgExpansionContentWrapper.addView(view);
      }
    }
  }

  @ModelProp
  public void setSendRequestVisibility(boolean requestSent) {
    if (requestSent) {
      tvSendSessionRequest.setEnabled(false);
      tvSendSessionRequest.setText(R.string.question_request_sent);
    }
  }

  @CallbackProp
  public void setOnStartSessionClickListener(@Nullable View.OnClickListener onClickListener) {
    tvSendSessionRequest.setOnClickListener(
        view -> {
          if (onClickListener != null) {
            onClickListener.onClick(view);
          }
        });
  }

  public static class PendingRequest {
    private final String id;
    private final String displayName;
    private final String rating;

    public PendingRequest(String id, String displayName, String rating) {
      this.id = id;
      this.displayName = displayName;
      this.rating = rating;
    }

    public String getId() {
      return id;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getRating() {
      return rating;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PendingRequest that = (PendingRequest) o;
      return Objects.equals(id, that.id)
          && Objects.equals(displayName, that.displayName)
          && Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, displayName, rating);
    }
  }
}
