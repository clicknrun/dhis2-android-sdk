/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.common;

import android.database.sqlite.SQLiteStatement;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectFactory;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectService;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould {
    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private Transaction transaction;

    @Mock
    private SQLiteStatement sqliteStatement;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<SystemInfo> systemInfoCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> userCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OrganisationUnit>> organisationUnitCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<DeletedObject>> deletableObjectCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programWithAccessCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OptionSet>> optionSetCall;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private SystemInfoService systemInfoService;

    @Mock
    private SystemInfoStore systemInfoStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private UserHandler userHandler;

    @Mock
    private UserService userService;

    @Mock
    private OrganisationUnitService organisationUnitService;

    @Mock
    private OptionSetService optionSetService;

    @Mock
    private ProgramService programService;

    @Mock
    private DeletedObjectService deletedObjectService;

    @Mock
    private Date serverDateTime;

    @Mock
    private User user;
    @Mock
    private DeletedObject deletedObject;

    @Mock
    private DataElement dataElement;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private Program programWithAccess;

    @Mock
    private Program program;

    @Mock
    private Payload<OrganisationUnit> organisationUnitPayload;

    @Mock
    private Payload<Program> programWithAccessPayload;

    @Mock
    private Payload<DeletedObject> deletedObjectPayload;

    @Mock
    private Payload<DataElement> dataElementPayload;

    @Mock
    private DataElementFactory dataElementFactory;

    @Mock
    private DataSetParentCall.Factory dataSetParentCallFactory;

    @Mock
    private Call<Response> dataSetParentCall;


    // object to test
    private MetadataCall metadataCall;

    private Response errorResponse;

    private Dhis2MockServer dhis2MockServer;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
        when(databaseAdapter.compileStatement(anyString())).thenReturn(sqliteStatement);

        when(systemInfoService.getSystemInfo(any(Fields.class), anyBoolean(),
                anyString())).thenReturn(systemInfoCall);

        when(deletedObjectService.getDeletedObjectsDeletedAt(any(Fields.class), anyBoolean(),
                anyString(), anyString())).thenReturn(deletableObjectCall);

        when(userService.getUser(any(Fields.class), anyBoolean(), anyString())).thenReturn(
                userCall);

        when(deletedObjectService.getDeletedObjectsDeletedAt(any(Fields.class), anyBoolean(),
                anyString(), anyString()))
                .thenReturn(deletableObjectCall);

        when(organisationUnitService.getOrganisationUnits(
                anyString(), any(Fields.class), any(Filter.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyString())
        ).thenReturn(organisationUnitCall);

        when(programService.getProgramsForAccess(any(Fields.class), any(Filter.class), anyBoolean())
        ).thenReturn(programWithAccessCall);

        when(optionSetService.optionSets(
                anyBoolean(), any(Fields.class), any(Filter.class), anyBoolean(), anyString())
        ).thenReturn(optionSetCall);

        when(deletedObjectService.getDeletedObjectsDeletedAt(any(Fields.class), anyBoolean(),
                anyString(), anyString())).thenReturn(deletableObjectCall);


        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(deletedObject.uid()).thenReturn("uid");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));
        when(organisationUnitPayload.items()).thenReturn(
                Collections.singletonList(organisationUnit));
        when(deletedObjectPayload.items()).thenReturn(Collections.singletonList(deletedObject));

        when(dataAccess.read()).thenReturn(true);
        when(access.data()).thenReturn(dataAccess);
        when(programWithAccess.access()).thenReturn(access);

        when(program.access()).thenReturn(access);
        when(programWithAccessPayload.items()).thenReturn(Collections.singletonList(programWithAccess));
        when(dataElementPayload.items()).thenReturn(Collections.singletonList(dataElement));

        when(resourceStore.getLastUpdated(any(ResourceModel.Type.class))).thenReturn("2017-01-01");

        when(dataSetParentCallFactory.create(any(User.class), any(GenericCallData.class), any(List.class)))
                .thenReturn(dataSetParentCall);
        Response<Payload<DataElement>> dataSetParentCallResponse = Response.success(dataElementPayload);
        when(dataSetParentCall.call()).thenReturn(dataSetParentCallResponse);

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        GenericCallData genericCallData = GenericCallData.create(databaseAdapter, resourceHandler, retrofit);
        OptionSetFactory optionSetFactory = new OptionSetFactory(genericCallData);
        TrackedEntityFactory trackedEntityFactory = new TrackedEntityFactory(retrofit,
                databaseAdapter, resourceHandler);
        dataElementFactory = new DataElementFactory(retrofit, databaseAdapter, resourceHandler);
        ProgramFactory programFactory = new ProgramFactory(retrofit, databaseAdapter,
                optionSetFactory.
                        getOptionSetHandler(), dataElementFactory, resourceHandler);

        OrganisationUnitFactory organisationUnitFactory = new OrganisationUnitFactory(retrofit,
                databaseAdapter,
                resourceHandler);

        CategoryFactory categoryFactory = new CategoryFactory(retrofit, databaseAdapter,
                resourceHandler);
        CategoryComboFactory categoryComboFactory = new CategoryComboFactory(retrofit,
                databaseAdapter, resourceHandler);
        DeletedObjectFactory deletedObjectFactory = new DeletedObjectFactory(retrofit,
                databaseAdapter, resourceHandler);

        metadataCall = new MetadataCall(
                databaseAdapter, systemInfoService, userService, userHandler,
                systemInfoStore, resourceStore,
                optionSetFactory, trackedEntityFactory, programFactory, organisationUnitFactory,
                categoryFactory, categoryComboFactory, deletedObjectFactory,
                dataSetParentCallFactory,
                DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE,
                programService, genericCallData);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));
        when(userCall.execute()).thenReturn(Response.success(user));
        when(programWithAccessCall.execute()).thenReturn(Response.success(programWithAccessPayload));
        when(deletableObjectCall.execute()).thenReturn(Response.success(deletedObjectPayload));
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_system_info_call_fail() throws Exception {
        final int expectedTransactions = 1;
        when(systemInfoCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, times(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_user_call_fail() throws Exception {
        final int expectedTransactions = 2;
        when(userCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions
                - 1)).setSuccessful();//ie last one is not marked as success...
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_organisation_unit_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("empty_categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 8;
        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);

        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions
                - 1)).setSuccessful(); //taking in account the sub-transactions
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_program_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("empty_categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 5;

        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_tracked_entity_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("empty_categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 7;

        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_option_set_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("empty_categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 8;
        when(optionSetCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }
}
