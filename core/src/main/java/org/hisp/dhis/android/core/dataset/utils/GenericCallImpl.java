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

package org.hisp.dhis.android.core.dataset.utils;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public abstract class GenericCallImpl<P extends BaseIdentifiableObject> implements Call<Response<Payload<P>>> {
    private DatabaseAdapter databaseAdapter;
    private ResourceHandler resourceHandler;
    private GenericHandler<P, ?> handler;
    private Date serverDate;
    private Set<String> uids;
    private boolean isExecuted;
    private ResourceModel.Type resourceType;

    public GenericCallImpl(DatabaseAdapter databaseAdapter,
                           ResourceStore resourceStore,
                           GenericHandler<P, ?> handler,
                           Set<String> uids,
                           Date serverDate,
                           ResourceModel.Type resourceType) {
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = new ResourceHandler(resourceStore);
        this.uids = uids;
        this.serverDate = new Date(serverDate.getTime());
        this.resourceType = resourceType;
        this.handler = handler;
    }

    @Override
    public final boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public final Response<Payload<P>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalArgumentException("Already executed");
            }

            isExecuted = true;
        }

        if (uids.size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of objects: " + uids.size() + ". " + "Max size is: " + MAX_UIDS);
        }

        Filter<DataSet, String> lastSynced =
                DataSet.lastUpdated.gt(resourceHandler.getLastUpdated(resourceType));
        Response<Payload<P>> response = getCall(uids, lastSynced).execute();

        if (response != null && response.isSuccessful()) {
            processResponse(response);
        }
        return response;
    }

    protected abstract retrofit2.Call<Payload<P>> getCall(Set<String> uids,
                                                          Filter<DataSet, String> lastUpdated) throws IOException;

    private void processResponse(Response<Payload<P>> response) {
        List<P> pojoList = response.body().items();
        if (pojoList != null && !pojoList.isEmpty()) {
            Transaction transaction = databaseAdapter.beginNewTransaction();

            try {
                handler.handleMany(pojoList);
                resourceHandler.handleResource(resourceType, serverDate);

                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
    }
}
