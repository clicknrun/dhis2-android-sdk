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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectFactory;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetQuery;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitQuery;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramAccessEndpointCall;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramQuery;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoQuery;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityQuery;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCall;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserQuery;
import org.hisp.dhis.android.core.user.UserService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import retrofit2.Response;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
        "PMD.CouplingBetweenObjects",
        "PMD.GodClass"
})
public class MetadataCall implements Call<Response> {
    private final DatabaseAdapter databaseAdapter;
    private final SystemInfoService systemInfoService;
    private final UserService userService;
    private final SystemInfoStore systemInfoStore;
    private final ResourceStore resourceStore;
    private final UserHandler userHandler;

    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityFactory trackedEntityFactory;
    private final CategoryFactory categoryFactory;
    private final ProgramFactory programFactory;
    private final OrganisationUnitFactory organisationUnitFactory;
    private final CategoryComboFactory categoryComboFactory;
    private final DeletedObjectFactory deletedObjectFactory;
    private final DataSetParentCall.Factory dataSetParentCallFactory;

    private final ProgramService programService;

    private boolean isExecuted;
    private final boolean isTranslationOn;
    private final String translationLocale;
    private final GenericCallData genericCallData;

    public MetadataCall(@NonNull DatabaseAdapter databaseAdapter,
                        @NonNull SystemInfoService systemInfoService,
                        @NonNull UserService userService,
                        @Nonnull UserHandler userHandler,
                        @NonNull SystemInfoStore systemInfoStore,
                        @NonNull ResourceStore resourceStore,
                        @NonNull OptionSetFactory optionSetFactory,
                        @NonNull TrackedEntityFactory trackedEntityFactory,
                        @Nonnull ProgramFactory programFactory,
                        @NonNull OrganisationUnitFactory organisationUnitFactory,
                        @NonNull CategoryFactory categoryFactory,
                        @NonNull CategoryComboFactory categoryComboFactory,
                        @NonNull DeletedObjectFactory deletedObjectFactory,
                        @NonNull DataSetParentCall.Factory dataSetParentCallFactory,
                        boolean isTranslationOn,
                        @NonNull String translationLocale,
                        @NonNull ProgramService programService,
                        @NonNull GenericCallData genericCallData) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoService = systemInfoService;
        this.userService = userService;
        this.userHandler = userHandler;
        this.systemInfoStore = systemInfoStore;
        this.resourceStore = resourceStore;

        this.optionSetFactory = optionSetFactory;
        this.trackedEntityFactory = trackedEntityFactory;
        this.programFactory = programFactory;
        this.organisationUnitFactory = organisationUnitFactory;
        this.categoryFactory = categoryFactory;
        this.categoryComboFactory = categoryComboFactory;
        this.deletedObjectFactory = deletedObjectFactory;
        this.dataSetParentCallFactory = dataSetParentCallFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
        this.genericCallData = genericCallData;

        this.programService = programService;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Response response;
        Transaction transaction = databaseAdapter.beginNewTransaction();
        SystemInfoQuery systemInfoQuery = SystemInfoQuery.defaultQuery(isTranslationOn,
                translationLocale);
        try {
            response = new SystemInfoCall(
                    databaseAdapter, systemInfoStore,
                    systemInfoService, resourceStore,
                    systemInfoQuery
            ).call();

            if (!response.isSuccessful()) {
                return response;
            }

            Date serverDate = genericCallData.serverDate();
            UserQuery userQuery = UserQuery.defaultQuery(isTranslationOn,
                    translationLocale);

            Response<User> userResponse = new UserCall(
                    userService,
                    databaseAdapter,
                    userHandler,
                    genericCallData.serverDate(),
                    userQuery
            ).call();
            response = userResponse;

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncCategories(serverDate);

            if (!response.isSuccessful()) {
                return response;
            }

            Response<Payload<Program>> programAccessResponse = ProgramAccessEndpointCall.FACTORY
                    .create(genericCallData, programService).call();
            response = programAccessResponse;

            if (!response.isSuccessful()) {
                return response;
            }

            Set<String> programUids = getProgramUidsWithDataReadAccess(
                    programAccessResponse.body().items());
            Response<Payload<Program>> programsResponse = syncPrograms(serverDate, programUids);
            response = programsResponse;

            if (!response.isSuccessful()) {
                return response;
            }

            List<Program> programs = programsResponse.body().items();
            response = syncTrackedEntities(serverDate, programs);

            if (!response.isSuccessful()) {
                return response;
            }

            User user = userResponse.body();
            OrganisationUnitQuery organisationUnitQuery = OrganisationUnitQuery.defaultQuery(
                    user, isTranslationOn, translationLocale, OrganisationUnitQuery.DEFAULT_UID);
            Response<Payload<OrganisationUnit>> organisationUnitResponse = organisationUnitFactory.newEndPointCall(
                    serverDate, organisationUnitQuery, programUids).call();
            response = organisationUnitResponse;

            if (!response.isSuccessful()) {
                return response;
            }

            response = syncOptionSets(programs);

            if (!response.isSuccessful()) {
                return response;
            }

            List<OrganisationUnit> organisationUnits = organisationUnitResponse.body().items();
            response = dataSetParentCallFactory.create(user, genericCallData, organisationUnits).call();

            if (!response.isSuccessful()) {
                return response;
            }

            DeletedObjectCall deletedObjectCall = new DeletedObjectCall(databaseAdapter,
                    systemInfoService, systemInfoStore, resourceStore, deletedObjectFactory,
                    isTranslationOn, translationLocale);

            response = deletedObjectCall.call();

            if (!response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();
            return response;
        } finally {
            transaction.end();
        }
    }

