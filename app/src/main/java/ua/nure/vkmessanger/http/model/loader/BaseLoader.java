package ua.nure.vkmessanger.http.model.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;

/**
 * Базовый класс для всех лоадеров в приложении.
 */
public abstract class BaseLoader extends AsyncTaskLoader<CustomResponse> {

    private Context context;

    public BaseLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public CustomResponse loadInBackground() {
        try {
            CustomResponse response = apiCall();
            if (response.getRequestResult() == RequestResult.SUCCESS) {
                response.save(getContext());
                onSuccess();
            } else {
                onError();
            }
            return response;
        } catch (ConnectException ex) {
            Toast.makeText(context, R.string.internet_connection_exception, Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception ex) {
            onError();
        }
        return new CustomResponse();
    }

    protected void onSuccess() { }

    protected void onError() { }

    public abstract CustomResponse apiCall() throws IOException;
}