package co.cheez.cheez.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;

import co.cheez.cheez.App;
import co.cheez.cheez.ImageWebView;
import co.cheez.cheez.MainActivity;
import co.cheez.cheez.Post;
import co.cheez.cheez.R;
import co.cheez.cheez.URLViewActivity;
import co.cheez.cheez.util.DialogUtil;
import co.cheez.cheez.util.MotionEventUtil;

/**
 * Created by jiho on 4/12/15.
 */
public class ContentViewFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String KEY_POSITION = "position";
    private Post mPost;
    private ImageWebView mImageWebView;
    private VideoView mVideoView;
    private boolean mLinkClicked = false;
    private Dialog mLikeDialog;
    private float mWindowWidth;
    private float mWindowHeight;

    private Button likeButton;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContentViewFragment newInstance(int position) {
        ContentViewFragment fragment = new ContentViewFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ContentViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.HONEYCOMB){
            Point size = new Point();
            display.getSize(size);
            mWindowWidth = size.x;
            mWindowHeight = size.y;
        }
        else{
            mWindowWidth = display.getWidth();  // deprecated
            mWindowHeight = display.getHeight();
        }


        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        mPost = App.getData().get(position);
        View rootView = null;
        if (mPost == null) {
            rootView = inflater.inflate(R.layout.fragment_last_page, container, false);
            rootView.findViewById(R.id.btn_show_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(MainActivity.getIntent(getActivity()));
                    getActivity().finish();
                }
            });

            return rootView;
        } else {
            rootView = inflater.inflate(R.layout.fragment_content_view, container, false);
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    mPost.touch(mWindowWidth, mWindowHeight, event.getRawX(), event.getRawY(), MotionEventUtil.actionToString(event.getAction()));
                    return true;
                }
            });
        }





        mPost.pass();
        // Get tracker.
        Tracker t = ((App) getActivity().getApplication()).getTracker();

        // Set screen name.
        t.setScreenName("ContentView " + mPost.id);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView subtitle = (TextView) rootView.findViewById(R.id.subtitle);
        Button linkButton = (Button) rootView.findViewById(R.id.btn_link);
        likeButton = (Button) rootView.findViewById(R.id.btn_like);
        Button hateButton = (Button) rootView.findViewById(R.id.btn_hate);

        if (mPost.liked) {
            likeButton.setTextColor(getResources().getColor(R.color.yelllow));
        } else {
            likeButton.setTextColor(getResources().getColor(android.R.color.white));
        }


        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_main);


        if (mPost.imageUrl.endsWith(".gif")) {
            mImageWebView = (ImageWebView) rootView.findViewById(R.id.wv_main);
            mImageWebView.setVisibility(View.VISIBLE);
            try {
                mImageWebView.setImageUrl(mPost.imageUrl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(mPost.imageUrl, imageView);
        }


        title.setText(mPost.title);
        if (mPost.subtitle != null) {
            subtitle.setText(mPost.subtitle);
        } else {
            subtitle.setVisibility(View.GONE);
        }

        linkButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        hateButton.setOnClickListener(this);


        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(App.BASE_URL + "/" + mPost.id))
                .build();

        ShareButton shareButton = (ShareButton) rootView.findViewById(R.id.btn_share_facebook);
        shareButton.setShareContent(content);


        rootView.findViewById(R.id.btn_send_mail).setOnClickListener(this);
        rootView.findViewById(R.id.btn_share).setOnClickListener(this);
        rootView.findViewById(R.id.btn_share_kakaotalk).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onPause() {
        if (mImageWebView != null) {
            //mImageWebView.onPause();
            //mImageWebView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mImageWebView != null) {
            mImageWebView.onResume();
            mImageWebView.resumeTimers();
        }

        if (mLinkClicked) {
            if (mLikeDialog == null || !mLikeDialog.isShowing()) {
                //showLikeDialog();
            }
        }
    }

    private void showLikeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("잘 보고 오셨나요?")
                .setMessage("이 게시물을 평가해주세요.\n" +
                        "평가가 다음 게시물 추천에 반영됩니다.")
                .setNegativeButton("싫어요 :(", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPost.like(false);
                        dialog.dismiss();
                        mLinkClicked = false;
                    }
                })
                .setPositiveButton("좋아요 :)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPost.like(true);
                        dialog.dismiss();
                        mLinkClicked = false;
                    }
                })
                .setCancelable(false);
        mLikeDialog = builder.create();
        mLikeDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_link:
                startActivity(URLViewActivity.getIntent(getActivity(), mPost.url));
                mLinkClicked = true;
                mPost.linkClick();
                break;
            case R.id.btn_like:
                if (!mPost.liked)
                    Toast.makeText(getActivity(), "좋아요 :)", Toast.LENGTH_SHORT).show();
                mPost.like(true);
                if (mPost.liked) {
                    likeButton.setTextColor(getResources().getColor(R.color.yelllow));
                } else {
                    likeButton.setTextColor(getResources().getColor(android.R.color.white));
                }

                break;
            case R.id.btn_hate:
                Toast.makeText(getActivity(), "싫어요 :(", Toast.LENGTH_SHORT).show();
                mPost.like(false);
                break;
            case R.id.btn_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, App.BASE_URL + "/" + mPost.id);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mPost.title);
                startActivity(Intent.createChooser(intent, "Share"));
                break;
            case R.id.btn_share_kakaotalk:
                KakaoLink kakaoLink = App.getKakaoLink();
                final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                try {
                    String text = mPost.title;
                    if (mPost.subtitle != null) {
                        text += "\n" + mPost.subtitle;
                    }
                    kakaoTalkLinkMessageBuilder
                            .addImage(mPost.imageUrl, 100, 100)
                            .addText(text)
                            .addWebLink("보러가기", App.BASE_URL + "/" + mPost.id).build();

                    final String linkContents = kakaoTalkLinkMessageBuilder.build();
                    kakaoLink.sendMessage(linkContents, getActivity());
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_mail:
                DialogUtil.getSendMailDialog(getActivity()).show();
                break;
        }
    }
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
                view.setVisibility(View.GONE);

            } else if (position <= 0) { // [-1,0]
                view.setVisibility(View.VISIBLE);
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);


            } else if (position <= 1) { // (0,1]
                view.setVisibility(View.VISIBLE);
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setVisibility(View.GONE);
                view.setAlpha(0);
            }
        }
    }



}


