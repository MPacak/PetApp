package hr.pet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.pet.model.Dog

private const val DB_NAME    = "pets.db"
private const val DB_VERSION = 1
private const val TABLE_DOG = "dogs"

private val CREATE_TABLE = "create table $TABLE_DOG( " +
        "${Dog::id.name} integer primary key autoincrement, " +
        "${Dog::breedPrimary.name} text not null, " +
        "${Dog::breedMixed.name} integer not null, " +
        "${Dog::age.name} text not null, " +
        "${Dog::gender.name} text not null, " +
        "${Dog::size.name} text not null, " +
        "${Dog::coat.name} text not null, " +
        "${Dog::name.name} text not null, " +
        "${Dog::description.name} text, " +
        "${Dog::colorPrimary.name} text, " +
        "${Dog::colorSecondary.name} text, " +
        "${Dog::picturePath.name} text not null " +
        ")"

private const val DROP_DOG_TABLE = "DROP TABLE IF EXISTS $TABLE_DOG"
class DogSqlHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
), DogRepository {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_DOG_TABLE)
        onCreate(db)
    }

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_DOG,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?): Long = writableDatabase.insert(
        TABLE_DOG,
        null,
        values
    )

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.update(
        TABLE_DOG,
        values,
        selection,
        selectionArgs
    )

    override fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.delete(
        TABLE_DOG,
        selection,
        selectionArgs
    )
}