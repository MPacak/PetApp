package hr.pet.factory

import android.content.Context
import hr.pet.dao.DogRepository
import hr.pet.dao.DogRepositoryImpl
import hr.pet.dao.OrganizationRepository
import hr.pet.dao.OrganizationRepositoryImpl
import hr.pet.dao.PetFinderSqlHelper


private fun getDbHelper(ctx: Context): PetFinderSqlHelper =
    PetFinderSqlHelper(ctx.applicationContext)

fun getDogRepository(ctx: Context): DogRepository =
    DogRepositoryImpl(getDbHelper(ctx))

fun getOrgRepository(ctx: Context): OrganizationRepository =
    OrganizationRepositoryImpl(getDbHelper(ctx))