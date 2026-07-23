package com.Nihilisttt.LearnWord.Page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.AfterSignInFragment;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.BeforeSignInFragment;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;

import java.util.Arrays;
import java.util.Collections;

public class FirstPage extends AppCompatActivity implements View.OnClickListener {

    private Button review_button;
    private Button learn_button;
    private ImageButton user_button;
    private FrameLayout sign_in_frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
//        initData();
        intiViews();

        replaceFragment(new BeforeSignInFragment());
        user_button.setOnClickListener(this);
        learn_button.setOnClickListener(this);
        review_button.setOnClickListener(this);
        sign_in_frame_layout.setOnClickListener(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sign_in_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void intiViews() {
        learn_button = findViewById(R.id.learn_button);
        review_button = findViewById(R.id.review_button);
        user_button = findViewById(R.id.user_page);
        learn_button = findViewById(R.id.learn_button);
        review_button = findViewById(R.id.review_button);
        sign_in_frame_layout = findViewById(R.id.sign_in_frame_layout);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_page: {
                Intent intent = new Intent(this, UserPage.class);
                startActivity(intent);
                break;
            }
            case R.id.learn_button: {
                Intent intent = new Intent(this, LearnPage.class);
                startActivity(intent);
                break;
            }
            case R.id.review_button: {
                Intent intent = new Intent(this, ReviewPage.class);
                startActivity(intent);
                break;
            }
            case R.id.sign_in_frame_layout: {
                replaceFragment(new AfterSignInFragment());
                sign_in_frame_layout.setClickable(false);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    sign_in_frame_layout.setVisibility(View.INVISIBLE);
                    handler.removeCallbacksAndMessages(null);
                }, 5000);
                break;
            }
        }
    }

    private void initData() {
        WordRepository wordRepository = WordRepository.getInstance(this);
        wordRepository.deleteAll();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Word word1 = new Word.Builder()
                .addWordId("1").withMeaningIdList(Arrays.asList("0","1", "2"))
                .withCollocationIdList(Arrays.asList("1", "2", "3"))
                .withSentenceIdList(Arrays.asList("1", "2", "3"))
                .withAntonymIdList(Arrays.asList("1", "2")) // 添加AntonymWord的ID
                .withSynonymIdList(Arrays.asList("1", "2")) // 添加SynonymWord的ID
                .build();
        BasicWord basicWord1 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("練", "る"))
                .kanaComponents(Arrays.asList("ね", ""))
                .wordId("1")
                .audioUrl("audio/audio2.wav")
                .accentMark("⓪")
                .build();
        AntonymWord antonymWord1 = new AntonymWord.Builder()
                .antonymWordId("1")
                .wordId("1")
                .correspondingWordId("2")
                .kanjiComponents(Arrays.asList("操", "る"))
                .kanaComponents(Arrays.asList("あやつ", ""))
                .build();
        AntonymWord antonymWord2 = new AntonymWord.Builder()
                .antonymWordId("2")
                .wordId("1")
                .correspondingWordId("3")
                .kanjiComponents(Arrays.asList("食", "べる"))
                .kanaComponents(Arrays.asList("た", ""))
                .build();
        SynonymWord synonymWord1 = new SynonymWord.Builder()
                .synonymWordId("1")
                .wordId("1")
                .correspondingWordId("2")
                .kanjiComponents(Arrays.asList("操", "る"))
                .kanaComponents(Arrays.asList("あやつ", ""))
                .build();
        SynonymWord synonymWord2 = new SynonymWord.Builder()
                .synonymWordId("2")
                .wordId("1")
                .correspondingWordId("3")
                .kanjiComponents(Arrays.asList("食", "べる"))
                .kanaComponents(Arrays.asList("た", ""))
                .build();

        WordCollocation wordCollocation1 = new WordCollocation.Builder()
                .wordCollocationId("1")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("文", "章"), Collections.singletonList("を"), Arrays.asList("練", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶん", "しょう"), Collections.singletonList(""), Arrays.asList("ね", "")
                ))
                .wordIdList(Arrays.asList("9", "10", "1"))
                .translation("推敲文章。")
                .collocationAudioUrl("audio/audio3.wav")
                .build();
        WordCollocation wordCollocation2 = new WordCollocation.Builder()
                .wordCollocationId("2")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("対", "策"), Collections.singletonList("を"), Arrays.asList("練", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("たい", "さく"), Collections.singletonList(""), Arrays.asList("ね", "")
                ))
                .wordIdList(Arrays.asList("4", "10", "2"))
                .translation("研究对策。")
                .collocationAudioUrl("audio/audio4.wav")
                .build();
        WordCollocation wordCollocation3 = new WordCollocation.Builder()
                .wordCollocationId("3")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Collections.singletonList("腕"), Collections.singletonList("を"), Arrays.asList("練", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Collections.singletonList("うで"),
                        Collections.singletonList(""),
                        Arrays.asList("ね", "")
                ))
                .wordIdList(Arrays.asList("7", "10", "1"))
                .translation("锻炼本领")
                .collocationAudioUrl("audio/audio4.wav")
                .build();
        WordMeaning wordMeaning0 = new WordMeaning.Builder()
                .wordMeaningId("0")
                .wordId("1")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("推敲する。")
                .translationDefinition("推敲，研究")
                .build();
        WordMeaning wordMeaning1 = new WordMeaning.Builder()
                .wordMeaningId("1")
                .wordId("1")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("推敲する。")
                .translationDefinition("推敲，研究")
                .build();
        WordMeaning wordMeaning2 = new WordMeaning.Builder()
                .wordMeaningId("2")
                .wordId("1")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("鍛える。")
                .translationDefinition("锻炼")
                .build();
        WordSentence wordSentence1 = new WordSentence.Builder()
                .wordSentenceId("1")
                .wordMeaningId("1")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("文", "章"), Collections.singletonList("を"), Arrays.asList("練", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶん", "しょう"), Collections.singletonList(""), Arrays.asList("ね", "")
                ))
                .wordIdList(Arrays.asList("9", "10", "1"))
                .translation("推敲文章。")
                .build();
        WordSentence wordSentence2 = new WordSentence.Builder()
                .wordSentenceId("2")
                .wordMeaningId("1")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("対", "策"), Collections.singletonList("を"), Arrays.asList("練", "る"), Collections.singletonList("、")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("たい", "さく"), Collections.singletonList(""), Arrays.asList("ね", ""), Collections.singletonList("")
                ))
                .wordIdList(Arrays.asList("4", "10", "1","99"))
                .translation("研究对策。")
                .build();
        WordSentence wordSentence3 = new WordSentence.Builder()
                .wordSentenceId("3")
                .wordMeaningId("2")
                .wordId("1")
                .kanjiComponents(Arrays.asList(
                        Collections.singletonList("腕"), Collections.singletonList("を"), Arrays.asList("練", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Collections.singletonList("うで"),
                        Collections.singletonList(""),
                        Arrays.asList("ね", "")
                ))
                .wordIdList(Arrays.asList("7", "10", "1"))
                .translation("锻炼本领")
                .build();
        Word word2 = new Word.Builder()
                .addWordId("2").withMeaningIdList(Arrays.asList("3", "4"))
                .withCollocationIdList(Arrays.asList("4", "5", "6"))
                .withSentenceIdList(Arrays.asList("4", "5", "6"))
                .build();
        BasicWord basicWord2 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("操", "る"))
                .kanaComponents(Arrays.asList("あやつ", ""))
                .wordId("2")
                .audioUrl("audio/audio3.wav")
                .accentMark("⓪")
                .build();
        WordCollocation wordCollocation4 = new WordCollocation.Builder()
                .wordCollocationId("4")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("物", "価"), Collections.singletonList("を"), Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶっ", "か"), Collections.singletonList(""), Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("1", "10", "2"))
                .translation("操控物价。")
                .build();
        WordCollocation wordCollocation5 = new WordCollocation.Builder()
                .wordCollocationId("5")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("世", "論"), Collections.singletonList("を"), Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("よ", "ろん"), Collections.singletonList(""), Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("4", "10", "2"))
                .translation("操纵舆论。")
                .build();
        WordCollocation wordCollocation6 = new WordCollocation.Builder()
                .wordCollocationId("6")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("英", "語"), Collections.singletonList("を"),
                        Arrays.asList("上", "手"), Collections.singletonList("に"),
                        Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("えい", "ご"),
                        Collections.singletonList(""),
                        Arrays.asList("じょう", "ず"),
                        Collections.singletonList(""),
                        Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("7", "10", "10", "10", "2"))
                .translation("擅长英语")
                .build();
        WordMeaning wordMeaning3 = new WordMeaning.Builder()
                .wordMeaningId("3")
                .wordId("2")
                .partOfSpeech(Constants.PartOfSpeech.NOUN)
                .originalDefinition("陰にあって、他人を思いい通りに動かす。")
                .translationDefinition("[暗地里]操控、控制")
                .build();
        WordMeaning wordMeaning4 = new WordMeaning.Builder()
                .wordMeaningId("4")
                .wordId("2")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("言葉を巧みに使う。")
                .translationDefinition("操，掌握")
                .build();
        WordSentence wordSentence4 = new WordSentence.Builder()
                .wordSentenceId("4")
                .wordMeaningId("3")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("物", "価"), Collections.singletonList("を"), Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶっ", "か"), Collections.singletonList(""), Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("1", "10", "2"))
                .translation("操控物价。")
                .build();
        WordSentence wordSentence5 = new WordSentence.Builder()
                .wordSentenceId("5")
                .wordMeaningId("3")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("世", "論"), Collections.singletonList("を"), Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("よ", "ろん"), Collections.singletonList(""), Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("4", "10", "2"))
                .translation("操纵舆论。")
                .build();
        WordSentence wordSentence6 = new WordSentence.Builder()
                .wordSentenceId("6")
                .wordMeaningId("4")
                .wordId("2")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("世", "論"), Collections.singletonList("を"), Arrays.asList("操", "る")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("よ", "ろん"), Collections.singletonList(""), Arrays.asList("あやつ", "")
                ))
                .wordIdList(Arrays.asList("4", "10", "2"))
                .translation("操纵舆论。")
                .build();

        // 新增单词3 - 食べる
        Word word3 = new Word.Builder()
                .addWordId("3").withMeaningIdList(Arrays.asList("5", "6"))
                .withCollocationIdList(Arrays.asList("7", "8", "9"))
                .withSentenceIdList(Arrays.asList("7", "8", "9"))
                .build();

        BasicWord basicWord3 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("食", "べる"))
                .kanaComponents(Arrays.asList("た", ""))
                .wordId("3")
                .accentMark("⓪")
                .build();

        WordCollocation wordCollocation7 = new WordCollocation.Builder()
                .wordCollocationId("7")
                .wordId("3")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("早", "飯"), Collections.singletonList("を"), Arrays.asList("食", "べる")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("はや", "めし"), Collections.singletonList(""), Arrays.asList("た", "")
                ))
                .wordIdList(Arrays.asList("10", "9", "3"))
                .translation("吃早饭")
                .build();

        WordCollocation wordCollocation8 = new WordCollocation.Builder()
                .wordCollocationId("8")
                .wordId("3")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("生", "魚"), Collections.singletonList("を"), Arrays.asList("食", "べる")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("なま", "ざかな"), Collections.singletonList(""), Arrays.asList("た", "")
                ))
                .wordIdList(Arrays.asList("13", "9", "3"))
                .translation("吃生鱼")
                .build();

        WordMeaning wordMeaning5 = new WordMeaning.Builder()
                .wordMeaningId("5")
                .wordId("3")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("食物を噛み砕き体内に取り込む")
                .translationDefinition("吃")
                .build();

        WordMeaning wordMeaning6 = new WordMeaning.Builder()
                .wordMeaningId("6")
                .wordId("3")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("生活の糧を得る")
                .translationDefinition("谋生")
                .build();

        WordSentence wordSentence7 = new WordSentence.Builder()
                .wordSentenceId("7")
                .wordMeaningId("5")
                .wordId("3")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("日", "本", "料", "理"), Collections.singletonList("を"), Arrays.asList("食", "べる")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("に", "ほん", "りょう", "り"), Collections.singletonList(""), Arrays.asList("た", "")
                ))
                .wordIdList(Arrays.asList("16", "9", "3"))
                .translation("吃日本料理")
                .build();

