package teamh.boostcamp.myapplication.data.local.room;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import teamh.boostcamp.myapplication.data.local.room.converter.DateTypeConverter;
import teamh.boostcamp.myapplication.data.local.room.converter.EmotionTypeConverter;
import teamh.boostcamp.myapplication.data.local.room.converter.StringListTypeConverter;
import teamh.boostcamp.myapplication.data.local.room.dao.AppDao;
import teamh.boostcamp.myapplication.data.local.room.dao.DiaryDao;
import teamh.boostcamp.myapplication.data.local.room.dao.LegacyDiaryDao;
import teamh.boostcamp.myapplication.data.local.room.entity.DiaryEntity;
import teamh.boostcamp.myapplication.data.model.Emotion;
import teamh.boostcamp.myapplication.data.model.LegacyDiary;
import teamh.boostcamp.myapplication.data.model.Memory;
import teamh.boostcamp.myapplication.data.model.Recommendation;

@Database(entities = {LegacyDiary.class, Recommendation.class, Memory.class, DiaryEntity.class}, version = 5, exportSchema = false)
@TypeConverters({DateTypeConverter.class, EmotionTypeConverter.class, StringListTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "appDB.db";
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // FIXME 더미 데이터 추가
                                    final String filePath = "/storage/emulated/0/2019-02-08.acc";
                                    final File file = new File("/storage/emulated/0/2019-02-08.acc");

                                    if (!file.exists()) {
                                        try {
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Random random = new Random();

                                    List<DiaryEntity> samples = new ArrayList<>();

                                    final long TODAY = new Date().getTime();
                                    final long DAY = 86400L;

                                    for (int i = 1; i <= 20; ++i) {
                                        samples.add(new DiaryEntity(
                                                i,
                                                new Date(TODAY - DAY * i),
                                                filePath,
                                                Arrays.asList(String.format("#%2d번",i)),
                                                Emotion.fromValue(Math.abs(random.nextInt() % 5)),
                                                Emotion.fromValue(Math.abs(random.nextInt() % 5))
                                        ));
                                    }

                                    DiaryEntity[] temp = new DiaryEntity[samples.size()];

                                    Completable.fromAction(() -> INSTANCE.diaryDao()
                                            .insert(samples.toArray(temp)))
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(() -> { Log.d("Test", "데이터 저장");
                                                    },
                                                    throwable -> {throwable.printStackTrace();
                                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AppDao appDao();

    public abstract DiaryDao diaryDao();

    public abstract LegacyDiaryDao legacyDiaryDao();
}