    private Response syncOptionSets(List<Program> programs) throws Exception {
        Set<String> optionSetUids = getAssignedOptionSetUids(programs);
        OptionSetQuery optionSetQuery = OptionSetQuery.defaultQuery(optionSetUids,
                isTranslationOn, translationLocale);

        Response response = optionSetFactory.newEndPointCall(optionSetQuery).call();

        return response;
    }

    private Response syncTrackedEntities(Date serverDate, List<Program> programs) throws Exception {
        Set<String> trackedEntityUids = getAssignedTrackedEntityUids(programs);

        TrackedEntityQuery trackedEntityQuery = TrackedEntityQuery.defaultQuery(
                trackedEntityUids, isTranslationOn,
                translationLocale);

        Response response = trackedEntityFactory.newEndPointCall(trackedEntityQuery,
                serverDate).call();


        return response;
    }

    private Response syncCategories(Date serverDate)
            throws Exception {

        CategoryQuery categoryQuery = CategoryQuery
                .defaultQuery(isTranslationOn,
                        translationLocale);

        Response response = categoryFactory.newEndPointCall(categoryQuery,
                serverDate).call();

        if (!response.isSuccessful()) {
            return response;
        }
        CategoryComboQuery categoryComboQuery = CategoryComboQuery
                .defaultQuery(isTranslationOn,
                        translationLocale);
        response = categoryComboFactory.newEndPointCall(categoryComboQuery,
                serverDate).call();

        return response;
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private Response<Payload<Program>> syncPrograms(Date serverDate, Set<String> programUids)
            throws Exception {

        ProgramQuery programQuery = ProgramQuery.defaultQuery(programUids, isTranslationOn,
                translationLocale);

        return programFactory.newEndPointCall(programQuery, serverDate).call();
    }

    /// Utilty methods:
    private Set<String> getAssignedOptionSetUids(List<Program> programs) {
        if (programs == null) {
            return null;
        }
        Set<String> uids = new HashSet<>();
        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);

            getOptionSetUidsForAttributes(uids, program);
            getOptionSetUidsForDataElements(uids, program);
        }
        return uids;
    }

    private void getOptionSetUidsForDataElements(Set<String> uids, Program program) {
        List<ProgramStage> programStages = program.programStages();
        int programStagesSize = programStages.size();

        for (int j = 0; j < programStagesSize; j++) {
            ProgramStage programStage = programStages.get(j);
            List<ProgramStageDataElement> programStageDataElements =
                    programStage.programStageDataElements();
            int programStageDataElementSize = programStageDataElements.size();

            for (int k = 0; k < programStageDataElementSize; k++) {
                ProgramStageDataElement programStageDataElement = programStageDataElements.get(k);

                if (programStageDataElement.dataElement() != null &&
                        programStageDataElement.dataElement().optionSet() != null) {
                    uids.add(programStageDataElement.dataElement().optionSet().uid());
                }
            }
        }
    }

    private void getOptionSetUidsForAttributes(Set<String> uids, Program program) {
        int programTrackedEntityAttributeSize = program.programTrackedEntityAttributes().size();
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                program.programTrackedEntityAttributes();

        for (int j = 0; j < programTrackedEntityAttributeSize; j++) {
            ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                    programTrackedEntityAttributes.get(j);

            if (programTrackedEntityAttribute.trackedEntityAttribute() != null &&
                    programTrackedEntityAttribute.trackedEntityAttribute().optionSet() != null) {
                uids.add(programTrackedEntityAttribute.trackedEntityAttribute().optionSet().uid());
            }
        }
    }

    private Set<String> getAssignedTrackedEntityUids(List<Program> programs) {
        if (programs == null) {
            return null;
        }

        Set<String> uids = new HashSet<>();

        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);

            if (program.trackedEntity() != null) {
                uids.add(program.trackedEntity().uid());
            }
        }
        return uids;
    }

    private Set<String> getProgramUidsWithDataReadAccess(List<Program> programsWithAccess) {
        Set<String> programUids = new HashSet<>();
        for (Program program : programsWithAccess) {
            Access access = program.access();
            if (access != null && access.data() != null && access.data().read()) {
                programUids.add(program.uid());
            }
        }
        return programUids;
    }
}
