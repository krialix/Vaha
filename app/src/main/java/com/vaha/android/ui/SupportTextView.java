package com.vaha.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.vaha.android.R;

public class SupportTextView extends AppCompatTextView {

  private static final int LEFT_DRAWABLE_INDEX = 0;
  private static final int TOP_DRAWABLE_INDEX = 1;
  private static final int RIGHT_DRAWABLE_INDEX = 2;
  private static final int BOTTOM_DRAWABLE_INDEX = 3;

  private int drawableWidth;
  private int drawableHeight;

  private int drawableTint;

  public SupportTextView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public SupportTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public SupportTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    if (attrs == null) {
      return;
    }

    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.SupportTextView, defStyleAttr, 0);

    final int drawableLeftResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableLeft, -1);
    final int drawableStartResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableStart, -1);
    final int drawableTopResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableTop, -1);
    final int drawableRightResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableRight, -1);
    final int drawableEndResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableEnd, -1);
    final int drawableBottomResId =
        a.getResourceId(R.styleable.SupportTextView_android_drawableBottom, -1);

    drawableWidth = a.getDimensionPixelSize(R.styleable.SupportTextView_compoundDrawableWidth, 0);
    drawableHeight = a.getDimensionPixelSize(R.styleable.SupportTextView_compoundDrawableHeight, 0);
    drawableTint = a.getColor(R.styleable.SupportTextView_drawableTintCompat, Color.TRANSPARENT);

    a.recycle();

    final int leftDrawableId = drawableLeftResId == -1 ? drawableStartResId : drawableLeftResId;
    final int rightDrawableId = drawableRightResId == -1 ? drawableEndResId : drawableRightResId;

    initCompoundDrawables(leftDrawableId, drawableTopResId, rightDrawableId, drawableBottomResId);
  }

  @Override
  public void setCompoundDrawables(
      @Nullable Drawable left,
      @Nullable Drawable top,
      @Nullable Drawable right,
      @Nullable Drawable bottom) {
    super.setCompoundDrawables(left, top, right, bottom);
    resize(getCompoundDrawables());
  }

  @Override
  public void setCompoundDrawablesRelative(
      @Nullable Drawable start,
      @Nullable Drawable top,
      @Nullable Drawable end,
      @Nullable Drawable bottom) {
    super.setCompoundDrawablesRelative(start, top, end, bottom);
    resize(getCompoundDrawablesRelative());
  }

  @Override
  public void setCompoundDrawablesRelativeWithIntrinsicBounds(
      int start, int top, int end, int bottom) {
    super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    resize(getCompoundDrawablesRelative());
  }

  @Override
  public void setCompoundDrawablesRelativeWithIntrinsicBounds(
      @Nullable Drawable start,
      @Nullable Drawable top,
      @Nullable Drawable end,
      @Nullable Drawable bottom) {
    super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    resize(getCompoundDrawablesRelative());
  }

  @Override
  public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
    super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    resize(getCompoundDrawables());
  }

  @Override
  public void setCompoundDrawablesWithIntrinsicBounds(
      @Nullable Drawable left,
      @Nullable Drawable top,
      @Nullable Drawable right,
      @Nullable Drawable bottom) {
    super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    resize(getCompoundDrawables());
  }

  public void setDrawableTint(@ColorInt int drawableTint) {
    this.drawableTint = drawableTint;
    invalidate();
  }

  private Drawable getVectorDrawable(@DrawableRes int drawableResId) {
    return AppCompatResources.getDrawable(getContext(), drawableResId);
  }

  private void initCompoundDrawables(
      int drawableStartId, int drawableTopId, int drawableEndId, int drawableBottomId) {
    Drawable[] drawables = getCompoundDrawablesRelative();

    inflateVectors(drawableStartId, drawableTopId, drawableEndId, drawableBottomId, drawables);
    resize(drawables);
    tint(drawables);

    setCompoundDrawables(
        drawables[LEFT_DRAWABLE_INDEX],
        drawables[TOP_DRAWABLE_INDEX],
        drawables[RIGHT_DRAWABLE_INDEX],
        drawables[BOTTOM_DRAWABLE_INDEX]);
  }

  private void inflateVectors(
      int drawableStartId,
      int drawableTopId,
      int drawableEndVectorId,
      int drawableBottomVectorId,
      Drawable[] drawables) {
    boolean rtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;

    if (drawableStartId != -1) {
      drawables[rtl ? RIGHT_DRAWABLE_INDEX : LEFT_DRAWABLE_INDEX] =
          getVectorDrawable(drawableStartId);
    }
    if (drawableTopId != -1) {
      drawables[TOP_DRAWABLE_INDEX] = getVectorDrawable(drawableTopId);
    }
    if (drawableEndVectorId != -1) {
      drawables[rtl ? LEFT_DRAWABLE_INDEX : RIGHT_DRAWABLE_INDEX] =
          getVectorDrawable(drawableEndVectorId);
    }
    if (drawableBottomVectorId != -1) {
      drawables[BOTTOM_DRAWABLE_INDEX] = getVectorDrawable(drawableBottomVectorId);
    }
  }

  private void tint(Drawable[] drawables) {
    if (drawableTint == Color.TRANSPARENT) {
      return;
    }

    for (int i = 0; i < drawables.length; i++) {
      if (drawables[i] == null) {
        continue;
      }

      Drawable wrappedDrawable = DrawableCompat.wrap(drawables[i]);
      DrawableCompat.setTint(wrappedDrawable.mutate(), drawableTint);

      drawables[i] = wrappedDrawable;
    }
  }

  private void resize(Drawable[] drawables) {
    if (drawableHeight > 0 || drawableWidth > 0) {
      for (Drawable drawable : drawables) {
        if (drawable == null) {
          continue;
        }

        final Rect realBounds =
            new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        float actualDrawableWidth = realBounds.width();
        float actualDrawableHeight = realBounds.height();
        float actualDrawableRatio = actualDrawableHeight / actualDrawableWidth;

        float scale;
        // check if both width and height defined then adjust drawable size according to the ratio
        if (drawableHeight > 0 && drawableWidth > 0) {
          float placeholderRatio = drawableHeight / (float) drawableWidth;
          if (placeholderRatio > actualDrawableRatio) {
            scale = drawableWidth / actualDrawableWidth;
          } else {
            scale = drawableHeight / actualDrawableHeight;
          }
        } else if (drawableHeight > 0) { // only height defined
          scale = drawableHeight / actualDrawableHeight;
        } else { // only width defined
          scale = drawableWidth / actualDrawableWidth;
        }

        actualDrawableWidth = actualDrawableWidth * scale;
        actualDrawableHeight = actualDrawableHeight * scale;

        realBounds.right = realBounds.left + Math.round(actualDrawableWidth);
        realBounds.bottom = realBounds.top + Math.round(actualDrawableHeight);

        drawable.setBounds(realBounds);
      }
    } else {
      for (Drawable drawable : drawables) {
        if (drawable == null) {
          continue;
        }

        drawable.setBounds(
            new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
      }
    }
  }
}
