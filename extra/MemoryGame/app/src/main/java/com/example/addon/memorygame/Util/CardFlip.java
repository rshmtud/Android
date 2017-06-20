package com.example.addon.memorygame.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.addon.memorygame.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Roshan on 12/12/2016.
 */

public class CardFlip extends RelativeLayout implements View.OnClickListener{
    @BindView(R.id.img_flipview)
    ImageView flipImage;
    private Context mContext;
    private boolean isFlipAllowed = true;
    private boolean isCardOpened = true;
    private String imageName;
    private boolean isImageVisible;


    public CardFlip(Context context) {
        super(context);
        mContext=context;
        initView();
    }

    public boolean isImageVisible() {
        return isImageVisible;
    }

    public void setImageVisible(boolean imageVisible) {
        isImageVisible = imageVisible;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
        flipImagewithAnimation();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.layout_card, this, true);
        ButterKnife.bind(this,view);
        flipImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card_bg));
        setClickListener();
    }

    private void setClickListener() {
        flipImage.setOnClickListener(this);
    }

    public boolean isFlipAllowed() {
        return isFlipAllowed;
    }

    public void setFlipAllowed(boolean flipAllowed) {
        isFlipAllowed = flipAllowed;
    }

    public boolean isCardOpened() {
        return isCardOpened;
    }

    public void setCardOpened(boolean cardOpened) {
        isCardOpened = cardOpened;
    }

    @Override
    public void onClick(View view) {
        
        int id = view.getId();
        
        switch (id)
        {
            
            case R.id.img_flipview:
                flipImagewithAnimation();
                break;
            
            
        }
        
    }

    private void setImageView(Drawable imgage)
    {
        flipImage.setImageDrawable(imgage);
    }

    private void flipImagewithAnimation() {

        if(isFlipAllowed)
        {
            Animation flipIn = AnimationUtils.loadAnimation(mContext, R.anim.flip_in);
            Animation fipOut = AnimationUtils.loadAnimation(mContext, R.anim.flip_out);
            if(isCardOpened)
            {
               flipImage.startAnimation(flipIn);
                flipImage.setImageDrawable(Utils.GetImage(mContext,imageName));
                setImageVisible(true);
            }else {
               // flipImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.colour3));
                flipImage.setImageDrawable(Utils.GetImage(mContext,"card_bg"));
                flipImage.startAnimation(flipIn);
                setImageVisible(false);


            }
            isCardOpened=!isCardOpened;
        }
    }
}
