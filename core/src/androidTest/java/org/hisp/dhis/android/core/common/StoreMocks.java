package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.option.OptionSetModel;

import java.util.Date;

public class StoreMocks {
    public static OptionSetModel generateOptionSetModel() {
        return OptionSetModel.builder()
                .uid("1234567890")
                .code("code")
                .name("name")
                .displayName("displayName")
                .created(new Date())
                .lastUpdated(new Date())
                .version(1)
                .valueType(ValueType.AGE)
                .build();
    }
}
