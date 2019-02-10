package teamh.boostcamp.myapplication.data.local.room.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Single;
import teamh.boostcamp.myapplication.data.model.Diary;
import teamh.boostcamp.myapplication.data.model.Emotion;
import teamh.boostcamp.myapplication.data.model.RecallEntity;

@Dao
public interface RecallDao {

    @Query("Select * FROM recall ORDER BY createdDate")
    Single<List<RecallEntity>> loadRecallEntity();

    @Query("Select * FROM diary WHERE selectedEmotion = :emotion ORDER BY recordDate LIMIT 5")
    List<Diary> selectDiary(Emotion emotion);
}
