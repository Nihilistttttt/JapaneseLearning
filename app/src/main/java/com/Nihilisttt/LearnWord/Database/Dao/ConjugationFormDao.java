package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.ConjugationFormEntity;

import java.util.List;

@Dao
public interface ConjugationFormDao {
    @Insert
    void insertConjugationForms(ConjugationFormEntity... entities);
    @Update
    void updateConjugationForms(ConjugationFormEntity... entities);
    @Query("DELETE FROM ConjugationForm")
    void deleteAllConjugationForms();
    @Query("DELETE FROM ConjugationForm WHERE conjugationFormId = :id")
    void deleteConjugationForm(String id);
    @Query("SELECT * FROM ConjugationForm ORDER BY conjugationFormId")
    LiveData<List<ConjugationFormEntity>> getAllConjugationForms();
    @Query("SELECT * FROM ConjugationForm WHERE conjugationFormId = :id")
    LiveData<ConjugationFormEntity> getConjugationFormById(String id);
    @Query("SELECT * FROM ConjugationForm WHERE wordId = :word_id")
    LiveData<List<ConjugationFormEntity>> getConjugationFormsByWordId(String word_id);
    @Query("SELECT * FROM ConjugationForm WHERE conjugationFormId IN (:idList)")
    LiveData<List<ConjugationFormEntity>> getConjugationFormsByConjugationFormIdList(List<String> idList);
}