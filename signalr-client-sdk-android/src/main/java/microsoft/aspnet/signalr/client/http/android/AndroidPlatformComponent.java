/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.http.android;

import com.squareup.okhttp.OkHttpClient;

import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.PlatformComponent;
import microsoft.aspnet.signalr.client.http.HttpConnection;

public class AndroidPlatformComponent implements PlatformComponent {

    private OkHttpClient client;

    public AndroidPlatformComponent(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpConnection createHttpConnection(Logger logger) {
        return new AndroidOkHttpConnection(logger, client);
    }

    @Override
    public String getOSName() {
        return "android";
    }

}
