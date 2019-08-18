package com.crabsofts.sharenotes.Models;

/**
 * Created by CodeGod on 5/28/2017.
 */

public class NotesDetailsModel {
    String unitname, subjectname, pages;
    int colorStrip;



    public NotesDetailsModel(String unitname, String subjectname, String pages, int colorStrip){
        this.setUnitname(unitname);
        this.setSubjectname(subjectname);
        this.setPages(pages);
        this.setColorStrip(colorStrip);
    }
    public int getColorStrip() {
        return colorStrip;
    }

    public void setColorStrip(int colorStrip) {
        this.colorStrip = colorStrip;
    }
    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
}
