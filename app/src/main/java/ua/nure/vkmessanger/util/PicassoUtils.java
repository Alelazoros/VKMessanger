package ua.nure.vkmessanger.util;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

/**
 * Полезные методы для работы с Picasso.
 */
public class PicassoUtils {

    private static Transformation transformation;

    /**
     * @return объект com.squareup.picasso.Transformation, который позволит
     * преобразовать изображение в форму круга.
     * <p/>
     * Используется библиотека: 'com.makeramen:roundedimageview:2.2.1'
     */
    public static Transformation getCircleTransformation() {
        if (transformation != null) {
            return transformation;
        }
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(45)
                .build();
        return transformation;
    }
}