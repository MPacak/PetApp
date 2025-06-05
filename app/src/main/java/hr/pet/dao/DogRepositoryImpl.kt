package hr.pet.dao

import android.content.ContentValues
import android.database.Cursor

class DogRepositoryImpl(private val dbHelper: PetFinderSqlHelper) : DogRepository {

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = dbHelper.readableDatabase.query(
        TABLE_DOGS,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?): Long =
        dbHelper.writableDatabase.insert(TABLE_DOGS, null, values)

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = dbHelper.writableDatabase.update(
        TABLE_DOGS,
        values,
        selection,
        selectionArgs
    )

    override fun delete(selection: String?, selectionArgs: Array<String>?): Int =
        dbHelper.writableDatabase.delete(
            TABLE_DOGS,
            selection,
            selectionArgs
        )
}