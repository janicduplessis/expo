package expo.modules.updates.db.dao

import androidx.room.*
import expo.modules.updates.db.entity.AssetEntity
import expo.modules.updates.db.entity.UpdateEntity
import expo.modules.updates.db.enums.UpdateStatus
import java.util.*

@Dao
abstract class UpdateDao {
  /**
   * for private use only
   * must be marked public for Room
   * so we use the underscore to discourage use
   */
  @Query("SELECT * FROM updates WHERE scope_key = :scopeKey AND status IN (:statuses);")
  abstract fun _loadUpdatesForProjectWithStatuses(scopeKey: String?, statuses: List<UpdateStatus>): List<UpdateEntity>

  @Query("SELECT * FROM updates WHERE id = :id;")
  abstract fun _loadUpdatesWithId(id: UUID): List<UpdateEntity>

  @Query("SELECT assets.* FROM assets INNER JOIN updates ON updates.launch_asset_id = assets.id WHERE updates.id = :id;")
  abstract fun _loadLaunchAsset(id: UUID): AssetEntity

  @Query("UPDATE updates SET keep = 1 WHERE id = :id;")
  abstract fun _keepUpdate(id: UUID)

  @Query("UPDATE updates SET status = :status WHERE id = :id;")
  abstract fun _markUpdateWithStatus(status: UpdateStatus, id: UUID)

  @Update
  abstract fun _updateUpdate(update: UpdateEntity)

  @Query(
    "UPDATE updates SET status = :status WHERE id IN (" +
      "SELECT DISTINCT update_id FROM updates_assets WHERE asset_id IN (:missingAssetIds));"
  )
  abstract fun _markUpdatesWithMissingAssets(missingAssetIds: List<Long>, status: UpdateStatus)

  /**
   * for public use
   */
  @Query("SELECT * FROM updates;")
  abstract fun loadAllUpdates(): List<UpdateEntity>

  fun loadLaunchableUpdatesForScope(scopeKey: String?): List<UpdateEntity> {
    return _loadUpdatesForProjectWithStatuses(
      scopeKey,
      listOf(UpdateStatus.READY, UpdateStatus.EMBEDDED, UpdateStatus.DEVELOPMENT)
    )
  }

  @Query("SELECT * FROM updates WHERE status = :status;")
  abstract fun loadAllUpdatesWithStatus(status: UpdateStatus): List<UpdateEntity>

  fun loadUpdateWithId(id: UUID): UpdateEntity? {
    val updateEntities = _loadUpdatesWithId(id)
    return if (updateEntities.isNotEmpty()) updateEntities[0] else null
  }

  fun loadLaunchAsset(id: UUID): AssetEntity {
    val assetEntity = _loadLaunchAsset(id)
    assetEntity.isLaunchAsset = true
    return assetEntity
  }

  @Insert
  abstract fun insertUpdate(update: UpdateEntity)

  fun setUpdateScopeKey(update: UpdateEntity, newScopeKey: String) {
    update.scopeKey = newScopeKey
    _updateUpdate(update)
  }

  @Transaction
  open fun markUpdateFinished(update: UpdateEntity, hasSkippedEmbeddedAssets: Boolean) {
    var statusToMark = UpdateStatus.READY
    if (update.status === UpdateStatus.DEVELOPMENT) {
      statusToMark = UpdateStatus.DEVELOPMENT
    } else if (hasSkippedEmbeddedAssets) {
      statusToMark = UpdateStatus.EMBEDDED
    }
    _markUpdateWithStatus(statusToMark, update.id)
    _keepUpdate(update.id)
  }

  fun markUpdateFinished(update: UpdateEntity) {
    markUpdateFinished(update, false)
  }

  fun markUpdateAccessed(update: UpdateEntity) {
    update.lastAccessed = Date()
    _updateUpdate(update)
  }

  fun markUpdatesWithMissingAssets(missingAssets: List<AssetEntity>) {
    val missingAssetIds = mutableListOf<Long>()
    for (asset in missingAssets) {
      missingAssetIds.add(asset.id)
    }
    _markUpdatesWithMissingAssets(missingAssetIds, UpdateStatus.PENDING)
  }

  @Delete
  abstract fun deleteUpdates(updates: List<UpdateEntity>)
}
