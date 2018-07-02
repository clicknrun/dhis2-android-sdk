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

import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

public class SectionHandler extends IdentifiableHandlerImpl<Section, SectionModel> {
    private final ObjectWithoutUidStore<SectionDataElementLinkModel> sectionDataElementLinkStore;

    SectionHandler(IdentifiableObjectStore<SectionModel> sectionStore,
                   ObjectWithoutUidStore<SectionDataElementLinkModel> sectionDataElementLinkStore) {
        super(sectionStore);
        this.sectionDataElementLinkStore = sectionDataElementLinkStore;
    }

    public static SectionHandler create(DatabaseAdapter databaseAdapter) {
        return new SectionHandler(
                SectionStore.create(databaseAdapter),
                SectionDataElementLinkStore.create(databaseAdapter));
    }

    @Override
    protected void afterObjectHandled(Section section, HandleAction action) {
        saveSectionDataElementLink(section);
    }

    private void saveSectionDataElementLink(Section section) {
        List<ObjectWithUid> dataElements = section.dataElements();
        if (dataElements != null) {
            SectionDataElementLinkModelBuilder builder =
                    new SectionDataElementLinkModelBuilder(section);
            for (ObjectWithUid dataElement : dataElements) {
                sectionDataElementLinkStore.updateOrInsertWhere(builder.buildModel(dataElement));
            }
        }
    }
}