// 新增单词4 - 書く
        Word word4 = new Word.Builder()
                .addWordId("4").withMeaningIdList(Arrays.asList("7", "8"))
                .withCollocationIdList(Arrays.asList("10", "11", "12"))
                .withSentenceIdList(Arrays.asList("10", "11", "12"))
                .build();

        BasicWord basicWord4 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("書", "く"))
                .kanaComponents(Arrays.asList("か", ""))
                .wordId("4")
                .accentMark("①")
                .build();

        WordCollocation wordCollocation10 = new WordCollocation.Builder()
                .wordCollocationId("10")
                .wordId("4")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("手", "紙"), Collections.singletonList("を"), Arrays.asList("書", "く")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("て", "がみ"), Collections.singletonList(""), Arrays.asList("か", "")
                ))
                .wordIdList(Arrays.asList("19", "9", "4"))
                .translation("写信")
                .build();

        WordMeaning wordMeaning7 = new WordMeaning.Builder()
                .wordMeaningId("7")
                .wordId("4")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("文字を記す")
                .translationDefinition("书写")
                .build();

        WordMeaning wordMeaning8 = new WordMeaning.Builder()
                .wordMeaningId("8")
                .wordId("4")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("文学作品を創作する")
                .translationDefinition("写作")
                .build();

        // 新增单词5 - 話す (はなす)
        Word word5 = new Word.Builder()
                .addWordId("5").withMeaningIdList(Arrays.asList("9", "10"))
                .withCollocationIdList(Arrays.asList("13", "14"))
                .withSentenceIdList(Arrays.asList("13", "14"))
                .build();

        BasicWord basicWord5 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("話", "す"))
                .kanaComponents(Arrays.asList("はな", ""))
                .wordId("5")
                .accentMark("⓪")
                .build();

        WordCollocation wordCollocation13 = new WordCollocation.Builder()
                .wordCollocationId("13")
                .wordId("5")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("日", "本", "語"), Collections.singletonList("を"), Arrays.asList("話", "す")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("に", "ほん", "ご"), Collections.singletonList(""), Arrays.asList("はな", "")
                ))
                .wordIdList(Arrays.asList("22", "9", "5"))
                .translation("说日语")
                .build();

        WordCollocation wordCollocation14 = new WordCollocation.Builder()
                .wordCollocationId("14")
                .wordId("5")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("真", "実"), Collections.singletonList("を"), Arrays.asList("話", "す")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("しん", "じつ"), Collections.singletonList(""), Arrays.asList("はな", "")
                ))
                .wordIdList(Arrays.asList("25", "9", "5"))
                .translation("说出真相")
                .build();

        WordMeaning wordMeaning9 = new WordMeaning.Builder()
                .wordMeaningId("9")
                .wordId("5")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("声に出して言う")
                .translationDefinition("说话")
                .build();

        WordMeaning wordMeaning10 = new WordMeaning.Builder()
                .wordMeaningId("10")
                .wordId("5")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("事柄について述べる")
                .translationDefinition("谈论")
                .build();

        WordSentence wordSentence13 = new WordSentence.Builder()
                .wordSentenceId("13")
                .wordMeaningId("9")
                .wordId("5")
                .kanjiComponents(Arrays.asList(
                        Collections.singletonList("彼"), Collections.singletonList("は"),
                        Arrays.asList("大", "声"), Collections.singletonList("で"),
                        Arrays.asList("話", "す")
                ))
                .kanaComponents(Arrays.asList(
                        Collections.singletonList("かれ"), Collections.singletonList(""),
                        Arrays.asList("おお", "ごえ"), Collections.singletonList(""),
                        Arrays.asList("はな", "")
                ))
                .wordIdList(Arrays.asList("28", "29", "30", "31", "5"))
                .translation("他大声说话。")
                .build();

        WordSentence wordSentence14 = new WordSentence.Builder()
                .wordSentenceId("14")
                .wordMeaningId("10")
                .wordId("5")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("将", "来"), Collections.singletonList("の"),
                        Collections.singletonList("夢"), Collections.singletonList("を"),
                        Arrays.asList("話", "す")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("しょう", "らい"), Collections.singletonList(""),
                        Collections.singletonList("ゆめ"), Collections.singletonList(""),
                        Arrays.asList("はな", "")
                ))
                .wordIdList(Arrays.asList("33", "34", "35", "9", "5"))
                .translation("谈论未来梦想")
                .build();

