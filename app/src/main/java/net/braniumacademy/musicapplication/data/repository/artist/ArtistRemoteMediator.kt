package net.braniumacademy.musicapplication.data.repository.artist

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistRemoteKeys
import net.braniumacademy.musicapplication.data.model.tracking.DBTracking
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import net.braniumacademy.musicapplication.data.source.remote.PagingParam
import net.braniumacademy.musicapplication.utils.MusicAppUtils.CACHE_TIMEOUT
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ArtistRemoteMediator @Inject constructor(
    private val repository: ArtistRepositoryImpl,
    private val database: AppDatabase
) : RemoteMediator<Int, Artist>() {
    private val artistDao = database.artistDao()
    private val artistRemoteKeysDao = database.artistRemoteKeysDao()
    private val dbTrackingDao = database.dbTrackingDao()
    private var currentPosition = -1

    override suspend fun initialize(): InitializeAction {
        return withContext(Dispatchers.IO) {
            val lastArtistUpdated = dbTrackingDao.tracking?.lastArtistUpdated ?: 0
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(CACHE_TIMEOUT, TimeUnit.HOURS)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastArtistUpdated < cacheTimeout) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Artist>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }

            LoadType.PREPEND ->
                return MediatorResult.Success(endOfPaginationReached = true)

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        val pagingParam = PagingParam(
            page * state.config.pageSize,
            state.config.pageSize
        )
        if (pagingParam.offset == currentPosition) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } else {
            currentPosition = pagingParam.offset
        }
        return try {
            val artists = repository.loadArtistPaging(pagingParam)
            val endOfPaginationReached = artists.isEmpty() || artists.size < state.config.pageSize
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    artistDao.clearAll()
                    artistRemoteKeysDao.clearAll()
                }
                val prevKey = if (page > 0) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = artists.map { artist ->
                    ArtistRemoteKeys(artist.id, prevKey, nextKey)
                }
                repository.insert(*artists.toTypedArray())
                artistRemoteKeysDao.insertAll(keys)
                dbTrackingDao.insert(
                    DBTracking(
                        0,
                        lastSongUpdated = 0,
                        lastArtistUpdated = System.currentTimeMillis()
                    )
                )
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Artist>
    ): ArtistRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { artistId ->
                artistRemoteKeysDao.remoteKeysArtistId(artistId)
            }
        }
    }

    private suspend fun getRemoteKeysForLastItem(
        state: PagingState<Int, Artist>
    ): ArtistRemoteKeys? {
        return state.pages.lastOrNull { page ->
            page.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { artist ->
            artistRemoteKeysDao.remoteKeysArtistId(artist.id)
        }
    }
}