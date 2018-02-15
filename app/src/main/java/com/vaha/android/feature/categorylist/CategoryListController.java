package com.vaha.android.feature.categorylist;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.vaha.android.R;
import com.vaha.android.VahaApplication;
import com.vaha.android.domain.CategoryListViewState;
import com.vaha.android.feature.ask.AskController;
import com.vaha.android.feature.base.BaseController;
import com.vaha.android.util.Connectivity;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

public class CategoryListController extends BaseController {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @Inject ViewModelProvider.Factory viewModelFactory;

  private CategoryListViewModel viewModel;

  private CategoryListEpoxyController epoxyController;

  public static CategoryListController create() {
    return new CategoryListController();
  }

  @NonNull
  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_category_list, container, false);
  }

  @Override
  protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);

    setupRecyclerView();

    viewModel = viewModelFactory.create(CategoryListViewModel.class);

    /*viewModel =
        ViewModelProvider.AndroidViewModelFactory.getInstance().of((FragmentActivity) getActivity(), viewModelFactory)
            .get(CategoryListViewModel.class);*/
  }

  @Override
  protected void onAttach(@NonNull View view) {
    super.onAttach(view);

    viewModel
        .getData()
        .observe(
            this,
            viewState -> {
              if (viewState instanceof CategoryListViewState.Data) {
                epoxyController.setData(((CategoryListViewState.Data) viewState).getEpoxyItems());
              } else if (viewState instanceof CategoryListViewState.Error) {
                Timber.d(((CategoryListViewState.Error) viewState).getError());
              }
            });
  }

  @Override
  protected void injectDependencies() {
    super.injectDependencies();
    VahaApplication.getAppComponent().categoryListComponent().build().inject(this);
  }

  private void setupRecyclerView() {
    final int spanCount = 2;
    epoxyController = new CategoryListEpoxyController();
    epoxyController.setOnItemClickListener(
        (v, categoryEntity) -> {
          if (!Connectivity.isConnected(getApplicationContext())) {
            Snackbar.make(getView(), R.string.common_error_no_connection, Snackbar.LENGTH_SHORT)
                .show();
            return;
          }

          boolean emailVerified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
          if (!emailVerified) {
            Toast.makeText(getActivity(), R.string.common_validate_email, Toast.LENGTH_SHORT)
                .show();
            return;
          }

          getParentController()
              .getRouter()
              .pushController(
                  RouterTransaction.with(AskController.create(categoryEntity.getId()))
                      .pushChangeHandler(new VerticalChangeHandler())
                      .popChangeHandler(new VerticalChangeHandler()));
        });

    epoxyController.setOnVerifyEmailClickListener(
        view ->
            new AlertDialog.Builder(getActivity())
                .setTitle(R.string.common_resend_activation_mail)
                .setPositiveButton(
                    android.R.string.yes,
                    (dialogInterface, i) ->
                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification())
                .setNegativeButton(android.R.string.no, null)
                .show());

    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
    epoxyController.setSpanCount(spanCount);
    layoutManager.setSpanSizeLookup(epoxyController.getSpanSizeLookup());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(epoxyController.getAdapter());
  }
}