// 新增单词6 - 読む (よむ)
        Word word6 = new Word.Builder()
                .addWordId("6").withMeaningIdList(Arrays.asList("11", "12"))
                .withCollocationIdList(Arrays.asList("15", "16"))
                .withSentenceIdList(Arrays.asList("15", "16"))
                .build();

        BasicWord basicWord6 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("読", "む"))
                .kanaComponents(Arrays.asList("よ", ""))
                .wordId("6")
                .accentMark("①")
                .build();

        WordCollocation wordCollocation15 = new WordCollocation.Builder()
                .wordCollocationId("15")
                .wordId("6")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("新", "聞"), Collections.singletonList("を"), Arrays.asList("読", "む")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("しん", "ぶん"), Collections.singletonList(""), Arrays.asList("よ", "")
                ))
                .wordIdList(Arrays.asList("38", "9", "6"))
                .translation("读报纸")
                .build();

        WordCollocation wordCollocation16 = new WordCollocation.Builder()
                .wordCollocationId("16")
                .wordId("6")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("空", "気"), Collections.singletonList("を"), Arrays.asList("読", "む")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("くう", "き"), Collections.singletonList(""), Arrays.asList("よ", "")
                ))
                .wordIdList(Arrays.asList("41", "9", "6"))
                .translation("察言观色")
                .build();

        WordMeaning wordMeaning11 = new WordMeaning.Builder()
                .wordMeaningId("11")
                .wordId("6")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("文字を解釈する")
                .translationDefinition("阅读")
                .build();

        WordMeaning wordMeaning12 = new WordMeaning.Builder()
                .wordMeaningId("12")
                .wordId("6")
                .partOfSpeech(Constants.PartOfSpeech.VERB)
                .originalDefinition("状況を判断する")
                .translationDefinition("解读")
                .build();

        WordSentence wordSentence15 = new WordSentence.Builder()
                .wordSentenceId("15")
                .wordMeaningId("11")
                .wordId("6")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("毎", "朝"), Collections.singletonList("、"),
                        Arrays.asList("英", "字", "新", "聞"), Collections.singletonList("を"),
                        Arrays.asList("読", "む")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("まい", "あさ"), Collections.singletonList(""),
                        Arrays.asList("えい", "じ", "しん", "ぶん"), Collections.singletonList(""),
                        Arrays.asList("よ", "")
                ))
                .wordIdList(Arrays.asList("44", "45", "46", "9", "6"))
                .translation("每天早晨读英文报纸。")
                .build();

        WordSentence wordSentence16 = new WordSentence.Builder()
                .wordSentenceId("16")
                .wordMeaningId("12")
                .wordId("6")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("彼", "女"), Collections.singletonList("は"),
                        Collections.singletonList("場"), Collections.singletonList("の"), Arrays.asList("空", "気"), Collections.singletonList("を"),
                        Arrays.asList("読", "む"), Collections.singletonList("の"), Collections.singletonList("が"),
                        Arrays.asList("得", "意"), Collections.singletonList("だ")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("かの", "じょ"), Collections.singletonList(""),
                        Collections.singletonList("ば"), Collections.singletonList(""), Arrays.asList("くう", "き"), Collections.singletonList(""),
                        Arrays.asList("よ", ""), Collections.singletonList(""), Collections.singletonList(""),
                        Arrays.asList("とく", "い"), Collections.singletonList("")
                ))
                .wordIdList(Arrays.asList("49", "50", "51", "52", "53", "9", "6", "56", "57", "58", "59"))
                .translation("她擅长察言观色。")
                .build();
