package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.option.OptionSetHandler;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class DataElementFactory {
    private final DatabaseAdapter databaseAdapter;
    private final DataElementService dataElementService;
    private final ResourceHandler resourceHandler;
    private final IdentifiableObjectStore<DataElementModel> dataElementStore;
    private final DataElementHandler dataElementHandler;
    private final List<DeletableStore> deletableStores;

    public DataElementFactory(Retrofit retrofit, DatabaseAdapter databaseAdapter,
            ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.dataElementService = retrofit.create(DataElementService.class);
        this.resourceHandler = resourceHandler;
        this.dataElementStore = DataElementStore.create(databaseAdapter);

        OptionSetHandler optionSetHandler = OptionSetHandler.create(databaseAdapter);
        this.dataElementHandler = DataElementHandler.create(databaseAdapter, optionSetHandler);
        deletableStores = new ArrayList<>();
        deletableStores.add(dataElementStore);

    }

    public DataElementEndPointCall newEndPointCall(DataElementQuery dataElementQuery,
            Date serverDate) {
        return new DataElementEndPointCall(dataElementService,
                dataElementQuery,
                dataElementHandler, resourceHandler, databaseAdapter, serverDate);
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }

    public IdentifiableObjectStore<DataElementModel> getDataElementStore() {
        return dataElementStore;
    }

    public DataElementHandler getDataElementHandler() {
        return dataElementHandler;
    }
}
