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
import org.hisp.dhis.android.core.calls.TransactionalCall;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementEndpointCall;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorEndpointCall;
import org.hisp.dhis.android.core.indicator.IndicatorTypeEndpointCall;
import org.hisp.dhis.android.core.user.User;

import java.util.List;

import retrofit2.Response;

import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getAssignedDataSetUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getDataElementUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getIndicatorTypeUids;
import static org.hisp.dhis.android.core.dataset.DataSetParentUidsHelper.getIndicatorUids;

public class DataSetParentCall extends TransactionalCall {
    private final User user;
    private final DataSetParentLinkManager linkManager;
    private final PeriodHandler periodHandler;

    private DataSetParentCall(User user, GenericCallData data, DataSetParentLinkManager linkManager,
                              PeriodHandler periodHandler) {
        super(data);
        this.user = user;
        this.linkManager = linkManager;
        this.periodHandler = periodHandler;
    }

    @Override
    public Response callBody() throws Exception {
        DataSetEndpointCall dataSetEndpointCall
                = DataSetEndpointCall.create(data, getAssignedDataSetUids(user));
        Response<Payload<DataSet>> dataSetResponse = dataSetEndpointCall.call();

        List<DataSet> dataSets = dataSetResponse.body().items();
        DataElementEndpointCall dataElementEndpointCall =
                DataElementEndpointCall.create(data, getDataElementUids(dataSets));
        Response<Payload<DataElement>> dataElementResponse = dataElementEndpointCall.call();

        IndicatorEndpointCall indicatorEndpointCall
                = IndicatorEndpointCall.create(data, getIndicatorUids(dataSets));
        Response<Payload<Indicator>> indicatorResponse = indicatorEndpointCall.call();

        List<Indicator> indicators = indicatorResponse.body().items();
        IndicatorTypeEndpointCall indicatorTypeEndpointCall
                = IndicatorTypeEndpointCall.create(data, getIndicatorTypeUids(indicators));
        indicatorTypeEndpointCall.call();

        linkManager.saveDataSetDataElementAndIndicatorLinks(dataSets);
        linkManager.saveDataSetOrganisationUnitLinks(user);

        periodHandler.generateAndPersist();

        return dataElementResponse;
    }

    public interface Factory {
        Call<Response> create(User user, GenericCallData data);
    }

    public static final Factory FACTORY = new Factory() {
        @Override
        public Call<Response> create(User user, GenericCallData data) {
            return new DataSetParentCall(user, data,
                    DataSetParentLinkManager.create(data.databaseAdapter()),
                    PeriodHandler.create(data.databaseAdapter()));
        }
    };
}