// 新增单词7 - 美しい (形容词)
        Word word7 = new Word.Builder()
                .addWordId("7").withMeaningIdList(Arrays.asList("13", "14"))
                .withCollocationIdList(Arrays.asList("17", "18"))
                .withSentenceIdList(Arrays.asList("17", "18"))
                .build();

        BasicWord basicWord7 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("美", "しい"))
                .kanaComponents(Arrays.asList("うつく", ""))
                .wordId("7")
                .accentMark("③")
                .build();

        WordCollocation wordCollocation17 = new WordCollocation.Builder()
                .wordCollocationId("17")
                .wordId("7")
                .kanjiComponents(Arrays.asList(
                        Collections.singletonList("心"), Collections.singletonList("の"), Arrays.asList("美", "しい")
                ))
                .kanaComponents(Arrays.asList(
                        Collections.singletonList("こころ"), Collections.singletonList(""), Arrays.asList("うつく", "")
                ))
                .wordIdList(Arrays.asList("60", "61", "7"))
                .translation("心灵美丽")
                .build();

        WordCollocation wordCollocation18 = new WordCollocation.Builder()
                .wordCollocationId("18")
                .wordId("7")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("風", "景"), Collections.singletonList("が"), Arrays.asList("美", "しい")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ふう", "けい"), Collections.singletonList(""), Arrays.asList("うつく", "")
                ))
                .wordIdList(Arrays.asList("63", "64", "7"))
                .translation("风景优美")
                .build();

        WordMeaning wordMeaning13 = new WordMeaning.Builder()
                .wordMeaningId("13")
                .wordId("7")
                .partOfSpeech(Constants.PartOfSpeech.ADJECTIVE)
                .originalDefinition("外観が優れている")
                .translationDefinition("美丽的")
                .build();

        WordMeaning wordMeaning14 = new WordMeaning.Builder()
                .wordMeaningId("14")
                .wordId("7")
                .partOfSpeech(Constants.PartOfSpeech.ADJECTIVE)
                .originalDefinition("精神的に崇高な")
                .translationDefinition("高尚的")
                .build();

        WordSentence wordSentence17 = new WordSentence.Builder()
                .wordSentenceId("17")
                .wordMeaningId("13")
                .wordId("7")
                .kanjiComponents(Arrays.asList(
                        Collections.singletonList("桜"), Collections.singletonList("の"),
                        Collections.singletonList("花"), Collections.singletonList("が"),
                        Arrays.asList("美", "しい")
                ))
                .kanaComponents(Arrays.asList(
                        Collections.singletonList("さくら"), Collections.singletonList(""),
                        Collections.singletonList("はな"), Collections.singletonList(""),
                        Arrays.asList("うつく", "")
                ))
                .wordIdList(Arrays.asList("66", "67", "68", "69", "70"))
                .translation("樱花很美")
                .build();

