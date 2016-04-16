package ua.nure.vkmessanger.model;

/**
 * Объект, описывающий сообщество ВК.
 * https://vk.com/dev/fields_groups
 *
 * Данный класс - это упрощенная модель оригинального типа данных Group из VK API,
 * т.к. большинство полей в данном приложении не нужны из-за отсутствия необходимости
 * работы непосредственно с сообществами.
 *
 * Данный класс должен использоваться лишь для того, чтобы вывести краткую информацию (фото, название)
 * о сообществе при отображении записи на стене, которая находится в выбранном диалоге.
 */
public class Group {

    private int id;

    /**
     * Название сообщества.
     */
    private String mName;

    /**
     * Короткий адрес сообщества, например, tproger.
     */
    private String mScreenName;

    /**
     * тип сообщества:
     * group — группа;
     * page — публичная страница;
     * event — мероприятие.
     */
    private String mType;

    /**
     * URL на фотографии в различных размерах.
     * В ответе не обязательно должны быть все размеры!
     * <p>
     * photo50 - url фотографии сообщества с размером 50x50px.
     * photo100 - url фотографии сообщества с размером 100x100px.
     * photo100 - url фотографии сообщества в максимальном размере.
     */
    private String mPhoto50;
    private String mPhoto100;
    private String mPhoto200;


    public Group(int id, String name, String screenName, String type, String photo50, String photo100, String photo200) {
        this.id = id;
        mName = name;
        mScreenName = screenName;
        mType = type;
        mPhoto50 = photo50;
        mPhoto100 = photo100;
        mPhoto200 = photo200;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getType() {
        return mType;
    }

    public String getPhotoURL() {
        if (mPhoto200 != null)
            return mPhoto200;
        else if (mPhoto100 != null)
            return mPhoto100;
        return mPhoto50;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", mName='" + mName + '\'' +
                ", mScreenName='" + mScreenName + '\'' +
                ", mType='" + mType + '\'' +
                ", mPhoto50='" + mPhoto50 + '\'' +
                ", mPhoto100='" + mPhoto100 + '\'' +
                ", mPhoto200='" + mPhoto200 + '\'' +
                '}';
    }
}
