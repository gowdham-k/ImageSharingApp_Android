package com.numnu.android.fragments.locationdetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.numnu.android.R;
import com.numnu.android.adapter.locationdetail.LocationItemsListAdapter;
import com.numnu.android.fragments.auth.LoginFragment;
import com.numnu.android.network.ApiServices;
import com.numnu.android.network.ServiceGenerator;
import com.numnu.android.network.request.BookmarkRequestData;
import com.numnu.android.network.response.BookmarkResponse;
import com.numnu.android.network.response.EventTagBusiness;
import com.numnu.android.network.response.ItemsByTagResponse;
import com.numnu.android.utils.Constants;
import com.numnu.android.utils.PreferencesHelper;
import com.numnu.android.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thulir on 9/10/17.
 */

public class LocationItemsListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView menuitemsRecyclerView;
    private Context context;
    private String title;
    ImageView toolbarIcon;
    private String locationId;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private int PAGE_SIZE = 20;
    private int nextPage = 1;
    private LocationItemsListAdapter eventItemsListAdapter;
    private ItemsByTagResponse eventItemsResponse;
    private String tagId;
    public ProgressDialog mProgressDialog;
    private AlertDialog dialog;

    public static LocationItemsListFragment newInstance(String category, String locationId, String tagId) {

        LocationItemsListFragment eventBusinessFragment = new LocationItemsListFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        args.putString("locationId", locationId);
        args.putString("tagId", tagId);
        eventBusinessFragment.setArguments(args);

        return eventBusinessFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            title = bundle.getString("category");
            locationId = bundle.getString("locationId");
            tagId = bundle.getString("tagId");
        }


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    View  view= inflater.inflate(R.layout.fragment_event_items_list, container, false);

        menuitemsRecyclerView = view.findViewById(R.id.menu_items_recyclerview);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        menuitemsRecyclerView.setLayoutManager(layoutManager);
//    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(menuitemsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
//        menuitemsRecyclerView.addItemDecoration(dividerItemDecoration);
       toolbarIcon= view.findViewById(R.id.toolbar_image);
      toolbarIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
            public void onClick(View v) {
                            showAlertshare();
            }
        });
        RelativeLayout toolbarBackImage = view.findViewById(R.id.toolbar_back);

        toolbarBackImage.setOnClickListener(this);

        TextView toolbarTitle=view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);

        final android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menuitemsRecyclerView.scrollToPosition(0);
            }
        });

        if(Utils.isNetworkAvailable(context)) {
            getItems();
        }else {
            showAlert();
        }
        // Pagination
        menuitemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadMoreItems();
                    }
                }
            }
        });

        return view;

}


    private void showAlert() {

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("No Internet connection");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    private void getItems()
    {

        isLoading = true;
        ApiServices apiServices = ServiceGenerator.createServiceHeader(ApiServices.class);
        Call<ItemsByTagResponse> call=apiServices.getLocationItemsByTagId(locationId,tagId);
        call.enqueue(new Callback<ItemsByTagResponse>() {
            @Override
            public void onResponse(Call<ItemsByTagResponse> call, Response<ItemsByTagResponse> response) {
                int responsecode = response.code();
                if(responsecode==200) {
                    eventItemsResponse = response.body();
                    updateUI();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<ItemsByTagResponse> call, Throwable t) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
                isLoading = false;

            }
        });

    }

    private void loadMoreItems()
    {

        nextPage += 1;
        isLoading = true;
        ApiServices apiServices = ServiceGenerator.createServiceHeader(ApiServices.class);
        Call<ItemsByTagResponse> call=apiServices.getLocationItemsByTagId(locationId,tagId, String.valueOf(nextPage));
        call.enqueue(new Callback<ItemsByTagResponse>() {
            @Override
            public void onResponse(Call<ItemsByTagResponse> call, Response<ItemsByTagResponse> response) {
                int responsecode = response.code();
                if(responsecode==200) {
                    List<EventTagBusiness> dataItems=response.body().getData();
                    if(!response.body().getPagination().isHasMore()){
                        isLastPage = true;
                    }
                    eventItemsListAdapter.addData(dataItems);
                    eventItemsListAdapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<ItemsByTagResponse> call, Throwable t) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
                isLoading = false;
                showAlert();


            }
        });

    }

    private void updateUI() {

        eventItemsListAdapter = new LocationItemsListAdapter(context, eventItemsResponse.getData());
        menuitemsRecyclerView.setAdapter(eventItemsListAdapter);
        eventItemsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        //View view = getLayoutInflater().inflate(R.layout.progress);
        alertDialog.setView(R.layout.progress);
        dialog = alertDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void hideProgressDialog(){
        if(dialog!=null)
        dialog.dismiss();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                getActivity().onBackPressed();
                break;



        }
    }

    public void showAlertshare() {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.bookmark_layout, null);
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        alertDialog.setView(deleteDialogView);
        final TextView shareTxt = (TextView) deleteDialogView.findViewById(R.id.share);
        final TextView BookmarkTxt = (TextView) deleteDialogView.findViewById(R.id.bookmark);
        TextView cancel = (TextView) deleteDialogView.findViewById(R.id.gender_cancel);
//        LinearLayout GenderLinLay = (LinearLayout) deleteDialogView.findViewById(R.id.genlin_lay);
//        Button ok = deleteDialogView.findViewById(R.id.ok_button);

        final android.support.v7.app.AlertDialog alertDialog1 = alertDialog.create();
        shareTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Post Content here..."+context.getPackageName());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share_using)));
                alertDialog1.dismiss();
            }
        });
        BookmarkTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean loginStatus =  PreferencesHelper.getPreferenceBoolean(context,PreferencesHelper.PREFERENCE_LOGGED_IN);
                if (!loginStatus) {
                    Bundle bundle = new Bundle();
                    bundle.putString("BusinessBookmarkIntent","businessbookmark");
                    LoginFragment logFragment = new LoginFragment();
                    logFragment.setArguments(bundle);
                    FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_righ);
                    transaction.add(R.id.frame_layout, logFragment);
                    transaction.addToBackStack(null).commit();
