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

package org.hisp.dhis.android.core;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.audit.MetadataAuditConnection;
import org.hisp.dhis.android.core.audit.MetadataAuditConsumer;
import org.hisp.dhis.android.core.audit.MetadataAuditHandlerFactory;
import org.hisp.dhis.android.core.audit.MetadataAuditListener;
import org.hisp.dhis.android.core.audit.MetadataSyncedListener;
import org.hisp.dhis.android.core.calls.AggregatedDataCall;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.SingleDataCall;
import org.hisp.dhis.android.core.calls.TrackedEntityInstancePostCall;
import org.hisp.dhis.android.core.calls.TrackerDataCall;
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingModel;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingStore;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkModel;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetModel;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.dataset.DataSetStore;
import org.hisp.dhis.android.core.datavalue.DataValueEndpointCall;
import org.hisp.dhis.android.core.datavalue.DataValueModel;
import org.hisp.dhis.android.core.datavalue.DataValueStore;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectFactory;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventPostCall;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkStore;
import org.hisp.dhis.android.core.indicator.IndicatorModel;
import org.hisp.dhis.android.core.indicator.IndicatorStore;
import org.hisp.dhis.android.core.indicator.IndicatorTypeModel;
import org.hisp.dhis.android.core.indicator.IndicatorTypeStore;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.period.PeriodModel;
import org.hisp.dhis.android.core.period.PeriodStore;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.relationship.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.RelationshipStore;
import org.hisp.dhis.android.core.relationship.RelationshipStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipTypeFactory;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.IsUserLoggedInCallable;
import org.hisp.dhis.android.core.user.LogOutUserCallable;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserAuthenticateCall;
import org.hisp.dhis.android.core.user.UserCredentialsHandler;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserQuery;
import org.hisp.dhis.android.core.user.UserRoleHandler;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserRoleStoreImpl;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// ToDo: handle corner cases when user initially has been signed in, but later was locked (or
// password has changed)
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public final class D2 {
    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;

    // services
    private final UserService userService;
    private final SystemInfoService systemInfoService;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final EventService eventService;
    private final ProgramService programService;

    // stores
    private final UserStore userStore;
    private final UserCredentialsStore userCredentialsStore;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final UserRoleStore userRoleStore;
    private final RelationshipStore relationshipStore;
    private final ResourceStore resourceStore;
    private final SystemInfoStore systemInfoStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final EnrollmentStore enrollmentStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final OrganisationUnitStore organisationUnitStore;


    private final IdentifiableObjectStore<DataSetModel> dataSetStore;
    private final ObjectStore<DataSetDataElementLinkModel> dataSetDataElementLinkStore;
    private final ObjectStore<DataSetOrganisationUnitLinkModel> dataSetOrganisationUnitLinkStore;
    private final IdentifiableObjectStore<IndicatorModel> indicatorStore;
    private final IdentifiableObjectStore<IndicatorTypeModel> indicatorTypeStore;
    private final ObjectStore<DataSetIndicatorLinkModel> dataSetIndicatorLinkStore;
    private final ObjectWithoutUidStore<DataValueModel> dataValueStore;
    private final ObjectWithoutUidStore<PeriodModel> periodStore;
    private final ObjectWithoutUidStore<ObjectStyleModel> objectStyleStore;
    private final ObjectWithoutUidStore<ValueTypeDeviceRenderingModel> valueTypeDeviceRenderingStore;

    //Handlers
    private final UserHandler userHandler;
    private final EventHandler eventHandler;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final ResourceHandler resourceHandler;
    private MetadataAuditConsumer metadataAuditConsumer;
    private MetadataAuditListener metadataAuditListener;
    private final DeletedObjectFactory deletedObjectFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    //Factories
    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityFactory trackedEntityFactory;
    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;
    private final ProgramFactory programFactory;
    private final DataElementFactory dataElementFactory;
    private final RelationshipTypeFactory relationshipTypeFactory;
    private final OrganisationUnitFactory organisationUnitFactory;
    private final CategoryFactory categoryFactory;
    private final CategoryComboFactory categoryComboFactory;

    //Generic Call Data
    private final GenericCallData genericCallData;

    @VisibleForTesting
    D2(@NonNull Retrofit retrofit, @NonNull DatabaseAdapter databaseAdapter,
            MetadataAuditConnection metadataAuditConnection,
            boolean isTranslationOn, @NonNull String translationLocale) {
        this.retrofit = retrofit;
        this.databaseAdapter = databaseAdapter;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;

        // services
        this.userService = retrofit.create(UserService.class);
        this.systemInfoService = retrofit.create(SystemInfoService.class);
        this.trackedEntityInstanceService = retrofit.create(TrackedEntityInstanceService.class);
        this.eventService = retrofit.create(EventService.class);
        this.programService = retrofit.create(ProgramService.class);

        // stores
        this.userStore =
                new UserStoreImpl(databaseAdapter);
        this.userCredentialsStore =
                new UserCredentialsStoreImpl(databaseAdapter);
        this.authenticatedUserStore =
                new AuthenticatedUserStoreImpl(databaseAdapter);
        this.resourceStore =
                new ResourceStoreImpl(databaseAdapter);
        this.systemInfoStore =
                new SystemInfoStoreImpl(databaseAdapter);
        this.userRoleStore =
                new UserRoleStoreImpl(databaseAdapter);

        this.eventStore = new EventStoreImpl(databaseAdapter);
        this.trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter);
        this.enrollmentStore = new EnrollmentStoreImpl(databaseAdapter);
        this.trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter);
        this.trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStoreImpl(databaseAdapter);
        this.organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter);

        this.dataSetStore = DataSetStore.create(databaseAdapter());
        this.dataSetDataElementLinkStore = DataSetDataElementLinkStore.create(databaseAdapter());
        this.dataSetOrganisationUnitLinkStore = DataSetOrganisationUnitLinkStore.create(databaseAdapter());
        this.indicatorStore = IndicatorStore.create(databaseAdapter());
        this.indicatorTypeStore = IndicatorTypeStore.create(databaseAdapter());
        this.dataSetIndicatorLinkStore = DataSetIndicatorLinkStore.create(databaseAdapter());
        this.dataValueStore = DataValueStore.create(databaseAdapter());
        this.periodStore = PeriodStore.create(databaseAdapter());
        this.objectStyleStore = ObjectStyleStore.create(databaseAdapter());
        this.valueTypeDeviceRenderingStore = ValueTypeDeviceRenderingStore.create(databaseAdapter());

        //handlers
        this.resourceHandler = new ResourceHandler(resourceStore);
        UserRoleHandler userRoleHandler = new UserRoleHandler(userRoleStore);
        UserCredentialsHandler userCredentialsHandler = new UserCredentialsHandler(
                userCredentialsStore);
        this.userHandler = new UserHandler(userStore, userCredentialsHandler, resourceHandler,
                userRoleHandler);

        TrackedEntityDataValueHandler trackedEntityDataValueHandler =
                new TrackedEntityDataValueHandler(trackedEntityDataValueStore);

        this.eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);

        TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler =
                new TrackedEntityAttributeValueHandler(trackedEntityAttributeValueStore);

        EnrollmentHandler enrollmentHandler = new EnrollmentHandler(enrollmentStore, eventHandler);
        relationshipStore = new RelationshipStoreImpl(databaseAdapter);
        RelationshipHandler relationshipHandler = new RelationshipHandler(relationshipStore,
                trackedEntityInstanceStore);

        this.trackedEntityInstanceHandler =
                new TrackedEntityInstanceHandler(
                        trackedEntityInstanceStore,
                        trackedEntityAttributeValueHandler,
                        enrollmentHandler, relationshipHandler);

        this.genericCallData = GenericCallData.create(databaseAdapter, new ResourceHandler(resourceStore), retrofit);

        //factories
        this.optionSetFactory = new OptionSetFactory(genericCallData);

        this.trackedEntityFactory =
                new TrackedEntityFactory(retrofit, databaseAdapter, resourceHandler);

        this.organisationUnitFactory =
                new OrganisationUnitFactory(retrofit, databaseAdapter, resourceHandler);

        this.trackedEntityAttributeFactory = new TrackedEntityAttributeFactory(
                retrofit, databaseAdapter, resourceHandler);

        this.dataElementFactory = new DataElementFactory(retrofit, databaseAdapter,
                resourceHandler);

        this.programFactory = new ProgramFactory(retrofit, databaseAdapter,
                optionSetFactory.getOptionSetHandler(), dataElementFactory, resourceHandler);

        this.relationshipTypeFactory =
                new RelationshipTypeFactory(retrofit, databaseAdapter, resourceHandler);

        this.categoryFactory = new CategoryFactory(retrofit(), databaseAdapter, resourceHandler);

        this.categoryComboFactory = new CategoryComboFactory(retrofit(), databaseAdapter,
                resourceHandler);

        this.deletedObjectFactory = new DeletedObjectFactory(retrofit, databaseAdapter,
                resourceHandler);

        if (metadataAuditConnection != null) {
            MetadataAuditHandlerFactory metadataAuditHandlerFactory =
                    new MetadataAuditHandlerFactory(trackedEntityFactory, optionSetFactory,
                            dataElementFactory, trackedEntityAttributeFactory, programFactory,
                            relationshipTypeFactory, organisationUnitFactory, categoryFactory,
                            categoryComboFactory, isTranslationOn, translationLocale);

            this.metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
            this.metadataAuditConsumer = new MetadataAuditConsumer(metadataAuditConnection);
            this.metadataAuditConsumer.setMetadataAuditListener(metadataAuditListener);
        }
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    public DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    @NonNull
    public Call<Response<User>> logIn(@NonNull String username, @NonNull String password) {
        if (username == null) {
            throw new NullPointerException("username == null");
        }
        if (password == null) {
            throw new NullPointerException("password == null");
        }

        UserQuery userQuery = UserQuery.defaultQuery(isTranslationOn, translationLocale);
        return new UserAuthenticateCall(userService, databaseAdapter, userHandler,
                authenticatedUserStore, organisationUnitFactory.getOrganisationUnitHandler()
                , username, password, userQuery
        );
    }

    @NonNull
    public Callable<Boolean> isUserLoggedIn() {
        AuthenticatedUserStore authenticatedUserStore =
                new AuthenticatedUserStoreImpl(databaseAdapter);

        return new IsUserLoggedInCallable(authenticatedUserStore);
    }

    @NonNull
    public Callable<Void> logout() {
        List<DeletableStore> deletableStoreList = new ArrayList<>();
        deletableStoreList.add(authenticatedUserStore);
        return new LogOutUserCallable(
                deletableStoreList
        );
    }

    @NonNull
    public Callable<Void> wipeDB() {
        List<DeletableStore> deletableStoreList = new ArrayList<>();
        deletableStoreList.add(userStore);
        deletableStoreList.add(userCredentialsStore);
        deletableStoreList.add(authenticatedUserStore);
        deletableStoreList.add(resourceStore);
        deletableStoreList.add(systemInfoStore);
        deletableStoreList.add(userRoleStore);
        deletableStoreList.add(relationshipStore);
        deletableStoreList.add(trackedEntityInstanceStore);
        deletableStoreList.add(enrollmentStore);
        deletableStoreList.add(trackedEntityDataValueStore);
        deletableStoreList.add(trackedEntityAttributeValueStore);
        deletableStoreList.add(eventStore);

        deletableStoreList.addAll(trackedEntityFactory.getDeletableStores());
        deletableStoreList.addAll(trackedEntityAttributeFactory.getDeletableStores());
        deletableStoreList.addAll(optionSetFactory.getDeletableStores());
        deletableStoreList.addAll(programFactory.getDeletableStores());
        deletableStoreList.addAll(dataElementFactory.getDeletableStores());
        deletableStoreList.addAll(relationshipTypeFactory.getDeletableStores());
        deletableStoreList.addAll(organisationUnitFactory.getDeletableStores());
        deletableStoreList.addAll(categoryFactory.getDeletableStores());
        deletableStoreList.addAll(categoryComboFactory.getDeletableStores());

        deletableStoreList.add(dataSetStore);
        deletableStoreList.add(dataSetDataElementLinkStore);
        deletableStoreList.add(dataSetOrganisationUnitLinkStore);
        deletableStoreList.add(indicatorStore);
        deletableStoreList.add(indicatorTypeStore);
        deletableStoreList.add(dataSetIndicatorLinkStore);
        deletableStoreList.add(dataValueStore);
        deletableStoreList.add(periodStore);
        deletableStoreList.add(objectStyleStore);
        deletableStoreList.add(valueTypeDeviceRenderingStore);
        return new LogOutUserCallable(deletableStoreList);
    }

    @NonNull
    public Call<Response> syncMetaData() {
        return new MetadataCall(
                databaseAdapter, systemInfoService, userService, userHandler, systemInfoStore,
                resourceStore, optionSetFactory, trackedEntityFactory, programFactory,
                organisationUnitFactory, categoryFactory, categoryComboFactory,
                deletedObjectFactory, DataSetParentCall.FACTORY, isTranslationOn, translationLocale, programService,
                genericCallData);
    }

    @NonNull
    public Call<Response> syncAggregatedData() {
        return new AggregatedDataCall(genericCallData, DataValueEndpointCall.FACTORY, dataSetStore, periodStore,
                organisationUnitStore);
    }

    @NonNull
    public Call<Response> syncSingleData(int eventLimitByOrgUnit) {
        return new SingleDataCall(organisationUnitFactory.getOrganisationUnitStore(),
                systemInfoStore, systemInfoService, resourceStore,
                eventService, databaseAdapter, resourceHandler, eventHandler, eventLimitByOrgUnit,
                isTranslationOn, translationLocale);
    }

    @NonNull
    public Call<Response> syncTrackerData() {
        return new TrackerDataCall(trackedEntityInstanceStore, systemInfoStore, systemInfoService,
                resourceStore, trackedEntityInstanceService, databaseAdapter, resourceHandler,
                trackedEntityInstanceHandler, isTranslationOn, translationLocale);
    }

    @NonNull
    public Call<Response<WebResponse>> syncTrackedEntityInstances() {
        return new TrackedEntityInstancePostCall(trackedEntityInstanceService,
                trackedEntityInstanceStore, enrollmentStore, eventStore,
                trackedEntityDataValueStore,
                trackedEntityAttributeValueStore);
    }

    public Call<Response<WebResponse>> syncSingleEvents() {
        return new EventPostCall(eventService, eventStore, trackedEntityDataValueStore);
    }

    public void startListeningSyncedMetadata(MetadataSyncedListener metadataSyncedListener)
            throws Exception {
        metadataAuditListener.setMetadataSyncedListener(metadataSyncedListener);
        metadataAuditConsumer.start();
    }

    public void stopListeningSyncedMetadata() throws Exception {
        metadataAuditConsumer.stop();
    }

    public static class Builder {
        private ConfigurationModel configuration;
        private DatabaseAdapter databaseAdapter;
        private OkHttpClient okHttpClient;
        private MetadataAuditConnection metadataAuditConnection;
        private boolean isTranslationOn;
        private Locale translationLocale = Locale.ENGLISH;


        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configuration(@NonNull ConfigurationModel configuration) {
            this.configuration = configuration;
            return this;
        }

        @NonNull
        public Builder databaseAdapter(@NonNull DatabaseAdapter databaseAdapter) {
            this.databaseAdapter = databaseAdapter;
            return this;
        }

        @NonNull
        public Builder okHttpClient(@NonNull OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        @NonNull
        public Builder translation(@NonNull Locale locale) {
            this.isTranslationOn = true;
            this.translationLocale = locale;
            return this;
        }

        @NonNull
        public Builder metadataAuditConnection(MetadataAuditConnection metadataAuditConnection) {
            this.metadataAuditConnection = metadataAuditConnection;
            return this;
        }

        public D2 build() {
            if (databaseAdapter == null) {
                throw new IllegalArgumentException("databaseAdapter == null");
            }

            if (configuration == null) {
                throw new IllegalStateException("configuration must be set first");
            }

            if (okHttpClient == null) {
                throw new IllegalArgumentException("okHttpClient == null");
            }

            ObjectMapper objectMapper = new ObjectMapper()
                    .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(configuration.serverUrl())
                    .client(okHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .addConverterFactory(FilterConverterFactory.create())
                    .addConverterFactory(FieldsConverterFactory.create())
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, databaseAdapter, metadataAuditConnection,
                    isTranslationOn, translationLocale.toString());
        }
    }
}