package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.audit.MetadataAuditConnection;
import org.hisp.dhis.android.core.audit.broker.RealBrokerMother;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class D2Factory {
    public static D2 create(String url, DatabaseAdapter databaseAdapter) {
        return create(url, databaseAdapter, Locale.ENGLISH);
    }

    public static D2 create(String url, DatabaseAdapter databaseAdapter, Locale locale) {
        MetadataAuditConnection metadataAuditConnection = MetadataAuditConnection.builder()
                .setHost(RealBrokerMother.hostIP)
                .setVirtualHost(RealBrokerMother.virtualHost)
                .setUsername(RealBrokerMother.user)
                .setPassword(RealBrokerMother.password)
                .setPort(RealBrokerMother.port)
                .build();

        return create(url, databaseAdapter, metadataAuditConnection, locale);
    }

    public static D2 create(String url, DatabaseAdapter databaseAdapter,
            MetadataAuditConnection metadataAuditConnection, Locale locale) {
        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse(url))
                .build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter)
                .translation(locale)
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                                .addInterceptor(loggingInterceptor)
                                .build()
                )
                .metadataAuditConnection(metadataAuditConnection)
                .build();

    }
}
