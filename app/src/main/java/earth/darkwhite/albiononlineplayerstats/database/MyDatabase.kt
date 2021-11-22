package earth.darkwhite.albiononlineplayerstats.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import earth.darkwhite.albiononlineplayerstats.database.dataconverter.KDDataConverter
import earth.darkwhite.albiononlineplayerstats.database.model.ReportDataIn
import earth.darkwhite.albiononlineplayerstats.database.model.Player
import earth.darkwhite.albiononlineplayerstats.database.model.ReportContainer

@Database(entities = [Player::class, ReportContainer::class, ReportDataIn::class], version = 1, exportSchema = false)
@TypeConverters(KDDataConverter::class)
abstract class MyDatabase : RoomDatabase() {

    abstract val databaseMyDao: MyDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java,
                        "AlbionOnlinePlayerStats"
                    )
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}