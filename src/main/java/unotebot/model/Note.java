package unotebot.model;

// Модель заметки

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
    private String mNoteId;
    private String mNote;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");

    public Note(String randomUUIDString, String note){
        this.mNoteId = randomUUIDString;
        this.mNote = note;
    }

    public String getmDate() {
        return dateFormat.format(new Date());
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

    public String getmNoteId() {
        return mNoteId;
    }

    public void setmNoteId(String mNoteId) {
        this.mNoteId = mNoteId;
    }

}