//                    Intent intent = new Intent(context, LoginFragment.class);
//                    intent.putExtra("BusinessBookmarkIntent","businessbookmark");
//                    context.startActivity(intent);
                    alertDialog1.dismiss();
                }else {
                    postBookmark();
                    alertDialog1.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
//        GenderLinLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog1.dismiss();
//            }
//        });

//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });


        alertDialog1.setCanceledOnTouchOutside(false);
        try {
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog1.show();
//        alertDialog1.getWindow().setLayout((int)Utils.convertDpToPixel(290,
//                getActivity()),(int)Utils.convertDpToPixel(290,getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog1.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.shareDialogAnimation;
        alertDialog1.getWindow().setAttributes(lp);
    }

    private void postBookmark()
    {
        showProgressDialog();
        String userId = PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_ID);
        BookmarkRequestData bookmarkRequestData = new BookmarkRequestData();
        bookmarkRequestData.setClientapp(Constants.CLIENT_APP);
        bookmarkRequestData.setClientip(Utils.getLocalIpAddress(context));
        bookmarkRequestData.setType(Constants.BOOKMARK_LOCATION);
        bookmarkRequestData.setEntityid(locationId);
//        bookmarkRequestData.setEntityname(eventName.getText().toString());

        ApiServices apiServices = ServiceGenerator.createServiceHeader(ApiServices.class);
        Call<BookmarkResponse> call=apiServices.postBookmark(userId,bookmarkRequestData);
        call.enqueue(new Callback<BookmarkResponse>() {
            @Override
            public void onResponse(Call<BookmarkResponse> call, Response<BookmarkResponse> response) {
                int responsecode = response.code();
                if(responsecode==201) {
                    BookmarkResponse bookmarkResponse = response.body();
                    Toast.makeText(context, "Bookmarked this page", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }else if(responsecode==422) {

                    Toast.makeText(context, "Already Bookmarked!!", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BookmarkResponse> call, Throwable t) {
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });

    }

}

