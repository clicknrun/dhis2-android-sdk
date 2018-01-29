package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionComboCategoryLinkStoreImpl implements
        CategoryOptionComboCategoryLinkStore {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboCategoryLinkModel.TABLE + " (" +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + ", " +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + ") " +
                    "VALUES(?, ?);";
    private static final String REMOVE_CATEGORY_OPTION_RELATIONS = "DELETE FROM "
            + CategoryOptionComboCategoryLinkModel.TABLE + " WHERE "+ CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + "=?;";
    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;

    public CategoryOptionComboCategoryLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryOptionComboCategoryLinkModel entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    private void validate(@NonNull CategoryOptionComboCategoryLinkModel link) {
        isNull(link.optionCombo());
        isNull(link.category());
    }

    private void bind(SQLiteStatement sqLiteStatement,
            @NonNull CategoryOptionComboCategoryLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.optionCombo());
        sqLiteBind(sqLiteStatement, 2, link.category());
    }

    private int executeInsert() {
        int lastId = (int) databaseAdapter.executeInsert(CategoryOptionComboCategoryLinkModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryOptionComboCategoryLinkModel.TABLE);
    }

    @Override
    public int removeCategoryComboOptionRelationsByCategoryOptionCombo(String categoryOptionComboUid){
        Cursor cursor = databaseAdapter.query(REMOVE_CATEGORY_OPTION_RELATIONS, categoryOptionComboUid);

        return cursor.getCount();
    }
}

