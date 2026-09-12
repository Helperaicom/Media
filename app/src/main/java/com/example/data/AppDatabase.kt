package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VideoEntity::class, CommentEntity::class, UserAccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "creator_stream_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: AppDao) {
            // Seed initial User Account
            dao.insertOrUpdateUser(
                UserAccountEntity(
                    id = 1,
                    name = "Alex Rivera",
                    email = "alex.viewer@example.com",
                    isCreator = false,
                    subscriptionTier = "FREE",
                    subscriptionExpiryMillis = 0L,
                    isSubscribed = false
                )
            )

            // Seed Sample Initial Videos
            val sampleVideos = listOf(
                VideoEntity(
                    title = "Building Modern Full-Stack Android Apps (Masterclass)",
                    description = "In this comprehensive masterclass, we dive deep into Jetpack Compose, Clean Architecture, Room Database, and real-time media streaming. Perfect for intermediate and advanced developers.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80",
                    duration = "24:15",
                    category = "Masterclass",
                    isPremium = false,
                    viewsCount = 18420,
                    likesCount = 1430,
                    isLiked = false,
                    isDownloaded = false,
                    downloadSizeMb = 142.5,
                    createdAt = System.currentTimeMillis() - (86400000L * 2),
                    creatorName = "DevStudio Pro",
                    tags = "android,kotlin,compose,development"
                ),
                VideoEntity(
                    title = "Secret Filming Techniques: Cinematic Lighting & Color Grading",
                    description = "Exclusive behind-the-scenes walkthrough of how we set up our multi-camera studio, key lights, bounce diffusion, and custom LUT color grade for Netflix-ready footage.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=800&q=80",
                    duration = "32:10",
                    category = "Filmmaking",
                    isPremium = true, // VIP Exclusive
                    viewsCount = 9540,
                    likesCount = 890,
                    isLiked = true,
                    isDownloaded = false,
                    downloadSizeMb = 210.0,
                    createdAt = System.currentTimeMillis() - (86400000L * 4),
                    creatorName = "DevStudio Pro",
                    tags = "filmmaking,lighting,cinematic,exclusive"
                ),
                VideoEntity(
                    title = "How I Built a 6-Figure Creator Business From Scratch",
                    description = "Complete transparency breakdown of revenue streams: sponsorships, digital products, subscription community, software tools, and audience building strategies.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=800&q=80",
                    duration = "19:45",
                    category = "Business",
                    isPremium = true, // VIP Exclusive
                    viewsCount = 24190,
                    likesCount = 2300,
                    isLiked = false,
                    isDownloaded = false,
                    downloadSizeMb = 115.0,
                    createdAt = System.currentTimeMillis() - (86400000L * 6),
                    creatorName = "DevStudio Pro",
                    tags = "business,creator,revenue,strategy"
                ),
                VideoEntity(
                    title = "Studio Tour & Complete Equipment Rig Breakdown 2026",
                    description = "Everything I use daily: cameras, lenses, microphones, studio acoustic panels, editing desk setup, and cloud backup NAS workflow.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=800&q=80",
                    duration = "15:20",
                    category = "Behind The Scenes",
                    isPremium = false,
                    viewsCount = 12900,
                    likesCount = 1120,
                    isLiked = false,
                    isDownloaded = true, // Already downloaded sample
                    downloadSizeMb = 88.0,
                    createdAt = System.currentTimeMillis() - (86400000L * 8),
                    creatorName = "DevStudio Pro",
                    tags = "gear,setup,tech,studio"
                ),
                VideoEntity(
                    title = "Live Q&A: Code Architecture, Monetization & Audience Growth",
                    description = "Raw 45-minute subscriber session answering top community questions regarding career progression, project pitching, and handling creative burnout.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&q=80",
                    duration = "44:50",
                    category = "Exclusive Q&A",
                    isPremium = true, // VIP Exclusive
                    viewsCount = 6840,
                    likesCount = 650,
                    isLiked = false,
                    isDownloaded = false,
                    downloadSizeMb = 310.0,
                    createdAt = System.currentTimeMillis() - (86400000L * 11),
                    creatorName = "DevStudio Pro",
                    tags = "live,community,qna,exclusive"
                ),
                VideoEntity(
                    title = "5 Mistakes Every Content Creator Makes in Year One",
                    description = "Avoid these common pitfalls with audio quality, pacing, thumbnail retention, algorithm myths, and consistency schedules.",
                    videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&q=80",
                    duration = "14:10",
                    category = "Tutorial",
                    isPremium = false,
                    viewsCount = 31500,
                    likesCount = 3100,
                    isLiked = true,
                    isDownloaded = false,
                    downloadSizeMb = 75.0,
                    createdAt = System.currentTimeMillis() - (86400000L * 15),
                    creatorName = "DevStudio Pro",
                    tags = "tips,content,tutorial,advice"
                )
            )

            dao.insertAllVideos(sampleVideos)

            // Seed sample comments
            val sampleComments = listOf(
                CommentEntity(
                    videoId = 1,
                    authorName = "Jordan Tech",
                    content = "The architecture breakdown in section 3 cleared up so much confusion. Thank you!",
                    timestamp = System.currentTimeMillis() - 3600000L * 5
                ),
                CommentEntity(
                    videoId = 1,
                    authorName = "Priya Sharma",
                    content = "Subscribed immediately after this video. Best explanation of reactive UI on the internet.",
                    timestamp = System.currentTimeMillis() - 3600000L * 12
                ),
                CommentEntity(
                    videoId = 2,
                    authorName = "Elena Rostova",
                    content = "This VIP video alone is worth the entire annual subscription. The color grading LUTs are magic!",
                    timestamp = System.currentTimeMillis() - 3600000L * 8
                )
            )

            dao.insertAllComments(sampleComments)
        }
    }
}