// 新增单词8 - 会議 (名词)
        Word word8 = new Word.Builder()
                .addWordId("8").withMeaningIdList(Arrays.asList("15", "16"))
                .withCollocationIdList(Arrays.asList("19", "20"))
                .withSentenceIdList(Arrays.asList("19", "20"))
                .build();

        BasicWord basicWord8 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("会", "議"))
                .kanaComponents(Arrays.asList("かい", "ぎ"))
                .wordId("8")
                .accentMark("⓪")
                .build();

        WordCollocation wordCollocation19 = new WordCollocation.Builder()
                .wordCollocationId("19")
                .wordId("8")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("緊", "急"), Arrays.asList("会", "議")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("きん", "きゅう"), Arrays.asList("かい", "ぎ")
                ))
                .wordIdList(Arrays.asList("71", "8"))
                .translation("紧急会议")
                .build();

        WordCollocation wordCollocation20 = new WordCollocation.Builder()
                .wordCollocationId("20")
                .wordId("8")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("会", "議"), Collections.singletonList("を"),
                        Arrays.asList("主", "催"), Collections.singletonList("する")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("かい", "ぎ"), Collections.singletonList(""),
                        Arrays.asList("しゅ", "さい"), Collections.singletonList("")
                ))
                .wordIdList(Arrays.asList("8", "9", "75", "76"))
                .translation("举办会议")
                .build();

        WordMeaning wordMeaning15 = new WordMeaning.Builder()
                .wordMeaningId("15")
                .wordId("8")
                .partOfSpeech(Constants.PartOfSpeech.NOUN)
                .originalDefinition("公式な話し合いの場")
                .translationDefinition("会议")
                .build();

        WordMeaning wordMeaning16 = new WordMeaning.Builder()
                .wordMeaningId("16")
                .wordId("8")
                .partOfSpeech(Constants.PartOfSpeech.NOUN)
                .originalDefinition("組織的な協議機関")
                .translationDefinition("协商机构")
                .build();

        WordSentence wordSentence19 = new WordSentence.Builder()
                .wordSentenceId("19")
                .wordMeaningId("15")
                .wordId("8")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("午", "後"), Collections.singletonList("の"),
                        Arrays.asList("会", "議"), Collections.singletonList("に"),
                        Arrays.asList("出", "席"), Collections.singletonList("する")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ご", "ご"), Collections.singletonList(""),
                        Arrays.asList("かい", "ぎ"), Collections.singletonList(""),
                        Arrays.asList("しゅっ", "せき"), Collections.singletonList("")
                ))
                .wordIdList(Arrays.asList("77", "78", "8", "80", "81", "82"))
                .translation("出席下午的会议")
                .build();
        // 新增单词9 - 文章 (名词)
        Word word9 = new Word.Builder()
                .addWordId("9").withMeaningIdList(Arrays.asList("17", "18"))
                .withCollocationIdList(Arrays.asList("21", "22", "23"))
                .withSentenceIdList(Arrays.asList("20", "21"))
                .build();

        BasicWord basicWord9 = new BasicWord.Builder()
                .kanjiComponents(Arrays.asList("文", "章"))
                .kanaComponents(Arrays.asList("ぶん", "しょう"))
                .wordId("9")
                .accentMark("⓪")
                .build();

