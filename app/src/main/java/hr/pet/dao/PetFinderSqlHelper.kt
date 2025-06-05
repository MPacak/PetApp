package hr.pet.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.pet.model.Dog
import hr.pet.model.Organization

private const val DB_NAME    = "pets.db"
private const val DB_VERSION = 1
internal const val TABLE_DOGS = "dogs"
internal const val TABLE_ORGS = "organizations"

private val CREATE_DOGS_TABLE = "create table $TABLE_DOGS( " +
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

private val CREATE_ORG_TABLE = "create table $TABLE_ORGS( " +
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

private const val DROP_ORG_TABLE = "drop table if exists $TABLE_ORGS"
private const val DROP_DOG_TABLE = "DROP TABLE IF EXISTS $TABLE_DOGS"
class PetFinderSqlHelper (context: Context?) : SQLiteOpenHelper(
context,
DB_NAME,
null,
DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DOGS_TABLE)
        db.execSQL(CREATE_ORG_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORGS")
        onCreate(db)
    }
}
