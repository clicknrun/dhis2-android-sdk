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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryModel;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryOptionHandler;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataset.utils.IdentifiableObjectStore;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class DataSetCall implements Call<Response<Payload<DataSet>>> {
    // retrofit service
    private final DataSetService dataSetService;

    // database adapter and handler
    private final DatabaseAdapter databaseAdapter;
    private final ResourceStore resourceStore;
    private final Date serverDate;
    private final Set<String> uids;
    private boolean isExecuted;

    private final IdentifiableObjectStore<DataSetModel> dataSetStore;
    private final IdentifiableObjectStore<CategoryComboModel> categoryComboStore;
    private final IdentifiableObjectStore<CategoryModel> categoryStore;
    private final IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore;
    private final IdentifiableObjectStore<CategoryOptionComboModel> categoryOptionComboStore;

    public DataSetCall(DataSetService dataSetService,
                       IdentifiableObjectStore<DataSetModel> dataSetStore,
                       IdentifiableObjectStore<CategoryComboModel> categoryComboStore,
                       IdentifiableObjectStore<CategoryModel> categoryStore,
                       IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore,
                       IdentifiableObjectStore<CategoryOptionComboModel> categoryOptionComboStore,
                       DatabaseAdapter databaseAdapter,
                       ResourceStore resourceStore,
                       Set<String> uids,
                       Date serverDate) {
        this.dataSetService = dataSetService;
        this.databaseAdapter = databaseAdapter;
        this.resourceStore = resourceStore;
        this.uids = uids;
        this.serverDate = new Date(serverDate.getTime());

        this.dataSetStore = dataSetStore;
        this.categoryComboStore = categoryComboStore;
        this.categoryStore = categoryStore;
        this.categoryOptionStore = categoryOptionStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
    }


    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<DataSet>> call() throws Exception {
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
        Response<Payload<DataSet>> response = getDataSets(uids);

        if (response != null && response.isSuccessful()) {
            saveDataSets(response);
        }
        return response;
    }

    private Response<Payload<DataSet>> getDataSets(Set<String> uids) throws IOException {
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        String lastSynced = resourceHandler.getLastUpdated(ResourceModel.Type.DATA_SET);

        return dataSetService.getDataSets(getFields(), DataSet.lastUpdated.gt(lastSynced),
                DataSet.uid.in(uids), Boolean.FALSE).execute();
    }

    private void saveDataSets(Response<Payload<DataSet>> response) {
        List<DataSet> dataSets = response.body().items();
        if (dataSets != null && !dataSets.isEmpty()) {
            DataSetHandler dataSetHandler =
                    new DataSetHandler(dataSetStore,
                            new CategoryComboHandler(categoryComboStore,
                                    new CategoryHandler(categoryStore,
                                            new CategoryOptionHandler(categoryOptionStore)),
                                    new CategoryOptionComboHandler(categoryOptionComboStore)));

            ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

            Transaction transaction = databaseAdapter.beginNewTransaction();

            try {
                dataSetHandler.handleMany(dataSets);
                resourceHandler.handleResource(ResourceModel.Type.DATA_SET, serverDate);

                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
    }

    // TODO insert and nest all fields
    private Fields<DataSet> getFields() {
        return Fields.<DataSet>builder().fields(
                DataSet.uid
        ).build();
    }
}
