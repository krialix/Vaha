package com.vaha.android.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;

public final class DrawableUtil {

  public static Drawable tint(@NonNull Drawable drawable, @ColorInt int tint) {
    Drawable wrapped = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(wrapped.mutate(), tint);
    return wrapped;
  }

  public static void tintMenu(Menu menu, @ColorInt int tint) {
    final int size = menu.size();
    for (int i = 0; i < size; i++) {
      Drawable drawable = menu.getItem(i).getIcon();
      if (drawable != null) {
        tint(drawable, tint);
      }
    }
  }
}
