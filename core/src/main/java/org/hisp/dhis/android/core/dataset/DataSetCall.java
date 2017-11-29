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
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkModel;
import org.hisp.dhis.android.core.category.CategoryComboCategoryLinkModel;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryOptionHandler;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.utils.GenericCallImpl;
import org.hisp.dhis.android.core.dataset.utils.IdentifiableObjectStore;
import org.hisp.dhis.android.core.dataset.utils.ObjectStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import retrofit2.Response;

public class DataSetCall extends GenericCallImpl<DataSet> implements Call<Response<Payload<DataSet>>> {
    private final DataSetService dataSetService;

    public DataSetCall(DataSetService dataSetService,
                       IdentifiableObjectStore<DataSetModel> dataSetStore,
                       IdentifiableObjectStore<CategoryComboModel> categoryComboStore,
                       IdentifiableObjectStore<CategoryModel> categoryStore,
                       IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore,
                       IdentifiableObjectStore<CategoryOptionComboModel> categoryOptionComboStore,
                       ObjectStore<CategoryCategoryOptionLinkModel> categoryCategoryOptionStore,
                       ObjectStore<CategoryComboCategoryLinkModel> categoryComboCategoryStore,
                       ObjectStore<CategoryOptionComboCategoryOptionLinkModel> categoryOptionComboCategoryOptionStore,
                       DatabaseAdapter databaseAdapter,
                       ResourceStore resourceStore,
                       Set<String> uids,
                       Date serverDate) {
        super(databaseAdapter,
                resourceStore,
                new DataSetHandler(dataSetStore,
                        new CategoryComboHandler(categoryComboStore,
                                new CategoryHandler(categoryStore,
                                        categoryCategoryOptionStore,
                                        new CategoryOptionHandler(categoryOptionStore)),
                                new CategoryOptionComboHandler(categoryOptionComboStore))),
                uids, serverDate,
                ResourceModel.Type.DATA_SET);
        this.dataSetService = dataSetService;
    }

    @Override
    protected retrofit2.Call<Payload<DataSet>> getCall(Set<String> uids, Filter<DataSet,
            String> lastUpdated) throws IOException {
        return dataSetService.getDataSets(getFields(), lastUpdated,
                DataSet.uid.in(uids), Boolean.FALSE);
    }

    // TODO insert and nest all fields
    private Fields<DataSet> getFields() {
        return Fields.<DataSet>builder().fields(
                DataSet.uid
        ).build();
    }
}
