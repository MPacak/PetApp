package hr.pet.dao

import android.content.ContentValues
import android.database.Cursor

class OrganizationRepositoryImpl(private val dbHelper: PetFinderSqlHelper) : OrganizationRepository {
    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = dbHelper.readableDatabase.query(
        TABLE_ORGS,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?): Long =
        dbHelper.writableDatabase.insert(TABLE_ORGS, null, values)

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = dbHelper.writableDatabase.update(
        TABLE_ORGS,
        values,
        selection,
        selectionArgs
    )

    override fun delete(selection: String?, selectionArgs: Array<String>?): Int =
        dbHelper.writableDatabase.delete(
            TABLE_ORGS,
            selection,
            selectionArgs
        )
}