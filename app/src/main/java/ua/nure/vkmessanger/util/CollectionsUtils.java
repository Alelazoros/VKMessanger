package ua.nure.vkmessanger.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antony on 5/28/2016.
 */
public class CollectionsUtils {

    public static <T> List<T> copyOf(List<T> list) {
        List<T> copy = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            copy.add(list.get(i));
        }
        return copy;
    }

}