package hr.pet.dao

import android.content.ContentValues
import android.database.Cursor

interface OrganizationRepository {
    fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor

    fun insert(values: ContentValues?): Long
    fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int

    fun delete(selection: String?, selectionArgs: Array<String>?): Int
}