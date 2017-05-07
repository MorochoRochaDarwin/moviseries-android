package xyz.moviseries.moviseries.bottom_sheets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import xyz.moviseries.moviseries.DeveloperKey;
import xyz.moviseries.moviseries.R;

/**
 * Created by DARWIN on 7/5/2017.
 */

public class BottomSheetMovieOptions extends BottomSheetDialogFragment {
    public static final String MOVIE_ID = "BottomSheetOpcionesPelicula.movie_id";
    public static final String TRAILER = "BottomSheetOpcionesPelicula.trailer";

    private String movie_id,trailer;


    private Context context;
    private BottomSheetBehavior mBehavior;


    public static BottomSheetDialogFragment newInstance(Bundle args) {
        BottomSheetMovieOptions bottomSheetNuevoEvento = new BottomSheetMovieOptions();
        bottomSheetNuevoEvento.setArguments(args);
        return bottomSheetNuevoEvento;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();


        Bundle args = getArguments();
        movie_id = args.getString(MOVIE_ID);
        trailer = args.getString(TRAILER);


    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        setStatusBarColorIfPossible(R.color.colorPrimary);

        View contentView = View.inflate(context, R.layout.bottom_sheet_opciones_pelicula, null);
        dialog.setContentView(contentView);

        Button btn_enlaces = (Button) contentView.findViewById(R.id.btn_enlaces);
        Button btn_trailer = (Button) contentView.findViewById(R.id.btn_trailer);


        LinearLayout layout = (LinearLayout) contentView.findViewById(R.id.marginBottom);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout.getLayoutParams();
        lp.height = navigationBarHeight();


        mBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        btn_enlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent  intent = YouTubeStandalonePlayer.createVideoIntent(
                        getActivity(), DeveloperKey.DEVELOPER_KEY, trailer, 0, true, false);
                startActivity(intent);
            }
        });

    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    /**
     * cambia el color del staus bar
     *
     * @param color
     */
    private void setStatusBarColorIfPossible(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getDialog().getWindow().setStatusBarColor(color);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    private int navigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }

        return 0;
    }


}
