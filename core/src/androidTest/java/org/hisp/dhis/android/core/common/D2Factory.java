package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class D2Factory {
    public static D2 create(String url, DatabaseAdapter databaseAdapter) {
        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse(url))
                .build();

        return new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter)
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                                .build()
                ).build();

    }
}
