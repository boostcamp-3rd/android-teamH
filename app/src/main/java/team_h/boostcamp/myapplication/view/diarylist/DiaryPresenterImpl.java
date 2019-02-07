package team_h.boostcamp.myapplication.view.diarylist;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import team_h.boostcamp.myapplication.R;
import team_h.boostcamp.myapplication.data.remote.deepaffects.request.EmotionAnalyzeRequest;
import team_h.boostcamp.myapplication.data.repository.DiaryRepository;
import team_h.boostcamp.myapplication.model.Diary;

public class DiaryPresenterImpl implements DiaryContract.Presenter {

    private static final String TAG = "DiaryPresenterImpl";

    public final ObservableBoolean isSaving = new ObservableBoolean(false);

    private DiaryContract.View view;

    private DiaryRepository diaryRepository;
    private DiaryRecorder diaryRecorderImpl;

    private CompositeDisposable compositeDisposable;

    private int selectedEmotion = -1;
    private int currentIdx = 0;
    private boolean isRecording = false;
    private boolean isLoadingItem = false;

    DiaryPresenterImpl(@NonNull DiaryContract.View view,
                       @NonNull DiaryRepository diaryRepository,
                       @NonNull DiaryRecorder diaryRecorderImpl) {
        this.view = view;
        this.diaryRepository = diaryRepository;
        this.diaryRecorderImpl = diaryRecorderImpl;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onViewDetached() {
        // 화면 종료시 리소스 해제
        diaryRecorderImpl.releaseRecorder();
        // 만약 구독중인 비동기 작업이 있다면 해제
        compositeDisposable.clear();
        isSaving.set(false);
    }


    /* Method Reference 를 통해 Emotion Image Click Event binding */
    public void emotionChanged(final int id) {
        switch (id) {
            case R.id.tv_record_item_mad:
                Log.e(TAG, "mad");
                selectedEmotion = 0;
                break;

            case R.id.tv_record_item_bad:
                Log.e(TAG, "bad");
                selectedEmotion = 1;
                break;

            case R.id.tv_record_item_normal:
                Log.e(TAG, "normal");
                selectedEmotion = 2;
                break;

            case R.id.tv_record_item_pgood:
                Log.e(TAG, "pgood");
                selectedEmotion = 3;
                break;

            case R.id.tv_record_item_good:
                Log.e(TAG, "good");
                selectedEmotion = 4;
                break;
        }
    }

    @Override
    public void recordDiaryItem() {
        if (isRecording) {
            // 녹음중이면 종료
            Log.d(TAG, "녹음 종료");
            diaryRecorderImpl.finishRecord();
            isRecording = false;
        } else {
            // 녹음 시작
            Log.d(TAG, "녹음 시작");
            diaryRecorderImpl.startRecord();
            isRecording = true;
        }
        // isRecording = !isRecording;
    }

    @Override
    public void saveDiaryItem(final List<String> tags) {

        // 현재 녹음중이면 거부
        if (isRecording) {
            view.showRecordNotFinishedMessage();
            return;
        }

        // 감정을 선택하지 않았다면
        if (selectedEmotion == -1) {
            view.showEmotionNotSelectedMessage();
            return;
        }

        final File file = new File(diaryRecorderImpl.getFilePath());

        // 저장된 Record File 이 없다면
        if (!file.exists()) {
            view.showNoRecordFileMessage();
            return;
        }

        // 키패드 닫기
        view.closeHashTagKeyPad();

        // 저장 시작
        isSaving.set(true);

        // 분석에 필요한 데이터들
        final String encodedRecordFile = getBase64EncodedFile(file);
        final EmotionAnalyzeRequest request = new EmotionAnalyzeRequest(encodedRecordFile);

        // 분석 후 저장
        compositeDisposable.add(diaryRepository.analyzeVoiceEmotion(request)
                .doOnError(throwable -> view.showEmotionAnalyzeFailMessage())
                .map(analyzedEmotion -> new Diary(0,
                        file.getName().split("\\.")[0],
                        diaryRecorderImpl.getFilePath(),
                        tags.toString(),
                        selectedEmotion,
                        analyzedEmotion))
                .flatMapCompletable(diaryRepository::insertRecordItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            Log.d(TAG, "저장 성공");
                            view.showDiaryItemSaved();
                            isSaving.set(false);
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            Log.d(TAG, "저장 실패");
                            view.showDiaryItemSaveFail();
                            isSaving.set(false);
                        }
                ));
    }

    @Override
    public void loadMoreDiaryItems() {
        if (!isLoadingItem) {
            isLoadingItem = true;
            compositeDisposable.add(diaryRepository.loadMoreDiaryItems(currentIdx)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(diaryList -> {
                                if (diaryList.size() != 0) {
                                    currentIdx = diaryList.get(diaryList.size() - 1).getId();
                                }
                                // 받아온 데이터 넘겨주기
                                view.showMoreDiaryItems(diaryList);
                            }
                            , throwable -> {
                                Log.e(TAG, "Diary Fragment Load 에서 발생");
                                throwable.printStackTrace();
                            }));
        }
    }

    // API 분석을 위해 녹음 파일을 Base64 Encoding 수행
    @NonNull
    private String getBase64EncodedFile(@NonNull final File file) {

        final byte[] encodedByteArray = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            final int readBytes = fileInputStream.read(encodedByteArray);

            if (readBytes == 0) {
                Log.d(TAG, "0바이트 읽음");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Base64.encodeToString(encodedByteArray, Base64.DEFAULT);
    }
}