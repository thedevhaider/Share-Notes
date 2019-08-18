package com.crabsofts.sharenotes.Models;

/**
 * Created by CodeGod on 5/13/2017.
 */

public class SubjectDetailsModel {
    int cardBackground;
    String subjectText, subject_owner;
    int id = 0;


    public SubjectDetailsModel(int cardBackground, String subjectText, int id, String subject_owner){
        this.setCardBackground(cardBackground);
        this.setSubjectText(subjectText);
        this.setId(id);
        this.setSubject_owner(subject_owner);

    }

    public String getSubject_owner() {
        return subject_owner;
    }

    public void setSubject_owner(String subject_owner) {
        this.subject_owner = subject_owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int reference) {

        this.id = reference;
    }
    public int getCardBackground() {
        return cardBackground;
    }

    public void setCardBackground(int cardBackground) {
        this.cardBackground = cardBackground;
    }

    public String getSubjectText() {

        return subjectText;
    }

    public void setSubjectText(String subjectText) {

        this.subjectText = subjectText;
    }
}
