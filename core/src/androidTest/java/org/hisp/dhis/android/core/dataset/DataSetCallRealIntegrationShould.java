package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataSetCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private DataSetCall dataSetCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());
        dataSetCall = createCall();
    }

    private DataSetCall createCall() {
        DataSetService dataSetService =
                d2.retrofit().create(DataSetService.class);

        GenericHandler<DataSet, DataSetModel> dataSetHandler =
                DataSetHandler.create(databaseAdapter());

        ResourceHandler resourceHandler =
                new ResourceHandler(new ResourceStoreImpl(databaseAdapter()));
        GenericCallData data = GenericCallData.create(databaseAdapter(), resourceHandler);

        Set<String> uids = new HashSet<String>();

        uids.add("BfMAe6Itzgt");
        uids.add("Lpw6GcnTrmS");
        uids.add("TuL8IOPzpHh");

        return new DataSetCall(data, dataSetService, dataSetHandler, uids);
    }

    @Test
    public void metadataSyncTest() throws Exception {
        retrofit2.Response loginResponse = d2.logIn("android", "Android123").call();
        assertThat(loginResponse.isSuccessful()).isTrue();

        retrofit2.Response dataSetResponse = dataSetCall.call();
        assertThat(dataSetResponse.isSuccessful()).isTrue();

        //TODO: add aditional sync + break point.
        //when debugger stops at the new break point manually change metadata online & resume.
        //This way I can make sure that additive (updates) work as well.
        //The changes could be to one of the programs, adding stuff to it.
        // adding a new program..etc.
    }
}
