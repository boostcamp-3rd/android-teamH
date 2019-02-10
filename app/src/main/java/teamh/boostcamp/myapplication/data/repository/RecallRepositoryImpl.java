package teamh.boostcamp.myapplication.data.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import teamh.boostcamp.myapplication.data.local.room.dao.RecallDao;
import teamh.boostcamp.myapplication.data.model.Emotion;
import teamh.boostcamp.myapplication.data.model.Recall;
import teamh.boostcamp.myapplication.data.model.RecallEntity;

public class RecallRepositoryImpl implements RecallRepository {

    private static RecallRepositoryImpl INSTANCE;
    private RecallDao recallDao;

    private RecallRepositoryImpl(RecallDao recallDao) {
        this.recallDao = recallDao;
    }

    public static RecallRepositoryImpl getInstance(RecallDao recallDao) {
        if (INSTANCE == null) {
            synchronized (RecallRepositoryImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecallRepositoryImpl(recallDao);
                }
            }
        }
        return INSTANCE;
    }

    public Single<List<Recall>> loadRecallList() {
        return recallDao.loadRecallEntity()
                .map(recallEntities -> {

                    List<Recall> recallList = new ArrayList<>();
                    for (int i = 0; i < recallEntities.size(); i++) {

                        RecallEntity recallEntity = recallEntities.get(i);

                        Date startDate = generateStartDate(recallEntity.getCreatedDate());

                        Recall recall = new Recall(startDate,
                                recallEntity.getCreatedDate(),
                                recallEntity.getEmotion(),
                                recallDao.selectDiary(recallEntity.getEmotion()));
                        recallList.add(recall);
                    }

                    return recallList;
                }).subscribeOn(Schedulers.io());
    }

    private Date generateStartDate(Date endDate){
        return new Date(endDate.getTime() - 14 * 24 * 60 * 60 * 1000);
    }
}
