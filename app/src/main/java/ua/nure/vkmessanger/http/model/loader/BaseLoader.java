package ua.nure.vkmessanger.http.model.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;

import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.http.model.Response;

/**
 * Базовый класс для всех лоадеров в приложении.
 */
public abstract class BaseLoader extends AsyncTaskLoader<Response> {

    public BaseLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Response loadInBackground() {
        try {
            Response response = apiCall();
            if (response.getRequestResult() == RequestResult.SUCCESS){
                response.save(getContext());
                onSuccess();
            }
            else {
                onError();
            }
            return response;
        }
        catch (IOException ex){
            onError();
            return new Response();
        }
    }

    protected void onSuccess(){ }

    protected void onError(){ }

    public abstract Response apiCall() throws IOException;
}
