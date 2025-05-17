package net.braniumacademy.musicapplication.data.repository.song

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongRemoteKeys
import net.braniumacademy.musicapplication.data.model.tracking.DBTracking
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import net.braniumacademy.musicapplication.data.source.remote.PagingParam
import net.braniumacademy.musicapplication.utils.MusicAppUtils.CACHE_TIMEOUT
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SongRemoteMediator @Inject constructor(
    private val repository: SongRepositoryImpl,
    private val database: AppDatabase
) : RemoteMediator<Int, Song>() {
    private val songDao = database.songDao()
    private val songRemoteKeysDao = database.songRemoteKeysDao()
    private val dbTrackingDao = database.dbTrackingDao()
    private var currentPosition = -1

    override suspend fun initialize(): InitializeAction {
        return withContext(Dispatchers.IO) {
            val lastSongUpdated = dbTrackingDao.tracking?.lastSongUpdated ?: 0
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(CACHE_TIMEOUT, TimeUnit.HOURS)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSongUpdated < cacheTimeout) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Song>): MediatorResult {
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
            val songs = repository.loadSongPaging(pagingParam)
            val endOfPaginationReached = songs.isEmpty() || songs.size < state.config.pageSize
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    songDao.clearAll()
                    songRemoteKeysDao.clearAll()
                }
                val prevKey = if (page > 0) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = songs.map { song ->
                    SongRemoteKeys(song.id, prevKey, nextKey)
                }
                repository.insert(*songs.toTypedArray())
                songRemoteKeysDao.insertAll(keys)
                dbTrackingDao.insert(
                    DBTracking(
                        0,
                        lastSongUpdated = System.currentTimeMillis(),
                        lastArtistUpdated = 0
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
        state: PagingState<Int, Song>
    ): SongRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { songId ->
                songRemoteKeysDao.remoteKeysSongId(songId)
            }
        }
    }

    private suspend fun getRemoteKeysForLastItem(
        state: PagingState<Int, Song>
    ): SongRemoteKeys? {
        return state.pages.lastOrNull { page ->
            page.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { song ->
            songRemoteKeysDao.remoteKeysSongId(song.id)
        }
    }
}