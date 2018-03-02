package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.common.DictionaryTableHandler;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;

import java.util.ArrayList;
import java.util.List;

public class OptionSetFactory {
    private final GenericCallData data;
    private final OptionSetService optionSetService;
    private final OptionSetHandler optionSetHandler;
    private final OptionHandler optionHandler;
    private final OptionStore optionStore;

    private final List<DeletableStore> deletableStores;

    public OptionSetFactory(GenericCallData data) {
        this.data = data;
        this.optionSetService = data.retrofit().create(OptionSetService.class);
        this.optionStore = new OptionStoreImpl(data.databaseAdapter());

        IdentifiableObjectStore<OptionSetModel> optionSetStore = OptionSetStore.create(data.databaseAdapter());
        DictionaryTableHandler<ObjectStyle> styleHandler = ObjectStyleHandler.create(data.databaseAdapter());

        this.optionHandler = new OptionHandler(optionStore, styleHandler);
        this.optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);

        this.deletableStores = new ArrayList<>();
        this.deletableStores.add(optionSetStore);
        this.deletableStores.add(optionStore);
    }

    public OptionSetCall newEndPointCall(OptionSetQuery optionSetQuery) {
        return new OptionSetCall(data, optionSetService, optionSetHandler, optionSetQuery);
    }

    public OptionSetHandler getOptionSetHandler() {
        return optionSetHandler;
    }

    public OptionHandler getOptionHandler() {
        return optionHandler;
    }

    public OptionStore getOptionStore() {
        return optionStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }
}
