package de.tu_darmstadt.informatik.newapp.GameActivity.Adapter;

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


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.tu_darmstadt.informatik.newapp.GameActivity.Game.GameActivity;
import de.tu_darmstadt.informatik.newapp.GameActivity.Model.CardData;
import de.tu_darmstadt.informatik.newapp.GameActivity.Util.Utils;
import de.tu_darmstadt.informatik.newapp.R;
/**
 * Created by Roshan on 12/12/2016.
 */
public class AdapterCard extends RecyclerView.Adapter<AdapterCard.ViewHolder> implements AdapterOperation<CardData> {

  private List<CardData> listCardData = new ArrayList<>();
  private Context mContext;
  private OnItemClickListenerTest mOnItemClickListener;
  private boolean isCardOpened = true;
  /**
   * The Count.
   */
  int count = 0;
  /**
   * The First image.
   */
  String firstImage = "";
  /**
   * The Idview.
   */
  int idview;
  private boolean isAllFilled;
  private boolean isFirstTimeCall=true;

  /**
   * The interface On item click listener test.
   */
  public interface OnItemClickListenerTest {
    /**
     * On item click.
     *
     * @param item       the item
     * @param isComplete the is complete
     */
    void onItemClick(CardData item,boolean isComplete);
  }


  /**
   * Instantiates a new Adapter card.
   *
   * @param mContext the m context
   */
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
        try {
          CardData cardData = listCardData.get(holder.getAdapterPosition());
          checkCardMatchLogic(cardData, holder.imageFlip, view);
        }catch(Exception e)
        {
          Log.d("INFO","Exception Catched");
        }
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
        GameActivity.SCORE += 2;
      } else {
        GameActivity.SCORE--;
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

  /**
   * Gets on item click listener.
   *
   * @return the on item click listener
   */
  public OnItemClickListenerTest getmOnItemClickListener() {
    return mOnItemClickListener;
  }

  /**
   * Sets on item click listener.
   *
   * @param mOnItemClickListener the m on item click listener
   */
  public void setmOnItemClickListener(OnItemClickListenerTest mOnItemClickListener) {
    this.mOnItemClickListener = mOnItemClickListener;
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final String imgName = listCardData.get(holder.getAdapterPosition()).getCardName();
    holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, imgName));

    if (listCardData.get(holder.getAdapterPosition()).isImageVisible()) {
      holder.imageFlip.setClickable(false);
      holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, imgName));
    } else {
      holder.imageFlip.setClickable(true);
      holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, "card_bg"));
    }
    if(isFirstTimeCall)
    {
      holder.imageFlip.setClickable(false);
      holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, imgName));
      Handler handler = new Handler();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if (listCardData.get(holder.getAdapterPosition()).isImageVisible()) {
            holder.imageFlip.setClickable(false);
            holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, imgName));
          } else {
            holder.imageFlip.setClickable(true);
            holder.imageFlip.setImageDrawable(Utils.GetImage(mContext, "card_bg"));
          }
          isFirstTimeCall=false;
        }
      };
      handler.postDelayed(runnable,1500);
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

  /**
   * Update.
   *
   * @param modelList the model list
   */
  public void update(List<CardData> modelList) {
    listCardData = new ArrayList<>();
    clear();
    for (CardData model : modelList) {
      listCardData.add(model);
//            Log.i("imageName ==>>", model.getCardName());
    }
    notifyDataSetChanged();
  }

  @Override
  public CardData getItem(int position) {
    return listCardData.get(position);
  }

  /**
   *
   * @param flipImage
   * @param card
   * @param v
   */
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

  /**
   * The type View holder.
   */
  public class ViewHolder extends RecyclerView.ViewHolder {
    /**
     * The Image flip.
     */
    @BindView(R.id.img_flipview)
    ImageView imageFlip;

    /**
     * Instantiates a new View holder.
     *
     * @param itemView the item view
     */
    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    /**
     * Bind.
     *
     * @param cardData             the card data
     * @param mOnItemClickListener the m on item click listener
     */
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
