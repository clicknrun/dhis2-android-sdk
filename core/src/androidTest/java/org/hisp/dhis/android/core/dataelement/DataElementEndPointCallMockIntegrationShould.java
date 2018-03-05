package org.hisp.dhis.android.core.dataelement;

import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;

public class DataElementEndPointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private DataElementFactory dataElementFactory;


    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        dataElementFactory = new DataElementFactory(
                d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void download_RelationShipTypes_according_to_default_query() throws Exception {
        String filename = "data_elements.json";

        dhis2MockServer.enqueueMockResponse(filename);

        Set<String> uidsSet =
                new HashSet<>(Arrays.asList("FTRrcoaog83", "P+-3jJH5Tu5VC", "FQ2o8UBlcrS"));

        DataElementQuery dataElementQuery = DataElementQuery.defaultQuery(uidsSet,
                DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE);

        dataElementFactory.newEndPointCall(dataElementQuery, new Date()).call();

        verifyDownloadedDataElements(filename);
    }

    private void verifyDownloadedDataElements(String fileName) throws IOException {
        Payload<DataElement> DataElementPayload = parseDataElementsResponse(fileName);

        List<DataElementModel> downloadedDataElements =
                dataElementFactory.getDataElementStore().queryAll();

        assertThat(downloadedDataElements.size(), is(DataElementPayload.items().size()));

        for (int i = 0; i < downloadedDataElements.size(); i++) {
            DataElement payloadItem = DataElementPayload.items().get(i);
            DataElementModel downloadedModel = downloadedDataElements.get(i);
            assertThat(downloadedModel, is(DataElementModel.factory.fromPojo(payloadItem)));
        }
    }

    private Payload<DataElement> parseDataElementsResponse(String fileName)
            throws IOException {
        String expectedDataElementResponseJson = new AssetsFileReader().getStringFromFile(
                fileName);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());
        return objectMapper.readValue(expectedDataElementResponseJson,
                new TypeReference<Payload<DataElement>>() {
                });
    }
}