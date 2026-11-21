package com.ute.studentmvvm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ute.studentmvvm.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ute_student_room.db"
                )
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.studentDao())
                }
            }
        }

        suspend fun populateDatabase(studentDao: StudentDao) {
            val initialStudents = listOf(
                Student(
                    fullName = "Đinh Xuân Hoàng",
                    studentCode = "2415053122114",
                    email = "hoang.dx@ute.udn.vn",
                    gpa = 3.76,
                    major = "Công Nghệ Thông Tin"
                ),
                Student(
                    fullName = "Nguyễn Văn An",
                    studentCode = "2415053122115",
                    email = "an.nv@ute.udn.vn",
                    gpa = 3.50,
                    major = "Công Nghệ Thông Tin"
                ),
                Student(
                    fullName = "Trần Thị Bích",
                    studentCode = "2415053122116",
                    email = "bich.tt@ute.udn.vn",
                    gpa = 3.82,
                    major = "Công Nghệ Thông Tin"
                ),
                Student(
                    fullName = "Lê Hoàng Cường",
                    studentCode = "2415053122117",
                    email = "cuong.lh@ute.udn.vn",
                    gpa = 3.25,
                    major = "Công Nghệ Thông Tin"
                )
            )
            studentDao.insertAll(initialStudents)
        }
    }
}
