package com.example.addon.memorygame.Model;

/**
 * Created by Roshan on 12/12/2016.
 */

public class CardData {

    private int id;

    private boolean isFlipped;

    private String cardName;

    private boolean isImageVisible;


    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public boolean isImageVisible() {
        return isImageVisible;
    }

    public void setImageVisible(boolean imageVisible) {
        isImageVisible = imageVisible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CardData{" +
                "isFlipped=" + isFlipped +
                ", cardName='" + cardName + '\'' +
                ", isImageVisible=" + isImageVisible +
                '}';
    }
}
