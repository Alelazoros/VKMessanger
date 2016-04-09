package ua.nure.vkmessanger.http.model;

import android.content.Context;

/**
 * Универсальный тип для хранения данных ответа от запроса к VK API.
 */
public class CustomResponse {

    private Object mAnswer;

    private RequestResult mRequestResult;

    public CustomResponse() {
        mRequestResult = RequestResult.ERROR;
    }

    public RequestResult getRequestResult() {
        return mRequestResult;
    }

    public CustomResponse setRequestResult(RequestResult requestResult) {
        this.mRequestResult = requestResult;
        return this;
    }

    public CustomResponse setAnswer(Object answer) {
        this.mAnswer = answer;
        return this;
    }

    public <T> T getTypedAnswer() {
        if (mAnswer == null) {
            return null;
        }
        return (T) mAnswer;
    }

    public void save(Context context){ }
}
