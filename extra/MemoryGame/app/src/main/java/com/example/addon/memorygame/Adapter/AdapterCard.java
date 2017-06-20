package com.example.addon.memorygame.Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.addon.memorygame.Model.CardData;
import com.example.addon.memorygame.Activity.MainActivity;
import com.example.addon.memorygame.R;
import com.example.addon.memorygame.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Roshan on 12/12/2016.
 */

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.ViewHolder> implements AdapterOperation<CardData> {

    private List<CardData> listCardData = new ArrayList<>();
    private Context mContext;
    private OnItemClickListenerTest mOnItemClickListener;
    private boolean isCardOpened = true;
    int count = 0;
    String firstImage = "";
    int idview;
    private boolean isAllFilled;

    public interface OnItemClickListenerTest {
        void onItemClick(CardData item,boolean isComplete);
    }


    public AdapterCard(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_card, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.imageFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardData cardData = listCardData.get(holder.getAdapterPosition());
                checkCardMatchLogic(cardData, holder.imageFlip, view);
            }
        });
        return holder;
    }
    private void checkCardMatchLogic(CardData cardData, ImageView imageView, View v) {
        if (count == 0) {
            count++;
            firstImage = cardData.getCardName();
            idview = cardData.getId();
            flipImagewithAnimation(imageView, cardData, v);
        } else if (count == 1) {
            if (cardData.getCardName().equalsIgnoreCase(firstImage) && idview != cardData.getId()) {
                imageView.setClickable(false);
                setViewUnclickable(idview, cardData.getId());
                MainActivity.SCORE += 2;
            } else {
                MainActivity.SCORE--;
                isCardOpened = true;
                if (cardData.getCardName().equalsIgnoreCase(firstImage) || idview == cardData.getId()) {
                    isCardOpened = false;
                }
                flipImagewithAnimation(imageView, cardData, v);
            }
            isCardOpened = true;
            count = -1;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    count=0;
                    update(listCardData);
                    mOnItemClickListener.onItemClick(listCardData.get(0),false);
                }
            };
            handler.postDelayed(runnable,1000);
        }


    }

    private void setViewUnclickable(int id1, int id2) {
        int countL = 0;
        int totCount = 0;
        for (CardData cardData : listCardData) {
            if (cardData.getId() == id1 || cardData.getId() == id2) {
                listCardData.get(countL).setImageVisible(true);
                listCardData.get(countL).setFlipped(true);
            }
            countL++;
            if(cardData.isImageVisible())
            {
                totCount++;
            }
        }
        if(totCount==16)
        {
            isAllFilled=true;
            mOnItemClickListener.onItemClick(listCardData.get(0),true);
        }
    }

    public OnItemClickListenerTest getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListenerTest mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String imgName = listCardData.get(holder.getAdapterPosition()).getCardName();
        if (listCardData.get(holder.getAdapterPosition()).isImageVisible()) {
            holder.imageFlip.setClickable(false);
            holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, imgName));
        } else {
            holder.imageFlip.setClickable(true);
            holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, "card_bg"));
        }
    }

    @Override
    public int getItemCount() {
        return listCardData.size();
    }

    @Override
    public void addAll(List<CardData> mCardDataList) {
        for (CardData cardData : mCardDataList) {
            add(cardData);
        }

    }

    @Override
    public void add(CardData analyticsItemViewDataModel) {
        listCardData.add(analyticsItemViewDataModel);
        notifyItemInserted(listCardData.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        return (getItemCount() == 0);
    }

    @Override
    public void remove(CardData item) {
        int position = listCardData.indexOf(item);
        if (position > -1) {
            listCardData.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }

    }

    public void update(List<CardData> modelList) {
        listCardData = new ArrayList<>();
        clear();
        for (CardData model : modelList) {
            listCardData.add(model);
            Log.i("imageName ==>>", model.getCardName());
        }
        notifyDataSetChanged();
    }

    @Override
    public CardData getItem(int position) {
        return listCardData.get(position);
    }

    private void flipImagewithAnimation(ImageView flipImage, CardData card, View v) {
        String imageName = card.getCardName();
        Animation flipIn = AnimationUtils.loadAnimation(mContext, R.anim.flip_in);
        Animation fipOut = AnimationUtils.loadAnimation(mContext, R.anim.flip_out);
        if (isCardOpened) {
            if (true) {
                flipImage.startAnimation(flipIn);
                flipImage.setImageDrawable(Utils.GetImage(mContext, imageName));
            }
        } else {
            flipImage.setImageDrawable(Utils.GetImage(mContext, "card_bg"));
            flipImage.startAnimation(flipIn);
        }
        isCardOpened = !isCardOpened;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_flipview)
        ImageView imageFlip;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final CardData cardData, final OnItemClickListenerTest mOnItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(cardData,isAllFilled);
                }
            });
        }
    }
}
