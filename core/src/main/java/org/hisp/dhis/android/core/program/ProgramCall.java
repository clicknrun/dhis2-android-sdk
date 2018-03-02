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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessiveMethodLength", "PMD.ExcessiveImports"})
public class ProgramCall implements Call<Response<Payload<Program>>> {
    private final ProgramService programService;

    private final DatabaseAdapter databaseAdapter;
    private final ResourceHandler resourceHandler;

    private boolean isExecuted;
    private final Date serverDate;
    private final ProgramHandler programHandler;
    private final ProgramQuery query;

    public ProgramCall(ProgramService programService,
            DatabaseAdapter databaseAdapter,
            ResourceHandler resourceHandler,
            Date serverDate,
            ProgramHandler programHandler, @NonNull ProgramQuery query) {
        this.programService = programService;
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = resourceHandler;
        this.serverDate = new Date(serverDate.getTime());
        this.programHandler = programHandler;
        this.query = query;

    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<Program>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }
        if (query.uIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of programs: " + query.uIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }

        String lastSyncedPrograms = resourceHandler.getLastUpdated(ResourceModel.Type.PROGRAM);
        Response<Payload<Program>> programsByLastUpdated = programService.getPrograms(
                getFields(), Program.lastUpdated.gt(lastSyncedPrograms),
                Program.uid.in(query.uIds()), Boolean.FALSE, query.isTranslationOn(),
                query.translationLocale()
        ).execute();
        if (programsByLastUpdated.isSuccessful()) {
            Transaction transaction = databaseAdapter.beginNewTransaction();
            try {
                List<Program> programs = programsByLastUpdated.body().items();
                int size = programs.size();
                for (int i = 0; i < size; i++) {
                    Program program = programs.get(i);
                    programHandler.handleProgram(program);
                }
                resourceHandler.handleResource(ResourceModel.Type.PROGRAM, serverDate);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }
        return programsByLastUpdated;
    }

    private Fields<Program> getFields() {
        return Fields.<Program>builder().fields(
                Program.uid, Program.code, Program.name, Program.displayName, Program.created,
                Program.lastUpdated, Program.shortName, Program.displayShortName,
                Program.description,
                Program.displayDescription, Program.version, Program.captureCoordinates,
                Program.dataEntryMethod,
                Program.deleted, Program.displayFrontPageList, Program.displayIncidentDate,
                Program.enrollmentDateLabel, Program.ignoreOverdueEvents, Program.incidentDateLabel,
                Program.onlyEnrollOnce, Program.programType, Program.registration,
                Program.relationshipFromA, Program.relationshipText,
                Program.selectEnrollmentDatesInFuture, Program.selectIncidentDatesInFuture,
                Program.useFirstStageDuringRegistration,
                Program.relatedProgram.with(Program.uid),
                Program.access.with(Access.data.with(DataAccess.write)),
                Program.style.with(ObjectStyle.allFields),
                getProgramStagesFields(),
                getProgramRulesFields(),
                getProgramVariablesFields(),
                getProgramIndicatorFields(),
                getProgramTrackedEntitiesAttributesFields(),
                Program.trackedEntity.with(TrackedEntity.uid),
                Program.categoryCombo.with(CategoryCombo.uid),
                getProgramRelationTypeFields()
        ).build();
    }

    @NonNull
    private NestedField<Program, ?> getProgramRelationTypeFields() {
        return Program.relationshipType.with(
                RelationshipType.uid, RelationshipType.code, RelationshipType.name,
                RelationshipType.displayName, RelationshipType.created,
                RelationshipType.lastUpdated,
                RelationshipType.aIsToB, RelationshipType.bIsToA, RelationshipType.deleted
        );
    }

    @NonNull
    private NestedField<Program, ?> getProgramTrackedEntitiesAttributesFields() {
        return Program.programTrackedEntityAttributes.with(
                ProgramTrackedEntityAttribute.uid, ProgramTrackedEntityAttribute.code,
                ProgramTrackedEntityAttribute.name,
                ProgramTrackedEntityAttribute.displayName,
                ProgramTrackedEntityAttribute.created,
                ProgramTrackedEntityAttribute.lastUpdated,
                ProgramTrackedEntityAttribute.shortName,
                ProgramTrackedEntityAttribute.displayShortName,
                ProgramTrackedEntityAttribute.description,
                ProgramTrackedEntityAttribute.displayDescription,
                ProgramTrackedEntityAttribute.allowFutureDate,
                ProgramTrackedEntityAttribute.deleted,
                ProgramTrackedEntityAttribute.displayInList,
                ProgramTrackedEntityAttribute.mandatory,
                ProgramTrackedEntityAttribute.program.with(Program.uid),
                ProgramTrackedEntityAttribute.trackedEntityAttribute.with(
                        TrackedEntityAttribute.uid, TrackedEntityAttribute.code,
                        TrackedEntityAttribute.created, TrackedEntityAttribute.lastUpdated,
                        TrackedEntityAttribute.name, TrackedEntityAttribute.displayName,
                        TrackedEntityAttribute.shortName,
                        TrackedEntityAttribute.displayShortName,
                        TrackedEntityAttribute.description,
                        TrackedEntityAttribute.displayDescription,
                        TrackedEntityAttribute.displayInListNoProgram,
                        TrackedEntityAttribute.displayOnVisitSchedule,
                        TrackedEntityAttribute.expression,
                        TrackedEntityAttribute.generated, TrackedEntityAttribute.inherit,
                        TrackedEntityAttribute.orgUnitScope,
                        TrackedEntityAttribute.programScope,
                        TrackedEntityAttribute.pattern,
                        TrackedEntityAttribute.sortOrderInListNoProgram,
                        TrackedEntityAttribute.unique, TrackedEntityAttribute.valueType,
                        TrackedEntityAttribute.searchScope,
                        TrackedEntityAttribute.optionSet.with(
                                OptionSet.uid, OptionSet.version
                        ),
                        TrackedEntityAttribute.style.with(ObjectStyle.allFields),
                        TrackedEntityAttribute.renderType
                )
        );
    }

    @NonNull
    private NestedField<Program, ?> getProgramIndicatorFields() {
        return Program.programIndicators.with(
                ProgramIndicator.uid, ProgramIndicator.code, ProgramIndicator.name,
                ProgramIndicator.displayName, ProgramIndicator.created,
                ProgramIndicator.lastUpdated, ProgramIndicator.shortName,
                ProgramIndicator.displayShortName, ProgramIndicator.description,
                ProgramIndicator.displayDescription, ProgramIndicator.decimals,
                ProgramIndicator.deleted, ProgramIndicator.dimensionItem,
                ProgramIndicator.displayInForm,
                ProgramIndicator.expression, ProgramIndicator.filter,
                ProgramIndicator.program.with(Program.uid)
        );
    }

    @NonNull
    private NestedField<Program, ?> getProgramVariablesFields() {
        return Program.programRuleVariables.with(
                ProgramRuleVariable.uid, ProgramRuleVariable.code, ProgramRuleVariable.name,
                ProgramRuleVariable.displayName, ProgramRuleVariable.created,
                ProgramRuleVariable.lastUpdated,
                ProgramRuleVariable.deleted,
                ProgramRuleVariable.programRuleVariableSourceType,
                ProgramRuleVariable.useCodeForOptionSet,
                ProgramRuleVariable.program.with(Program.uid),
                ProgramRuleVariable.dataElement.with(DataElement.uid),
                ProgramRuleVariable.programStage.with(ProgramStage.uid),
                ProgramRuleVariable.trackedEntityAttribute.with(TrackedEntityAttribute.uid)
        );
    }

    @NonNull
    private NestedField<Program, ?> getProgramRulesFields() {
        return Program.programRules.with(
                ProgramRule.uid, ProgramRule.code, ProgramRule.name,
                ProgramRule.displayName,
                ProgramRule.created, ProgramRule.lastUpdated, ProgramRule.deleted,
                ProgramRule.priority, ProgramRule.condition,
                ProgramRule.program.with(Program.uid),
                ProgramRule.programStage.with(ProgramStage.uid),
                ProgramRule.programRuleActions.with(
                        ProgramRuleAction.uid, ProgramRuleAction.code,
                        ProgramRuleAction.name,
                        ProgramRuleAction.displayName, ProgramRuleAction.created,
                        ProgramRuleAction.lastUpdated, ProgramRuleAction.content,
                        ProgramRuleAction.data,
                        ProgramRuleAction.deleted, ProgramRuleAction.location,
                        ProgramRuleAction.programRuleActionType,
                        ProgramRuleAction.programRule.with(ProgramRule.uid),
                        ProgramRuleAction.dataElement.with(DataElement.uid),
                        ProgramRuleAction.programIndicator.with(ProgramIndicator.uid),
                        ProgramRuleAction.programStage.with(ProgramStage.uid),
                        ProgramRuleAction.programStageSection.with(ProgramStageSection.uid),
                        ProgramRuleAction.trackedEntityAttribute.with(
                                TrackedEntityAttribute.uid)
                )
        );
    }

    @NonNull
    private NestedField<Program, ?> getProgramStagesFields() {
        return Program.programStages.with(
                ProgramStage.uid, ProgramStage.code, ProgramStage.name,
                ProgramStage.displayName,
                ProgramStage.created, ProgramStage.lastUpdated,
                ProgramStage.allowGenerateNextVisit,
                ProgramStage.autoGenerateEvent, ProgramStage.blockEntryForm,
                ProgramStage.captureCoordinates,
                ProgramStage.deleted, ProgramStage.displayGenerateEventBox,
                ProgramStage.executionDateLabel,
                ProgramStage.formType, ProgramStage.generatedByEnrollmentDate,
                ProgramStage.hideDueDate,
                ProgramStage.minDaysFromStart, ProgramStage.openAfterEnrollment,
                ProgramStage.repeatable,
                ProgramStage.reportDateToUse, ProgramStage.sortOrder,
                ProgramStage.standardInterval,
                ProgramStage.validCompleteOnly, ProgramStage.programStageDataElements.with(
                        ProgramStageDataElement.uid, ProgramStageDataElement.code,
                        ProgramStageDataElement.created,
                        ProgramStageDataElement.lastUpdated,
                        ProgramStageDataElement.allowFutureDate,
                        ProgramStageDataElement.allowProvidedElsewhere,
                        ProgramStageDataElement.compulsory,
                        ProgramStageDataElement.deleted,
                        ProgramStageDataElement.displayInReports,
                        ProgramStageDataElement.sortOrder,
                        ProgramStageDataElement.programStage.with(ProgramStage.uid),
                        ProgramStageDataElement.dataElement.with(DataElement.allFields)
                ),
                ProgramStage.programStageSections.with(
                        ProgramStageSection.uid, ProgramStageSection.code,
                        ProgramStageSection.name,
                        ProgramStageSection.displayName, ProgramStageSection.created,
                        ProgramStageSection.lastUpdated, ProgramStageSection.sortOrder,
                        ProgramStageSection.deleted,
                        ProgramStageSection.dataElements.with(DataElement.uid),
                        ProgramStageSection.programIndicators.with(ProgramIndicator.uid,
                                ProgramIndicator.program.with(Program.uid)
                        ),
                        ProgramStageSection.renderType
                ),
                ProgramStage.style.with(ObjectStyle.allFields)
        );
    }

}
