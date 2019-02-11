package teamh.boostcamp.myapplication.view.graph;

import java.util.Date;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import teamh.boostcamp.myapplication.data.repository.StatisticsRepository;

public class StatisticsPresenter {

    @NonNull
    private StatisticsRepository statisticsRepository;

    @NonNull
    private StatisticsView statisticsView;

    @NonNull
    private CompositeDisposable compositeDisposable;

    // 생성자를 통한 주입.
    public StatisticsPresenter(@NonNull StatisticsView statisticsView,
                               @NonNull StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
        this.statisticsView = statisticsView;
        this.compositeDisposable = new CompositeDisposable();
    }

    void loadStatisticsData() {
        /**
         * Repository에게 데이터 요청해서 받아오는 로직.
         * 그리고 view에 갱신
         * 화면이 꺼지는 상황을 대비하기 위해서 CompositeDisposable이 필요하다.
         * Detach, Destroy 상황에서 구독을 해제해줘야 한다.
         * */
      compositeDisposable.add(statisticsRepository.loadRecentEmotionHistoryList(new Date())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emotionHistoryList -> {
                    statisticsView.updateStatisticsData(emotionHistoryList);
                    statisticsView.showLoadStatisticsDataSuccessMessage();
                }, throwable -> {
                    // TODO 에러 처리
                    statisticsView.showLoadStatisticsDataFailMessage();
                }));
      //compositeDisposable.clear();
    }

    void loadTagList() {
        statisticsRepository.loadRecentCountedTagList(new Date())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countedTags -> {
                    // TODO : 뷰에 처리
                }, throwable -> {
                    // TODO : 에러 처리
                });
    }
}
