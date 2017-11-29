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
package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.utils.IdentifiableObjectStore;
import org.hisp.dhis.android.core.dataset.utils.GenericHandler;

public class CategoryComboHandler extends GenericHandler<CategoryCombo, CategoryComboModel> {

    private final CategoryHandler categoryHandler;
    private final CategoryOptionComboHandler categoryOptionComboHandler;

    private CategoryComboHandler(IdentifiableObjectStore<CategoryComboModel> store,
                                CategoryHandler categoryHandler,
                                CategoryOptionComboHandler categoryOptionComboHandler) {
        super(store);
        this.categoryHandler = categoryHandler;
        this.categoryOptionComboHandler = categoryOptionComboHandler;
    }

    @Override
    protected void afterObjectPersisted(CategoryCombo categoryCombo) {
        // TODO handle children
        this.categoryHandler.handleMany(categoryCombo.categories());
        this.categoryOptionComboHandler.handleMany(categoryCombo.categoryOptionCombos());
    }

    @Override
    protected CategoryComboModel pojoToModel(CategoryCombo categoryCombo) {
        return CategoryComboModel.create(categoryCombo);
    }

    public static CategoryComboHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryComboHandler(CategoryComboStoreFactory.create(databaseAdapter),
                new CategoryHandler(CategoryStoreFactory.create(databaseAdapter),
                        CategoryCategoryOptionLinkStoreFactory.create(databaseAdapter),
                        new CategoryOptionHandler(CategoryOptionStoreFactory.create(databaseAdapter))),
                new CategoryOptionComboHandler(CategoryOptionComboStoreFactory.create(databaseAdapter)));
    }
}
