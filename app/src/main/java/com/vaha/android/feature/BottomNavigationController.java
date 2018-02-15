package com.vaha.android.feature;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.vaha.android.R;
import com.vaha.android.fcm.FirebaseBackgroundService;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.feature.categorylist.CategoryListController;
import com.vaha.android.feature.profile.ProfileController;
import com.vaha.android.feature.questionlist.QuestionListController;
import com.vaha.android.feature.sessionlist.SessionListController;

import butterknife.BindView;
import me.toptas.fancyshowcase.FancyShowCaseView;

/**
 * The {@link Controller} for the Bottom Navigation View. Populates a {@link BottomNavigationView}
 * with the supplied {@link Menu} resource. The first item set as checked will be shown by default.
 * The backstack of each {@link MenuItem} is switched out, in order to maintain a separate backstack
 * for each {@link MenuItem} - even though that is against the Google Design Guidelines:
 *
 * @author chris6647@gmail.com
 * @see <a
 *     href="https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior">Material
 *     Design Guidelines</a>
 */
public class BottomNavigationController extends BaseController {

  private static final String KEY_STATE_ROUTER_BUNDLES = "key_state_router_bundles";
  private static final String KEY_STATE_CURRENTLY_SELECTED_ID = "key_state_currently_selected_id";

  @BindView(R.id.navigation)
  BottomNavigationView bottomNavigationView;

  @BindView(R.id.child_controller_container)
  ViewGroup controllerContainer;

  private int currentlySelectedItemId;

  private SparseArray<Bundle> routerBundles;

  private Router childRouter;

  public static BottomNavigationController create() {
    return new BottomNavigationController();
  }

  private static Controller getControllerFor(int menuItemId) {
    switch (menuItemId) {
      case R.id.navigation_category:
        return CategoryListController.create();
      case R.id.navigation_questions:
        return QuestionListController.create();
      case R.id.navigation_sessions:
        return SessionListController.create();
      case R.id.navigation_profile:
        return ProfileController.create();
      default:
        throw new IllegalStateException("Unknown bottomNavigationView item selected.");
    }
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_bottom_navigation, container, false);
  }

  @Override
  protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);
    getActivity().startService(new Intent(getActivity(), FirebaseBackgroundService.class));

    View menuItemView = bottomNavigationView.findViewById(bottomNavigationView.getSelectedItemId());
    showCategoryTabTutorial(menuItemView);

    Menu menu = bottomNavigationView.getMenu();
    final int menuSize = menu.size();

    childRouter = getChildRouter(controllerContainer);

    /*
     * Not having access to Backstack or RouterTransaction constructors,
     * we have to save/restore the entire routers for each backstack.
     */
    if (routerBundles == null) {
      routerBundles = new SparseArray<>(menuSize);
      for (int i = 0; i < menuSize; i++) {
        final MenuItem menuItem = menu.getItem(i);
        final int itemId = menuItem.getItemId();
        /* Ensure the first checked item is shown */
        if (menuItem.isChecked()) {
          childRouter.setRoot(RouterTransaction.with(getControllerFor(itemId)));
          bottomNavigationView.setSelectedItemId(itemId);
          currentlySelectedItemId = bottomNavigationView.getSelectedItemId();
          break;
        }
      }
    } else {
      /*
       * Since we are restoring our state,
       * and onRestoreInstanceState is called before onViewBound,
       * all we need to do is rebind.
       */
      childRouter.rebindIfNeeded();
    }

    bottomNavigationView.setOnNavigationItemSelectedListener(
        item -> {
          showTutorials(item);

          if (currentlySelectedItemId != item.getItemId()) {
            saveChildRouter(currentlySelectedItemId);
            clearChildRouter();

            currentlySelectedItemId = item.getItemId();
            Bundle routerBundle = routerBundles.get(currentlySelectedItemId);
            if (routerBundle != null && !routerBundle.isEmpty()) {
              childRouter.restoreInstanceState(routerBundle);
              childRouter.rebindIfNeeded();
            } else {
              childRouter.setRoot(
                  RouterTransaction.with(getControllerFor(currentlySelectedItemId))
                      .pushChangeHandler(new FadeChangeHandler())
                      .popChangeHandler(new FadeChangeHandler()));
            }
            return true;
          }

          return false;
        });
  }

  private void showTutorials(MenuItem item) {
    int selectedItemId = item.getItemId();
    if (bottomNavigationView != null) {
      View view = bottomNavigationView.findViewById(selectedItemId);
      switch (selectedItemId) {
        case R.id.navigation_questions:
          showQuestionTabTutorial(view);
          break;
          /*case R.id.navigation_sessions:
          showCategoryTabTutorial(view);
          break;*/
      }
    }
  }

  private void showCategoryTabTutorial(View view) {
    new FancyShowCaseView.Builder(getActivity())
        .focusOn(view)
        .showOnce(String.valueOf(view.getId()))
        .title("Ask in a category")
        .build()
        .show();
  }

  private void showQuestionTabTutorial(View view) {
    new FancyShowCaseView.Builder(getActivity())
        .focusOn(view)
        .delay(100)
        .focusCircleRadiusFactor(2d)
        .showOnce(String.valueOf(view.getId()))
        .title("Answer to available questions")
        .build()
        .show();
  }

  private void saveChildRouter(int itemId) {
    Bundle routerBundle = new Bundle();
    childRouter.saveInstanceState(routerBundle);
    routerBundles.put(itemId, routerBundle);
  }

  /** Removes ALL {@link Controller}'s in the child{@link Router}'s backstack */
  private void clearChildRouter() {
    childRouter.setPopsLastView(true); // Ensure the last view can be removed while we do this
    childRouter.popToRoot();
    childRouter.popCurrentController();
    childRouter.setPopsLastView(false);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    routerBundles = savedInstanceState.getSparseParcelableArray(KEY_STATE_ROUTER_BUNDLES);
    currentlySelectedItemId = savedInstanceState.getInt(KEY_STATE_CURRENTLY_SELECTED_ID);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    saveChildRouter(currentlySelectedItemId);
    outState.putSparseParcelableArray(KEY_STATE_ROUTER_BUNDLES, routerBundles);
    /*
     * For some reason the BottomNavigationView does not seem to correctly restore its
     * selectedId, even though the view appears with the correct state.
     * So we keep track of it manually
     */
    outState.putInt(KEY_STATE_CURRENTLY_SELECTED_ID, currentlySelectedItemId);
  }

  @Override
  public boolean handleBack() {
    /*
    The childRouter should handleBack,
    as this BottomNavigationController doesn't have a back step sensible to the user.
    */
    return childRouter.handleBack();
  }
}
