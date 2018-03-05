package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

public interface IdentifiableStore extends DeletableStore {

    void delete(@NonNull String uid) throws RuntimeException;

    Boolean exists(String uid);

}