// 文章的词组搭配
        WordCollocation wordCollocation21 = new WordCollocation.Builder()
                .wordCollocationId("21")
                .wordId("9")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("文", "章"), Collections.singletonList("を"), Arrays.asList("書", "く")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶん", "しょう"), Collections.singletonList(""), Arrays.asList("か", "")
                ))
                .wordIdList(Arrays.asList("9", "10", "4"))
                .translation("撰写文章")
                .build();

        WordCollocation wordCollocation22 = new WordCollocation.Builder()
                .wordCollocationId("22")
                .wordId("9")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("文", "章"), Collections.singletonList("が"), Arrays.asList("上", "手")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("ぶん", "しょう"), Collections.singletonList(""), Arrays.asList("じょう", "ず")
                ))
                .wordIdList(Arrays.asList("9", "11", "12"))
                .translation("擅长写作")
                .build();

// 文章的词义
        WordMeaning wordMeaning17 = new WordMeaning.Builder()
                .wordMeaningId("17")
                .wordId("9")
                .partOfSpeech(Constants.PartOfSpeech.NOUN)
                .originalDefinition("まとまった内容の文書")
                .translationDefinition("文章")
                .build();

        WordMeaning wordMeaning18 = new WordMeaning.Builder()
                .wordMeaningId("18")
                .wordId("9")
                .partOfSpeech(Constants.PartOfSpeech.NOUN)
                .originalDefinition("修辞的な表現方法")
                .translationDefinition("写作技巧")
                .build();

