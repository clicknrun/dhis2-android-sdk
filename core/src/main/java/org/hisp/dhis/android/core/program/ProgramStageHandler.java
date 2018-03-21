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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class ProgramStageHandler {
    private final IdentifiableObjectStore<ProgramStageModel> programStageStore;
    private final ProgramStageSectionHandler programStageSectionHandler;
    private final ProgramStageDataElementHandler programStageDataElementHandler;
    private final GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    public ProgramStageHandler(IdentifiableObjectStore<ProgramStageModel> programStageStore,
                               ProgramStageSectionHandler programStageSectionHandler,
                               ProgramStageDataElementHandler programStageDataElementHandler,
                               GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler) {
        this.programStageStore = programStageStore;
        this.programStageSectionHandler = programStageSectionHandler;
        this.programStageDataElementHandler = programStageDataElementHandler;
        this.styleHandler = styleHandler;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public void handleProgramStage(String programUid, List<ProgramStage> programStages) {
        if (programStages == null || programUid == null) {
            return;
        }
        for (int i = 0, size = programStages.size(); i < size; i++) {
            ProgramStage programStage = programStages.get(i);
            if (isDeleted(programStage)) {
                programStageStore.delete(programStage.uid());
            } else {
                ProgramStageModel model = ProgramStageModel.fromPojo(programStage, programUid);
                programStageStore.updateOrInsert(model);
            }


            // We will first save programStageDataElements which will invoke saving of all data elements
            // and graph below (data elements, option sets, options..)
            programStageDataElementHandler.handleProgramStageDataElements(
                    programStage.programStageDataElements());

            // programStageSectionHandler will first persist the programStageSections,
            // then we will update the programStageDataElements with the missing link to programStageSection
            // based on the dataElement foreign key for the programStageDataElement
            programStageSectionHandler.handleProgramStageSection(programStage.uid(),
                    programStage.programStageSections());

            styleHandler.handle(programStage.style(),
                    new ObjectStyleModelBuilder(programStage.uid(), ProgramStageModel.TABLE));
        }
    }
}
