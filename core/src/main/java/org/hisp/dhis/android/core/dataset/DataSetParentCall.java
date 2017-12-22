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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboEndpointCall;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericEndpointCallImpl;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElementEndpointCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserRole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class DataSetParentCall implements Call<Response> {
    private boolean isExecuted;

    private final User user;
    private final GenericCallData data;

    public DataSetParentCall(@NonNull User user,
                             @NonNull GenericCallData data) {
        this.data = data;
        this.user = user;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Transaction transaction = data.databaseAdapter().beginNewTransaction();
        try {
            Response<Payload<DataSet>> dataSetResponse = handleEndpointCall(
                    DataSetEndpointCall.create(data, getAssignedDataSetUids(user)));

            List<DataSet> dataSets = dataSetResponse.body().items();
            handleEndpointCall(DataElementEndpointCall.create(data,
                    getDataElementUids(dataSets)));

            Response<Payload<CategoryCombo>> categoryComboResponse =
                    handleEndpointCall(CategoryComboEndpointCall.create(data,
                            getCategoryComboUids(dataSets)));

            List<CategoryCombo> categoryCombos = categoryComboResponse.body().items();
            Response<Payload<Category>> categoryResponse =
                    handleEndpointCall(CategoryEndpointCall.create(data,
                            getCategoryUids(categoryCombos)));
            
            transaction.setSuccessful();
            return categoryResponse;
        }
        finally {
            transaction.end();
        }
    }

    private <M extends BaseIdentifiableObject> Response<Payload<M>>
        handleEndpointCall(GenericEndpointCallImpl<M> endpointCall)
            throws Exception {
        Response<Payload<M>> response = endpointCall.call();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unsuccessful call: " + endpointCall);
        } else {
            return response;
        }
    }

    private Set<String> getAssignedDataSetUids(User user) {
        if (user == null || user.userCredentials() == null || user.userCredentials().userRoles() == null) {
            return null;
        }

        Set<String> dataSetUids = new HashSet<>();

        getDataSetUidsFromUserRoles(user, dataSetUids);
        getDataSetUidsFromOrganisationUnits(user, dataSetUids);

        return dataSetUids;
    }

    private void getDataSetUidsFromOrganisationUnits(User user, Set<String> dataSetUids) {
        List<OrganisationUnit> organisationUnits = user.organisationUnits();

        if (organisationUnits != null) {
            for (OrganisationUnit organisationUnit : organisationUnits) {
                addDataSets(organisationUnit.dataSets(), dataSetUids);
            }
        }
    }

    private void getDataSetUidsFromUserRoles(User user, Set<String> dataSetUids) {
        List<UserRole> userRoles = user.userCredentials().userRoles();

        if (userRoles != null) {
            for (UserRole userRole : userRoles) {
                addDataSets(userRole.dataSets(), dataSetUids);
            }
        }
    }

    private void addDataSets(List<DataSet> dataSets, Set<String> dataSetUids) {
        if (dataSets != null) {
            for (DataSet dataSet : dataSets) {
                dataSetUids.add(dataSet.uid());
            }
        }
    }

    private Set<String> getCategoryComboUids(List<DataSet> dataSets) {
        Set<String> uids = new HashSet<>();
        for (DataSet dataSet : dataSets) {
            uids.add(dataSet.categoryComboUid());
            for (DataElementCategoryCombo dataSetElement : dataSet.dataSetElements()) {
                uids.add(dataSetElement.categoryComboUid());
            }
        }
        return uids;
    }

    private Set<String> getDataElementUids(List<DataSet> dataSets) {
        Set<String> uids = new HashSet<>();
        for (DataSet dataSet : dataSets) {
            for (DataElementCategoryCombo dataSetElement : dataSet.dataSetElements()) {
                uids.add(dataSetElement.dataElement().uid());
            }
        }
        return uids;
    }

    private Set<String> getCategoryUids(List<CategoryCombo> categoryCombos) {
        Set<String> uids = new HashSet<>();
        for (CategoryCombo cc : categoryCombos) {
            for (Category c : cc.categories()) {
                uids.add(c.uid());
            }
        }
        return uids;
    }
}