// 文章的例句
        WordSentence wordSentence20 = new WordSentence.Builder()
                .wordSentenceId("20")
                .wordMeaningId("17")
                .wordId("9")
                .kanjiComponents(Arrays.asList(
                        Arrays.asList("卒", "業", "論", "文"), Collections.singletonList("の"),
                        Arrays.asList("文", "章"), Collections.singletonList("を"),
                        Arrays.asList("推", "敲"), Collections.singletonList("する")
                ))
                .kanaComponents(Arrays.asList(
                        Arrays.asList("そつ", "ぎょう", "ろん", "ぶん"), Collections.singletonList(""),
                        Arrays.asList("ぶん", "しょう"), Collections.singletonList(""),
                        Arrays.asList("すい", "こう"), Collections.singletonList("")
                ))
                .wordIdList(Arrays.asList("13", "14", "8", "9", "15", "15"))
                .translation("推敲毕业论文的文章")
                .build();

// 新增单词10 - を (助词)
        Word word10 = new Word.Builder()
                .addWordId("10").withMeaningIdList(Collections.singletonList("19"))
                .build();

        BasicWord basicWord10 = new BasicWord.Builder()
                .kanjiComponents(Collections.singletonList("を"))
                .kanaComponents(Collections.singletonList(""))
                .wordId("10")
                .accentMark("") // 助词无音调
                .build();

        WordMeaning wordMeaning19 = new WordMeaning.Builder()
                .wordMeaningId("19")
                .wordId("10")
                .partOfSpeech(Constants.PartOfSpeech.UNKNOWN)
                .originalDefinition("目的語を示す格助詞")
                .translationDefinition("表示宾语的格助词")
                .build();

        wordRepository.insertWord(word1);
        wordRepository.insertBasicWord(basicWord1);
        wordRepository.insertAntonym(antonymWord1);
        wordRepository.insertAntonym(antonymWord2);
        wordRepository.insertSynonym(synonymWord1);
        wordRepository.insertSynonym(synonymWord2);
        wordRepository.insertWordCollocation(wordCollocation1);
        wordRepository.insertWordCollocation(wordCollocation2);
        wordRepository.insertWordCollocation(wordCollocation3);
        wordRepository.insertWordMeaning(wordMeaning0);
        wordRepository.insertWordMeaning(wordMeaning1);
        wordRepository.insertWordMeaning(wordMeaning2);
        wordRepository.insertWordSentence(wordSentence1);
        wordRepository.insertWordSentence(wordSentence2);
        wordRepository.insertWordSentence(wordSentence3);
        wordRepository.insertWord(word2);
        wordRepository.insertBasicWord(basicWord2);
        wordRepository.insertWordCollocation(wordCollocation4);
        wordRepository.insertWordCollocation(wordCollocation5);
        wordRepository.insertWordCollocation(wordCollocation6);
        wordRepository.insertWordMeaning(wordMeaning3);
        wordRepository.insertWordMeaning(wordMeaning4);
        wordRepository.insertWordSentence(wordSentence4);
        wordRepository.insertWordSentence(wordSentence5);
        wordRepository.insertWordSentence(wordSentence6);
        wordRepository.insertWord(word3);
        wordRepository.insertBasicWord(basicWord3);
        wordRepository.insertWordCollocation(wordCollocation7);
        wordRepository.insertWordCollocation(wordCollocation8);
        wordRepository.insertWordMeaning(wordMeaning5);
        wordRepository.insertWordMeaning(wordMeaning6);
        wordRepository.insertWordSentence(wordSentence7);
        wordRepository.insertWord(word4);
        wordRepository.insertBasicWord(basicWord4);
        wordRepository.insertWordCollocation(wordCollocation10);
        wordRepository.insertWordMeaning(wordMeaning7);
        wordRepository.insertWordMeaning(wordMeaning8);
        wordRepository.insertWord(word5);
        wordRepository.insertBasicWord(basicWord5);
        wordRepository.insertWordCollocation(wordCollocation13);
        wordRepository.insertWordCollocation(wordCollocation14);
        wordRepository.insertWordMeaning(wordMeaning9);
        wordRepository.insertWordMeaning(wordMeaning10);
        wordRepository.insertWordSentence(wordSentence13);
        wordRepository.insertWordSentence(wordSentence14);
        wordRepository.insertWord(word6);
        wordRepository.insertBasicWord(basicWord6);
        wordRepository.insertWordCollocation(wordCollocation15);
        wordRepository.insertWordCollocation(wordCollocation16);
        wordRepository.insertWordMeaning(wordMeaning11);
        wordRepository.insertWordMeaning(wordMeaning12);
        wordRepository.insertWordSentence(wordSentence15);
        wordRepository.insertWordSentence(wordSentence16);
        wordRepository.insertWord(word7);
        wordRepository.insertBasicWord(basicWord7);
        wordRepository.insertWordCollocation(wordCollocation17);
        wordRepository.insertWordCollocation(wordCollocation18);
        wordRepository.insertWordMeaning(wordMeaning13);
        wordRepository.insertWordMeaning(wordMeaning14);
        wordRepository.insertWordSentence(wordSentence17);
        wordRepository.insertWord(word8);
        wordRepository.insertBasicWord(basicWord8);
        wordRepository.insertWordCollocation(wordCollocation19);
        wordRepository.insertWordCollocation(wordCollocation20);
        wordRepository.insertWordMeaning(wordMeaning15);
        wordRepository.insertWordMeaning(wordMeaning16);
        wordRepository.insertWordSentence(wordSentence19);
        wordRepository.insertWord(word9);
        wordRepository.insertBasicWord(basicWord9);
        wordRepository.insertWordCollocation(wordCollocation21);
        wordRepository.insertWordCollocation(wordCollocation22);
        wordRepository.insertWordMeaning(wordMeaning17);
        wordRepository.insertWordMeaning(wordMeaning18);
        wordRepository.insertWordSentence(wordSentence20);
        wordRepository.insertWord(word10);
        wordRepository.insertBasicWord(basicWord10);
        wordRepository.insertWordMeaning(wordMeaning19);
    }
}