package net.thechance.mena.trends.data.local.database

const val DATABASE_NAME = "trends_database.db"
expect class TrendsDatabaseBuilder {
    fun build(): AppDatabase
}

fun createDatabase(builder: TrendsDatabaseBuilder): AppDatabase {
    return builder.build()
}