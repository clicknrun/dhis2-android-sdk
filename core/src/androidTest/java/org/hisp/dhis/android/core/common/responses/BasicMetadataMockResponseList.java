package org.hisp.dhis.android.core.common.responses;

import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.MetadataMockResponseList;

import java.io.IOException;

public class BasicMetadataMockResponseList extends MetadataMockResponseList {

    public BasicMetadataMockResponseList() throws IOException {
        super(new AssetsFileReader());
    }

    @Override
    protected String getSystemInfoMockResponse() {
        return "system_info.json";
    }

    @Override
    protected String getUserMockResponse() {
        return "user.json";
    }

    @Override
    protected String getCategoriesMockResponse() {
        return "categories.json";
    }

    @Override
    protected String getCategoryCombosMockResponse() {
        return "category_combos.json";
    }

    @Override
    protected String getProgramsWithAccessMockResponse() {
        return "programs_with_access.json";
    }

    @Override
    protected String getProgramsMockResponse() {
        return "programs.json";
    }

    @Override
    protected String getTrackedEntityMockResponse() {
        return "tracked_entities.json";
    }

    @Override
    protected String getOrganisationUnitMockResponse() {
        return "organisationUnits.json";
    }

    @Override
    protected String getOptionSetMockResponse() {
        return "option_sets.json";
    }

    @Override
    protected String getDataSetsWithAccessMockResponse() {
        return "data_sets_with_access.json";
    }

    @Override
    protected String getDataSetsMockResponse() {
        return "data_sets.json";
    }

    @Override
    protected String getDataElementsMockResponse() {
        return "data_elements.json";
    }

    @Override
    protected String getIndicatorsMockResponse() {
        return "indicators.json";
    }

    @Override
    protected String getIndicatorTypesMockResponse() {
        return "indicators_types.json";
    }
}
