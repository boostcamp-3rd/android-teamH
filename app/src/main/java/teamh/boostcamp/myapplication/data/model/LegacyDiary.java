package teamh.boostcamp.myapplication.data.model;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * 일기 아이템 데이터를 가지는 DataClass */
@Entity(tableName = "diary")
public class LegacyDiary {

    // DB 에서 인식하기 위한 PK
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private final int id;

    // 저장한 날
    @ColumnInfo(name = "recordDate")
    @NonNull
    private final String recordDate;

    // 저장된 파일 경로
    @ColumnInfo(name = "recordFilePath")
    @NonNull
    private final String recordFilePath;

    // 설정한 태그들
    @ColumnInfo(name = "tags")
    private final String tags;

    // 선택한 감정 번호
    @ColumnInfo(name = "selectedEmotion")
    private final int selectedEmotion;

    // API 를 통해 분석된 감정
    @ColumnInfo(name = "analyzedEmotion")
    private final int analyzedEmotion;

    // TimeStamp 추가
    @ColumnInfo(name = "timeStamp")
    private final long timeStamp;

    public LegacyDiary(int id,
                       @NonNull String recordDate,
                       @NonNull String recordFilePath,
                       @NonNull String tags,
                       final int selectedEmotion,
                       final int analyzedEmotion,
                       final long timeStamp) {
        this.id = id;
        this.recordDate = recordDate;
        this.recordFilePath = recordFilePath;
        this.tags = tags;
        this.selectedEmotion = selectedEmotion;
        this.analyzedEmotion = analyzedEmotion;
        this.timeStamp = timeStamp;
    }

    /*최대한 안바뀌게 -> final */
    public int getId() {
        return id;
    }

    @NonNull
    public String getRecordDate() {
        return recordDate;
    }

    @NonNull
    public String getTags() {
        return tags;
    }

    @NonNull
    public String getRecordFilePath() {
        return recordFilePath;
    }

    public int getSelectedEmotion() {
        return selectedEmotion;
    }

    public int getAnalyzedEmotion() {
        return analyzedEmotion;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public static LegacyDiary[] generateSampleDiaryData() {
        String filePath = "/storage/emulated/0/2019-02-08.acc";
        File file = new File("/storage/emulated/0/2019-02-08.acc");

        if (!file.exists()) {
            try {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    Log.e("Test", "파일 생성 실패");
                } else {
                    Log.e("Test", "파일 생성 성공");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Random random = new Random();

        List<LegacyDiary> samples = new ArrayList<>();

        for (int i = 1; i <= 20; ++i) {
            samples.add(new LegacyDiary(
                    i,
                    String.format(Locale.getDefault(), "2019-01-%2d", i),
                    filePath,
                    String.format(Locale.getDefault(), "#%d번", i),
                    Math.abs(random.nextInt() % 5),
                    Math.abs(random.nextInt() % 5),
                    new Date().getTime() / 1000 + i * 10
            ));
        }

        LegacyDiary[] temp = new LegacyDiary[samples.size()];
        return temp;
    }
}
