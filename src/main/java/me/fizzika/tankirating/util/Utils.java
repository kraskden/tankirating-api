package me.fizzika.tankirating.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static <T> List<List<T>> split(List<T> content, int sliceSize) {
        if (sliceSize == 0) {
            return List.of(content);
        }
        var res = new ArrayList<List<T>>();
        for (int sliceStart = 0; sliceStart < content.size(); sliceStart += sliceSize) {
            int sliceEnd = Math.min(sliceStart + sliceSize, content.size());
            res.add(content.subList(sliceStart, sliceEnd));
        }
        return res;
    }

}
