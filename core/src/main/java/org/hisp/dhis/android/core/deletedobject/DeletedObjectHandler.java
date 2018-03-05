package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableStore;

public class DeletedObjectHandler {
    @NonNull
    public final IdentifiableStoreFactory identifiableStoreFactory;

    public DeletedObjectHandler(IdentifiableStoreFactory identifiableStoreFactory) {
        this.identifiableStoreFactory = identifiableStoreFactory;
    }

    public void handle(String uid, String klass) {
        IdentifiableStore identifiableStore = getStore(klass);
        try {
            identifiableStore.delete(uid);
        } catch (RuntimeException e) {
            // Nothing has been deleted.
        }
    }

    public IdentifiableStore getStore(String klass) {
        return identifiableStoreFactory.getByKlass(klass);
    }
}
