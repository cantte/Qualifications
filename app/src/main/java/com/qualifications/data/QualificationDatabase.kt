package com.qualifications.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.qualifications.model.Activity
import com.qualifications.model.Qualification
import com.qualifications.model.Subject

private const val SUBJECTS_TABLE_NAME = "subjects"
private const val QUALIFICATIONS_DATABASE_NAME = "qualifications"
private const val ACTIVITIES_TABLE_NAME = "activities"

class QualificationDatabase(private val context: Context) {
    private val databaseConnection: SQLiteDatabase = context.openOrCreateDatabase(
        QUALIFICATIONS_DATABASE_NAME , Context.MODE_PRIVATE , null
    )

    init {
        databaseConnection.execSQL("CREATE TABLE IF NOT EXISTS $SUBJECTS_TABLE_NAME(code TEXT PRIMARY KEY, name TEXT, definitive DECIMAL)")
        databaseConnection.execSQL(
            "CREATE TABLE IF NOT EXISTS $QUALIFICATIONS_DATABASE_NAME(id INTEGER PRIMARY KEY AUTOINCREMENT, cort INTEGER, subject_code TEXT, " +
                    "FOREIGN KEY(subject_code) REFERENCES $SUBJECTS_TABLE_NAME(code))"
        )
        databaseConnection.execSQL(
            "CREATE TABLE IF NOT EXISTS $ACTIVITIES_TABLE_NAME(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, note REAL, percent REAL, qualification_id INTEGER, " +
                    "FOREIGN KEY(qualification_id) REFERENCES $QUALIFICATIONS_DATABASE_NAME(id))"
        )
    }

    fun saveSubject(subject: Subject): Boolean {
        val subjectValues = ContentValues()
        subjectValues.put("code" , subject.code)
        subjectValues.put("name" , subject.name)
        subjectValues.put("definitive" , subject.definitive)

        return if (databaseConnection.insert(SUBJECTS_TABLE_NAME , null , subjectValues) > 0) {
            subject.qualifications.forEach {
                val qualificationContentValues = ContentValues()
                qualificationContentValues.put("cort" , it.cort)
                qualificationContentValues.put("subject_code" , subject.code)

                databaseConnection.insert(
                    QUALIFICATIONS_DATABASE_NAME ,
                    null ,
                    qualificationContentValues
                )
            }

            true
        } else false
    }

    fun getSubjects(): ArrayList<Subject> {
        val subjectCursor: Cursor =
            databaseConnection.rawQuery("SELECT * FROM $SUBJECTS_TABLE_NAME" , null)
        val subjects: ArrayList<Subject> = ArrayList()

        if (subjectCursor.count > 0) {
            subjectCursor.moveToFirst()
            do {
                val subject = Subject(subjectCursor.getString(0) , subjectCursor.getString(1))
                setSubjectQualifications(subject)
                subjects.add(subject)
            } while (subjectCursor.moveToNext())
        }

        return subjects
    }

    fun getSubject(code: String): Subject? {
        val subjectCursor: Cursor = databaseConnection.rawQuery(
            "SELECT * FROM $SUBJECTS_TABLE_NAME WHERE code = ?" ,
            arrayOf(code)
        )

        if (subjectCursor.count == 0)
            return null

        val subject = Subject(subjectCursor.getString(0) , subjectCursor.getString(1))
        setSubjectQualifications(subject)
        return subject
    }

    private fun setSubjectQualifications(subject: Subject) {
        val qualificationCursor = databaseConnection.rawQuery(
            "SELECT * FROM $QUALIFICATIONS_DATABASE_NAME WHERE subject_code = ?" ,
            arrayOf(subject.code)
        )
        if (qualificationCursor.count > 0) {
            qualificationCursor.moveToFirst()
            do {
                val qualification = Qualification()
                qualification.id = qualificationCursor.getInt(0)
                qualification.cort = qualificationCursor.getInt(1)

                qualification.activities = getQualificationActivities(qualification.id)

                //subject.addQualification(qualification)
            } while (qualificationCursor.moveToNext())
        }
    }

    private fun getQualificationActivities(qualificationId: Int): ArrayList<Activity> {
        val activitiesCursor = databaseConnection.rawQuery(
            "SELECT * FROM $ACTIVITIES_TABLE_NAME WHERE qualification_id = ?" ,
            arrayOf(qualificationId.toString())
        )
        val activities: ArrayList<Activity> = ArrayList()

        if (activitiesCursor.count > 0) {
            activitiesCursor.moveToFirst()
            do {
                val activity = Activity()
                activity.id = activitiesCursor.getInt(0)
                activity.name = activitiesCursor.getString(1)
                activity.note = activitiesCursor.getFloat(2)
                activity.percent = activitiesCursor.getFloat(3)

                activities.add(activity)
            } while (activitiesCursor.moveToNext())
        }

        return activities
    }

    fun saveActivity(activity: Activity , qualificationId: Int): Boolean {
        val activityContentValues = ContentValues()

        activityContentValues.put("name" , activity.name)
        activityContentValues.put("note" , activity.note)
        activityContentValues.put("percent" , activity.percent)
        activityContentValues.put("qualification_id" , qualificationId)

        return databaseConnection.insert(ACTIVITIES_TABLE_NAME , null , activityContentValues) > 0
    }

    fun updateActivity(activity: Activity): Boolean {
        val activityContentValues = ContentValues()

        activityContentValues.put("name" , activity.name)
        activityContentValues.put("note" , activity.note)
        activityContentValues.put("percent" , activity.percent)

        return databaseConnection.update(
            ACTIVITIES_TABLE_NAME ,
            activityContentValues ,
            "id = ?" ,
            arrayOf(activity.id.toString())
        ) > 0
    }

    fun updateSubject(subject: Subject): Boolean {
        val subjectContentValues = ContentValues()

        subjectContentValues.put("name" , subject.name)

        return databaseConnection.update(
            SUBJECTS_TABLE_NAME ,
            subjectContentValues ,
            "code = ?" ,
            arrayOf(subject.code)
        ) > 0
    }

    fun deleteActivity(activityCode: Int): Boolean {
        return databaseConnection.delete(
            ACTIVITIES_TABLE_NAME ,
            "id = ?" ,
            arrayOf(activityCode.toString())
        ) > 0
    }

    fun deleteSubject(subjectCode: String): Boolean {
        return databaseConnection.delete(
            SUBJECTS_TABLE_NAME ,
            "code = ?" ,
            arrayOf(subjectCode)
        ) > 0
    }
}