package ua.nure.vkmessanger.util;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

/**
 * Полезные методы для работы с Picasso.
 */
public class PicassoUtils {

    /**
     * @return объект com.squareup.picasso.Transformation, который позволит
     * преобразовать изображение в форму круга.
     *
     * Используется библиотека: 'com.makeramen:roundedimageview:2.2.1'
     */
    public static Transformation getCircleTransformation(){
        return new RoundedTransformationBuilder()
                .cornerRadiusDp(45)
                .build();
    }
}
