package hr.pet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.pet.model.Organization

private const val DB_NAME    = "pets.db"
private const val DB_VERSION = 2
private const val TABLE_ORG = "organizations"

private val CREATE_ORG_TABLE = "create table $TABLE_ORG( " +
        "${Organization::id.name} integer primary key autoincrement, " +
        "${Organization::name.name} text not null, " +
        "${Organization::email.name} text, " +
        "${Organization::phone.name} text, " +
        "${Organization::address.name} text not null, " +
        "${Organization::state.name} text not null, " +
        "${Organization::city.name} text not null, " +
        "${Organization::postcode.name} text not null, " +
        "${Organization::country.name} text not null, " +
        "${Organization::photoPath.name} text not null" +
        ")"

private const val DROP_ORG_TABLE = "drop table if exists $TABLE_ORG"
class OrganizationSqlHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
), OrganizationRepository {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_ORG_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_ORG_TABLE)
        onCreate(db)
    }

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_ORG,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?): Long = writableDatabase.insert(
        TABLE_ORG,
        null,
        values
    )

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.update(
        TABLE_ORG,
        values,
        selection,
        selectionArgs
    )

    override fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.delete(
        TABLE_ORG,
        selection,
        selectionArgs
    )
}
