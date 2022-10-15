package com.aad.storyapp.helper

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 15/10/2022 10.55
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object DataClassMapper {

    fun mapStoryEntityToStoryModel(data: List<com.aad.storyapp.datasource.local.entities.Story>): List<com.aad.storyapp.model.Story> {
        return data.map {
            com.aad.storyapp.model.Story(
                id = it.id,
                name = it.name,
                photoUrl = it.photoUrl,
                description = it.description,
                createdAt = it.createdAt,
                lat = it.lat,
                lon = it.lon
            )
        }
    }

    fun mapStoryModelToStoryEntity(data: List<com.aad.storyapp.model.Story>): List<com.aad.storyapp.datasource.local.entities.Story> {
        return data.map {
            com.aad.storyapp.datasource.local.entities.Story(
                id = it.id,
                name = it.name,
                photoUrl = it.photoUrl,
                description = it.description,
                createdAt = it.createdAt,
                lat = it.lat,
                lon = it.lon
            )
        }
    }

}