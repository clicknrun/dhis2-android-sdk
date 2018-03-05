package org.hisp.dhis.android.core.datavalue;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getDataSetUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getOrgUnitUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getPeriodIds;

@RunWith(AndroidJUnit4.class)
public class DataValueEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private DataValueEndpointCall dataValueCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/dev/api/", databaseAdapter());
        dataValueCall = createCall();
    }

    private DataValueEndpointCall createCall() {
        ResourceHandler resourceHandler =
                new ResourceHandler(new ResourceStoreImpl(databaseAdapter()));
        GenericCallData data = GenericCallData.create(databaseAdapter(), resourceHandler, d2.retrofit());

        return DataValueEndpointCall.FACTORY.create(data, getDataSetUids(), getPeriodIds(), getOrgUnitUids());
    }

    // @Test
    public void download_data_values() throws Exception {
        if (!d2.isUserLoggedIn().call()) {
            retrofit2.Response loginResponse = d2.logIn("android", "Android123").call();
            assertThat(loginResponse.isSuccessful()).isTrue();
        }

        /*  This test won't pass independently of the sync of metadata, as the foreign keys
            constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        retrofit2.Response dataValueResponse = dataValueCall.call();
        assertThat(dataValueResponse.isSuccessful()).isTrue();
    }

    @Test
    public void stub() {
    }
}
