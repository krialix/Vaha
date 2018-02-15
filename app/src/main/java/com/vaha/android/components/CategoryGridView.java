package com.vaha.android.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.epoxy.CallbackProp;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.vaha.android.BuildConfig;
import com.vaha.android.R;
import com.vaha.android.data.entity.Category;
import com.vaha.android.util.GlideApp;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@ModelView(defaultLayout = R.layout.item_category_grid)
public class CategoryGridView extends ConstraintLayout {

  private static final String imageQualityModifier = "150";

  @BindView(R.id.iv_category_bg)
  ImageView ivImage;

  @BindView(R.id.tv_category_text)
  TextView tvDisplayName;

  public CategoryGridView(Context context) {
    super(context);
    init();
  }

  public CategoryGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CategoryGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private static CharSequence getImageUrl(CharSequence imageUrl) {
    return BuildConfig.DEBUG ? imageUrl : imageUrl + "=s" + imageQualityModifier + "-rw";
  }

  private void init() {
    inflate(getContext(), R.layout.layout_category_grid, this);
    ButterKnife.bind(this);
  }

  @ModelProp
  public void setCategory(Category category) {
    String displayLanguage = Locale.getDefault().getDisplayLanguage();

    if (displayLanguage.equals("Türkçe")) {
      tvDisplayName.setText(category.getDisplayNameTr());
    } else {
      tvDisplayName.setText(category.getDisplayNameEn());
    }

    final CharSequence url = getImageUrl(category.getImage());

    GlideApp.with(getContext()).load(url).into(ivImage);
  }

  @CallbackProp
  public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
    super.setOnClickListener(onClickListener);
  }
}
