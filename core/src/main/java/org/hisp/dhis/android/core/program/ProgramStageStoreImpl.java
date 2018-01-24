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

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class ProgramStageStoreImpl implements ProgramStageStore {
    private static final String FIELDS =
            ProgramStageModel.Columns.UID + ", " +
                    ProgramStageModel.Columns.CODE + ", " +
                    ProgramStageModel.Columns.NAME + ", " +
                    ProgramStageModel.Columns.DISPLAY_NAME + ", " +
                    ProgramStageModel.Columns.CREATED + ", " +
                    ProgramStageModel.Columns.LAST_UPDATED + ", " +
                    ProgramStageModel.Columns.EXECUTION_DATE_LABEL + ", " +
                    ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT + ", " +
                    ProgramStageModel.Columns.VALID_COMPLETE_ONLY + ", " +
                    ProgramStageModel.Columns.REPORT_DATE_TO_USE + ", " +
                    ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT + ", " +
                    ProgramStageModel.Columns.REPEATABLE + ", " +
                    ProgramStageModel.Columns.CAPTURE_COORDINATES + ", " +
                    ProgramStageModel.Columns.FORM_TYPE + ", " +
                    ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX + ", " +
                    ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE + ", " +
                    ProgramStageModel.Columns.AUTO_GENERATE_EVENT + ", " +
                    ProgramStageModel.Columns.SORT_ORDER + ", " +
                    ProgramStageModel.Columns.HIDE_DUE_DATE + ", " +
                    ProgramStageModel.Columns.BLOCK_ENTRY_FORM + ", " +
                    ProgramStageModel.Columns.MIN_DAYS_FROM_START + ", " +
                    ProgramStageModel.Columns.STANDARD_INTERVAL + ", " +
                    ProgramStageModel.Columns.PROGRAM;

    private static final String QUERY_BY_PROGRAM_STATEMENT =
            "SELECT " + FIELDS + " FROM " + ProgramStageModel.TABLE + " WHERE " +
                    ProgramStageModel.Columns.PROGRAM + "=?";

    private static final String INSERT_STATEMENT = "INSERT INTO " + ProgramStageModel.TABLE + " (" +
            FIELDS
            + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramStageModel.TABLE + " SET " +
            ProgramStageModel.Columns.UID + " =?, " +
            ProgramStageModel.Columns.CODE + " =?, " +
            ProgramStageModel.Columns.NAME + " =?, " +
            ProgramStageModel.Columns.DISPLAY_NAME + " =?, " +
            ProgramStageModel.Columns.CREATED + " =?, " +
            ProgramStageModel.Columns.LAST_UPDATED + " =?, " +
            ProgramStageModel.Columns.EXECUTION_DATE_LABEL + " =?, " +
            ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT + " =?, " +
            ProgramStageModel.Columns.VALID_COMPLETE_ONLY + " =?, " +
            ProgramStageModel.Columns.REPORT_DATE_TO_USE + " =?, " +
            ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT + " =?, " +
            ProgramStageModel.Columns.REPEATABLE + " =?, " +
            ProgramStageModel.Columns.CAPTURE_COORDINATES + " =?, " +
            ProgramStageModel.Columns.FORM_TYPE + " =?, " +
            ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX + " =?, " +
            ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE + " =?, " +
            ProgramStageModel.Columns.AUTO_GENERATE_EVENT + " =?, " +
            ProgramStageModel.Columns.SORT_ORDER + " =?, " +
            ProgramStageModel.Columns.HIDE_DUE_DATE + " =?, " +
            ProgramStageModel.Columns.BLOCK_ENTRY_FORM + " =?, " +
            ProgramStageModel.Columns.MIN_DAYS_FROM_START + " =?, " +
            ProgramStageModel.Columns.STANDARD_INTERVAL + " =?, " +
            ProgramStageModel.Columns.PROGRAM + " =? " +
            " WHERE " +
            ProgramStageModel.Columns.UID + " =?;";
    private static final String DELETE_STATEMENT =
            "DELETE FROM " + ProgramStageModel.TABLE + " WHERE " +
                    ProgramStageModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramStageStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
            @NonNull Date lastUpdated,
            @Nullable String executionDateLabel,
            @NonNull Boolean allowGenerateNextVisit,
            @NonNull Boolean validCompleteOnly,
            @Nullable String reportDateToUse,
            @NonNull Boolean openAfterEnrollment,
            @NonNull Boolean repeatable,
            @NonNull Boolean captureCoordinates,
            @NonNull FormType formType,
            @NonNull Boolean displayGenerateEventBox,
            @NonNull Boolean generatedByEnrollmentDate,
            @NonNull Boolean autoGenerateEvent,
            @NonNull Integer sortOrder,
            @NonNull Boolean hideDueDate,
            @NonNull Boolean blockEntryForm,
            @NonNull Integer minDaysFromStart,
            @NonNull Integer standardInterval,
            @NonNull String program) {
        isNull(uid);
        isNull(program);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated,
                executionDateLabel,
                allowGenerateNextVisit, validCompleteOnly, reportDateToUse, openAfterEnrollment,
                repeatable,
                captureCoordinates, formType, displayGenerateEventBox, generatedByEnrollmentDate,
                autoGenerateEvent,
                sortOrder, hideDueDate, blockEntryForm, minDaysFromStart, standardInterval,
                program);

        Long insert = databaseAdapter.executeInsert(ProgramStageModel.TABLE, insertStatement);

        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable String executionDateLabel,
            @NonNull Boolean allowGenerateNextVisit, @NonNull Boolean validCompleteOnly,
            @Nullable String reportDateToUse, @NonNull Boolean openAfterEnrollment,
            @NonNull Boolean repeatable, @NonNull Boolean captureCoordinates,
            @NonNull FormType formType, @NonNull Boolean displayGenerateEventBox,
            @NonNull Boolean generatedByEnrollmentDate, @NonNull Boolean autoGenerateEvent,
            @NonNull Integer sortOrder, @NonNull Boolean hideDueDate,
            @NonNull Boolean blockEntryForm,
            @NonNull Integer minDaysFromStart, @NonNull Integer standardInterval,
            @NonNull String program, @NonNull String whereProgramStageUid) {
        isNull(uid);
        isNull(program);
        isNull(whereProgramStageUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated,
                executionDateLabel,
                allowGenerateNextVisit, validCompleteOnly, reportDateToUse, openAfterEnrollment,
                repeatable,
                captureCoordinates, formType, displayGenerateEventBox, generatedByEnrollmentDate,
                autoGenerateEvent, sortOrder, hideDueDate, blockEntryForm,
                minDaysFromStart, standardInterval, program);

        // bind the where argument
        sqLiteBind(updateStatement, 24, whereProgramStageUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramStageModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramStageModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    @Override
    public List<ProgramStage> queryByProgramUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_PROGRAM_STATEMENT, uid);

        Map<String, List<ProgramStage>> programMap = mapFromCursor(cursor);

        return programMap.get(uid);
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement, @NonNull String uid,
            @Nullable String code,
            @NonNull String name, @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable String executionDateLabel,
            @NonNull Boolean allowGenerateNextVisit, @NonNull Boolean validCompleteOnly,
            @Nullable String reportDateToUse, @NonNull Boolean openAfterEnrollment,
            @NonNull Boolean repeatable, @NonNull Boolean captureCoordinates,
            @NonNull FormType formType, @NonNull Boolean displayGenerateEventBox,
            @NonNull Boolean generatedByEnrollmentDate, @NonNull Boolean autoGenerateEvent,
            @NonNull Integer sortOrder, @NonNull Boolean hideDueDate,
            @NonNull Boolean blockEntryForm, @NonNull Integer minDaysFromStart,
            @NonNull Integer standardInterval, @NonNull String program) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, executionDateLabel);
        sqLiteBind(sqLiteStatement, 8, allowGenerateNextVisit);
        sqLiteBind(sqLiteStatement, 9, validCompleteOnly);
        sqLiteBind(sqLiteStatement, 10, reportDateToUse);
        sqLiteBind(sqLiteStatement, 11, openAfterEnrollment);
        sqLiteBind(sqLiteStatement, 12, repeatable);
        sqLiteBind(sqLiteStatement, 13, captureCoordinates);
        sqLiteBind(sqLiteStatement, 14, formType == null ? null : formType.name());
        sqLiteBind(sqLiteStatement, 15, displayGenerateEventBox);
        sqLiteBind(sqLiteStatement, 16, generatedByEnrollmentDate);
        sqLiteBind(sqLiteStatement, 17, autoGenerateEvent);
        sqLiteBind(sqLiteStatement, 18, sortOrder);
        sqLiteBind(sqLiteStatement, 19, hideDueDate);
        sqLiteBind(sqLiteStatement, 20, blockEntryForm);
        sqLiteBind(sqLiteStatement, 21, minDaysFromStart);
        sqLiteBind(sqLiteStatement, 22, standardInterval);
        sqLiteBind(sqLiteStatement, 23, program);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramStageModel.TABLE);
    }

    private Map<String, List<ProgramStage>> mapFromCursor(Cursor cursor) {

        Map<String, List<ProgramStage>> programStagesMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    String uid = getStringFromCursor(cursor, 0);
                    String code = getStringFromCursor(cursor, 1);
                    String name = getStringFromCursor(cursor, 2);
                    String displayName = getStringFromCursor(cursor, 3);
                    Date created = getDateFromCursor(cursor, 4);
                    Date lastUpdated = getDateFromCursor(cursor, 5);
                    String executionDateLabel = getStringFromCursor(cursor, 6);
                    Boolean allowGenerateNextVisit = getBooleanFromCursor(cursor, 7);
                    Boolean validCompleteOnly = getBooleanFromCursor(cursor, 8);
                    String reportDateToUse = getStringFromCursor(cursor, 9);
                    Boolean openAfterEnrollment = getBooleanFromCursor(cursor, 10);
                    Boolean repeatable = getBooleanFromCursor(cursor, 11);
                    Boolean captureCoordinates = getBooleanFromCursor(cursor, 12);
                    FormType formType = getFormTypeFromCursor(cursor, 13);
                    Boolean displayGenerateEventBox = getBooleanFromCursor(cursor, 14);
                    Boolean generatedByEnrollmentDate = getBooleanFromCursor(cursor, 15);
                    Boolean autoGenerateEvent = getBooleanFromCursor(cursor, 16);
                    Integer sortOrder = getIntegerFromCursor(cursor, 17);
                    Boolean hideDueDate = getBooleanFromCursor(cursor, 18);
                    Boolean blockEntryForm = getBooleanFromCursor(cursor, 19);
                    Integer minDaysFromStart = getIntegerFromCursor(cursor, 20);
                    Integer standardInterval = getIntegerFromCursor(cursor, 21);
                    String program = getStringFromCursor(cursor, 22);

                    if (!programStagesMap.containsKey(program)) {
                        programStagesMap.put(program, new ArrayList<ProgramStage>());
                    }

                    programStagesMap.get(program).add(ProgramStage.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .executionDateLabel(executionDateLabel)
                            .allowGenerateNextVisit(allowGenerateNextVisit)
                            .validCompleteOnly(validCompleteOnly)
                            .reportDateToUse(reportDateToUse)
                            .openAfterEnrollment(openAfterEnrollment)
                            .repeatable(repeatable)
                            .captureCoordinates(captureCoordinates)
                            .formType(formType)
                            .displayGenerateEventBox(displayGenerateEventBox)
                            .generatedByEnrollmentDate(generatedByEnrollmentDate)
                            .autoGenerateEvent(autoGenerateEvent)
                            .sortOrder(sortOrder)
                            .hideDueDate(hideDueDate)
                            .blockEntryForm(blockEntryForm)
                            .minDaysFromStart(minDaysFromStart)
                            .captureCoordinates(captureCoordinates)
                            .standardInterval(standardInterval)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return programStagesMap;
    }

    @Nullable
    private String getStringFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getString(index);
    }

    @Nullable
    private Date getDateFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : parse(cursor.getString(index));
    }

    @Nullable
    private Boolean getBooleanFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getInt(index) > 0;
    }

    @Nullable
    private Integer getIntegerFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : cursor.getInt(index);
    }

    @Nullable
    private FormType getFormTypeFromCursor(Cursor cursor, int index) {
        return cursor.getString(index) == null ? null : FormType.valueOf(
                cursor.getString(index));
    }
}
