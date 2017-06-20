package com.example.addon.memorygame.Adapter;

import java.util.List;

/**
 * Created by Roshan on 12/12/2016.
 */

public interface AdapterOperation<T> {

     void addAll(List<T> mListItems);

     void add(T dataModel) ;

     boolean isEmpty();

     void remove(T item) ;

     void clear() ;

    T getItem(int position);
}
