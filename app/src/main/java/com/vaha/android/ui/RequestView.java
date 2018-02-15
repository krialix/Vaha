package com.vaha.android.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vaha.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestView extends ConstraintLayout {

  @BindView(R.id.tv_request_displayName)
  TextView tvDisplayName;

  public RequestView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public RequestView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public RequestView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    inflate(context, R.layout.layout_request, this);
    ButterKnife.bind(this);
  }

  public void setContent(String name, String rating) {
    tvDisplayName.setText(getContext().getString(R.string.request_view_content, name, rating));
  }
}
