/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.http.android;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import microsoft.aspnet.signalr.client.Constants;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.http.HttpConnection;
import microsoft.aspnet.signalr.client.http.HttpConnectionFuture;
import microsoft.aspnet.signalr.client.http.HttpConnectionFuture.ResponseCallback;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.StreamResponse;

/**
 * Android HttpConnection implementation, based on AndroidHttpClient and
 * AsyncTask for async operations
 */
public class AndroidOkHttpConnection implements HttpConnection {
    private Logger mLogger;
    private OkHttpClient client;

    /**
     * Initializes the AndroidHttpConnection
     *
     * @param logger logger to log activity
     */
    public AndroidOkHttpConnection(Logger logger, OkHttpClient client) {
        if (logger == null) {
            throw new IllegalArgumentException("logger");
        }

        this.client = client;
        mLogger = logger;
    }

    @Override
    public HttpConnectionFuture execute(final Request request, final ResponseCallback responseCallback) {

        mLogger.log("Create new AsyncTask for HTTP Connection", LogLevel.Verbose);

        final HttpConnectionFuture future = new HttpConnectionFuture();

        com.squareup.okhttp.Request okHttpRequest = createRequest(request);

        final Call call = client.newCall(okHttpRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                mLogger.log("Error executing request: " + e.getMessage(), LogLevel.Critical);
                future.triggerError(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                mLogger.log("Request executed", LogLevel.Verbose);

                InputStream bodyStream = response.body().byteStream();
                Map<String, List<String>> headersMap = response.headers().toMultimap();
                try {
                    responseCallback.onResponse(new StreamResponse(bodyStream, response.code(), headersMap));
                    future.setResult(null);
                } catch (Exception e) {
                    mLogger.log("Error calling onResponse: " + e.getMessage(), LogLevel.Critical);
                    future.triggerError(e);
                }
            }
        });

        future.onCancelled(new Runnable() {
            @Override
            public void run() {
                call.cancel();
            }
        });

        return future;
    }

    private static com.squareup.okhttp.Request createRequest(Request request) {
        com.squareup.okhttp.Request.Builder okHttpRequestBuilder = new com.squareup.okhttp.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());
        switch (request.getVerb()) {
            case Constants.HTTP_GET:
                okHttpRequestBuilder.get();
                break;
            case Constants.HTTP_POST:
                okHttpRequestBuilder.post(RequestBody.create(null, request.getContent()));
                break;
            default:
                throw new IllegalArgumentException(String.format("%s is not GET or POST", request.getVerb()));
        }

        for (String key : request.getHeaders().keySet()) {
            okHttpRequestBuilder.addHeader(key, request.getHeaders().get(key));
        }

        return okHttpRequestBuilder.build();
    }
}
