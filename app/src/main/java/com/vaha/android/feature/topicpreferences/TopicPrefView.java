package com.vaha.android.feature.topicpreferences;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

import com.airbnb.epoxy.CallbackProp;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.vaha.android.R;
import com.vaha.android.data.entity.TopicResponse;

import java.util.Locale;

import static com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class TopicPrefView extends SwitchCompat {

  public TopicPrefView(Context context) {
    super(context);
    init();
  }

  public TopicPrefView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TopicPrefView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    final int padding = getResources().getDimensionPixelSize(R.dimen._16asdp);
    setPadding(padding, padding, padding, padding);
  }

  @ModelProp
  public void setTopicResponse(TopicResponse topicResponse) {
    setChecked(topicResponse.isSubscribed());

    String displayLanguage = Locale.getDefault().getDisplayLanguage();

    if (displayLanguage.equals("Türkçe")) {
      setText(topicResponse.getDisplayName());
    } else {
      setText(topicResponse.getDisplayName());
    }
  }

  @CallbackProp
  public void selectListener(@Nullable OnCheckedChangeListener onCheckedChangeListener) {
    setOnCheckedChangeListener(onCheckedChangeListener);
  }
}
