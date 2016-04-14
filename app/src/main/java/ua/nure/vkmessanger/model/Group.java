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
            group — группа;
            page — публичная страница;
            event — мероприятие.
     */
    private String mType;

    /**
     * Текст описания сообщества.
     */
    private String mDescription;

    public Group(int id, String name, String screenName, String type, String description) {
        this.id = id;
        mName = name;
        mScreenName = screenName;
        mType = type;
        mDescription = description;
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

    public String getDescription() {
        return mDescription;
    }
}
