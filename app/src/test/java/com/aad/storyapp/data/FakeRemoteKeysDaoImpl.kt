package com.aad.storyapp.data

import com.aad.storyapp.datasource.local.dao.RemoteKeysDao
import com.aad.storyapp.datasource.local.entities.RemoteKeys

/****************************************************
 * Created by Indra Muliana
 * On Friday, 28/10/2022 19.14
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class FakeRemoteKeysDaoImpl : RemoteKeysDao {

    private val remoteKeys = mutableListOf<RemoteKeys>()

    override suspend fun insertAll(remoteKey: List<RemoteKeys>) {
        remoteKeys.addAll(remoteKey)
    }

    override suspend fun getRemoteKeysId(id: String): RemoteKeys? {
        return remoteKeys.firstOrNull { it.id == id }
    }

    override suspend fun deleteRemoteKeys() {
        remoteKeys.clear()
    }
}