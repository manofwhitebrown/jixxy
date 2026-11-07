package app.jixxy.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.PauseRuntime
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [StateStore] on plain SQLite (android.database): no extra libraries or annotation processors, and it supports
 * the range query [eventsBetween]. Events are purged after [BrainConfig.retentionDays]; [clearEvents] deletes
 * every row and vacuums so nothing remains in the file. Nothing leaves the device.
 *
 * @param isBlind when true, [logEvent] drops the event (blind mode = no logging).
 */
class SqliteStateStore(
    context: Context,
    private val isBlind: () -> Boolean = { false },
    private val now: () -> Long = System::currentTimeMillis,
) : StateStore {

    private val helper = object : SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE events (id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER NOT NULL, " +
                    "type TEXT NOT NULL, pkg TEXT, value INTEGER, line_id TEXT)"
            )
            db.execSQL("CREATE INDEX idx_events_ts ON events(ts)")
            db.execSQL(
                "CREATE TABLE brain_state (id INTEGER PRIMARY KEY CHECK (id = 1), energy INTEGER NOT NULL, " +
                    "mood TEXT NOT NULL, bond_xp INTEGER NOT NULL, first_seen_ts INTEGER, " +
                    "last_unprompted_ts INTEGER, unprompted_day_epoch INTEGER, unprompted_today INTEGER NOT NULL, " +
                    "pause_json TEXT)"
            )
        }

        /** v1 -> v2 (D-017): Pause grants/timers survive process death. Existing rows get NULL, which decodes to the default. */
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) db.execSQL("ALTER TABLE brain_state ADD COLUMN pause_json TEXT")
        }
    }

    private var writesSincePurge = 0

    override suspend fun load(): BrainState = withContext(Dispatchers.IO) {
        helper.readableDatabase.rawQuery("SELECT * FROM brain_state WHERE id = 1", null).use { c ->
            if (!c.moveToFirst()) return@use BrainState()
            fun longOrNull(col: String) = c.getColumnIndexOrThrow(col).let { if (c.isNull(it)) null else c.getLong(it) }
            BrainState(
                energy = c.getInt(c.getColumnIndexOrThrow("energy")),
                mood = Mood.values().firstOrNull { it.name == c.getString(c.getColumnIndexOrThrow("mood")) } ?: Mood.CHILL,
                bondXp = c.getInt(c.getColumnIndexOrThrow("bond_xp")),
                firstSeenTs = longOrNull("first_seen_ts"),
                lastUnpromptedTs = longOrNull("last_unprompted_ts"),
                unpromptedDayEpoch = longOrNull("unprompted_day_epoch"),
                unpromptedToday = c.getInt(c.getColumnIndexOrThrow("unprompted_today")),
                pause = PauseRuntime.decode(c.getColumnIndexOrThrow("pause_json").let { if (c.isNull(it)) null else c.getString(it) }),
            )
        }
    }

    override suspend fun save(state: BrainState) = withContext(Dispatchers.IO) {
        val v = ContentValues().apply {
            put("id", 1)
            put("energy", state.energy)
            put("mood", state.mood.name)
            put("bond_xp", state.bondXp)
            put("first_seen_ts", state.firstSeenTs)
            put("last_unprompted_ts", state.lastUnpromptedTs)
            put("unprompted_day_epoch", state.unpromptedDayEpoch)
            put("unprompted_today", state.unpromptedToday)
            put("pause_json", state.pause.encode())
        }
        helper.writableDatabase.insertWithOnConflict("brain_state", null, v, SQLiteDatabase.CONFLICT_REPLACE)
        Unit
    }

    override suspend fun logEvent(e: UsageEvent) {
        if (isBlind()) return
        withContext(Dispatchers.IO) {
            val v = ContentValues().apply {
                put("ts", e.ts)
                put("type", e.type.name)
                put("pkg", e.pkg)
                put("value", e.value)
                put("line_id", e.lineId)
            }
            val db = helper.writableDatabase
            db.insert("events", null, v)
            if (++writesSincePurge >= PURGE_EVERY) {
                writesSincePurge = 0
                purgeOld(db)
            }
        }
    }

    override suspend fun eventsBetween(fromTs: Long, toTs: Long): List<UsageEvent> = withContext(Dispatchers.IO) {
        helper.readableDatabase.rawQuery(
            "SELECT ts, type, pkg, value, line_id FROM events WHERE ts >= ? AND ts < ? ORDER BY ts ASC, id ASC",
            arrayOf(fromTs.toString(), toTs.toString()),
        ).use { c ->
            buildList {
                while (c.moveToNext()) {
                    val type = UsageEventType.values().firstOrNull { it.name == c.getString(1) } ?: continue
                    add(
                        UsageEvent(
                            ts = c.getLong(0), type = type,
                            pkg = if (c.isNull(2)) null else c.getString(2),
                            value = if (c.isNull(3)) null else c.getInt(3),
                            lineId = if (c.isNull(4)) null else c.getString(4),
                        )
                    )
                }
            }
        }
    }

    override suspend fun clearEvents() = withContext(Dispatchers.IO) {
        val db = helper.writableDatabase
        db.delete("events", null, null)
        db.execSQL("VACUUM")  // rewrite the file so deleted rows do not linger on disk
    }

    /** Removes events older than the retention window. Also run at app start by the owner of this store. */
    suspend fun purgeExpired() = withContext(Dispatchers.IO) { purgeOld(helper.writableDatabase) }

    private fun purgeOld(db: SQLiteDatabase) {
        db.delete("events", "ts < ?", arrayOf(retentionCutoff(now()).toString()))
    }

    private companion object {
        const val DB_NAME = "jixxy.db"
        const val DB_VERSION = 2
        const val PURGE_EVERY = 200
    }
}
