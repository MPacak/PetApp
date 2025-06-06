package hr.pet

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import hr.pet.dao.DogRepository
import hr.pet.dao.DogRepositoryImpl
import hr.pet.dao.OrganizationRepository
import hr.pet.dao.OrganizationRepositoryImpl
import hr.pet.dao.PetFinderSqlHelper
import hr.pet.factory.getDogRepository
import hr.pet.factory.getOrgRepository
import hr.pet.model.Dog
import hr.pet.model.Organization

private const val AUTHORITY = "hr.pet.provider"
private const val PATH_DOGS     = "dogs"
private const val PATH_ORGS     = "organizations"
private const val DOGS     = 10
private const val DOG_ID   = 11
private const val ORGS     = 20
private const val ORG_ID   = 21
private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(AUTHORITY, PATH_DOGS, DOGS)
    addURI(AUTHORITY, "$PATH_DOGS/#", DOG_ID)
    addURI(AUTHORITY, PATH_ORGS, ORGS)
    addURI(AUTHORITY, "$PATH_ORGS/#", ORG_ID)
}
val DOGS_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_DOGS")
val ORGS_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_ORGS")
class PetProvider : ContentProvider() {
    private lateinit var dogRepo: DogRepository
    private lateinit var orgRepo: OrganizationRepository
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val count = when (URI_MATCHER.match(uri)) {
            DOGS, DOG_ID -> dogRepo.delete(
                if (URI_MATCHER.match(uri) == DOG_ID) "${Dog::id}=?" else selection,
                if (URI_MATCHER.match(uri) == DOG_ID) arrayOf(uri.lastPathSegment!!) else selectionArgs
            )
            ORGS, ORG_ID -> orgRepo.delete(
                if (URI_MATCHER.match(uri) == ORG_ID) "${Organization::id}=?" else selection,
                if (URI_MATCHER.match(uri) == ORG_ID) arrayOf(uri.lastPathSegment!!) else selectionArgs
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return count
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = when (URI_MATCHER.match(uri)) {
            DOGS   -> dogRepo.insert(values)
            ORGS   -> orgRepo.insert(values)
            else        -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val newUri = when (URI_MATCHER.match(uri)) {
            DOGS -> ContentUris.withAppendedId(DOGS_CONTENT_URI, id)
            ORGS -> ContentUris.withAppendedId(ORGS_CONTENT_URI, id)
            else        -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return newUri
    }

    override fun onCreate(): Boolean {
        dogRepo = getDogRepository(context!!)
        orgRepo = getOrgRepository(context!!)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor = when (URI_MATCHER.match(uri)) {
            DOGS    -> dogRepo.query(projection, selection, selectionArgs, sortOrder)
            ORGS    -> orgRepo.query(projection, selection, selectionArgs, sortOrder)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val count = when (URI_MATCHER.match(uri)) {
            DOGS, DOG_ID -> dogRepo.update(
                values,
                if (URI_MATCHER.match(uri) == DOG_ID) "${Dog::id.name}=?" else selection,
                selectionArgs
            )
            ORGS, ORG_ID -> orgRepo.update(
                values,
                if (URI_MATCHER.match(uri) == ORG_ID) "${Organization::id.name}=?" else selection,
                selectionArgs
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return count
    }
}