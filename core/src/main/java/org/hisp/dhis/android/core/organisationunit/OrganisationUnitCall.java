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
package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.User;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall implements Call<Response<Payload<OrganisationUnit>>> {

    private final User user;
    private final OrganisationUnitService organisationUnitService;
    private final DatabaseAdapter database;
    private final OrganisationUnitHandler organisationUnitHandler;
    private final ResourceHandler resourceHandler;
    private final String uid;

    private final Date serverDate;
    private boolean isExecuted;

    public OrganisationUnitCall(@NonNull User user,
                                @NonNull OrganisationUnitService organisationUnitService,
                                @NonNull DatabaseAdapter database,
                                @NonNull ResourceHandler resourceHandler,
                                @NonNull Date serverDate,
                                @NonNull OrganisationUnitHandler organisationUnitHandler,
                                @NonNull String uid) {
        this.user = user;
        this.organisationUnitService = organisationUnitService;
        this.database = database;
        this.resourceHandler = resourceHandler;
        this.serverDate = new Date(serverDate.getTime());
        this.organisationUnitHandler = organisationUnitHandler;
        this.uid = uid;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<OrganisationUnit>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("AlreadyExecuted");
            }
            isExecuted = true;
        }
        Response<Payload<OrganisationUnit>> response = null;

        Transaction transaction = database.beginNewTransaction();
        try {
            Filter<OrganisationUnit, String> lastUpdatedFilter =
                    OrganisationUnit.lastUpdated.gt(
                            resourceHandler.getLastUpdated(ResourceModel.Type.ORGANISATION_UNIT)
                    );
            // Call OrganisationUnitService for each tree root & try to handleTrackedEntity
            // sub-tree:


            if (uid.isEmpty()) {
                Set<String> rootOrgUnitUids = findRoots(user.organisationUnits());
                for (String uid : rootOrgUnitUids) {
                    response = getOrganisationUnitByUId(uid, lastUpdatedFilter);
                    if (!response.isSuccessful()) {
                        //stop early unsuccessful
                        break;
                    }
                }
            } else {
                response = getOrganisationUnitByUId(uid, lastUpdatedFilter);
            }
            if (response != null && response.isSuccessful()) {
                resourceHandler.handleResource(ResourceModel.Type.ORGANISATION_UNIT, serverDate);
                transaction.setSuccessful();
            }
        } finally {
            transaction.end();
        }
        return response;
    }

    private Response<Payload<OrganisationUnit>> getOrganisationUnitByUId(
            @NonNull String uid,
            @Nullable Filter<OrganisationUnit, String> lastUpdatedFilter) throws IOException {
        Response<Payload<OrganisationUnit>>  response = getOrganisationUnit(uid, lastUpdatedFilter);
        if (response.isSuccessful()) {
            organisationUnitHandler.handleOrganisationUnits(
                    response.body().items(),
                    OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE,
                    user.uid()
            );
        }
        return response;
    }

    private Response<Payload<OrganisationUnit>> getOrganisationUnit(
            @NonNull String uid,
            @Nullable Filter<OrganisationUnit, String> lastUpdatedFilter) throws IOException {

        Fields<OrganisationUnit> fields = Fields.<OrganisationUnit>builder().fields(
                OrganisationUnit.uid, OrganisationUnit.code, OrganisationUnit.name,
                OrganisationUnit.displayName, OrganisationUnit.created, OrganisationUnit.lastUpdated,
                OrganisationUnit.shortName, OrganisationUnit.displayShortName,
                OrganisationUnit.description, OrganisationUnit.displayDescription,
                OrganisationUnit.displayDescription, OrganisationUnit.path, OrganisationUnit.openingDate,
                OrganisationUnit.closedDate, OrganisationUnit.level, OrganisationUnit.deleted,
                OrganisationUnit.parent.with(OrganisationUnit.uid),
                OrganisationUnit.programs.with(Program.uid)
        ).build();
        return organisationUnitService.getOrganisationUnits(uid, fields, lastUpdatedFilter, true, false).execute();
    }
}