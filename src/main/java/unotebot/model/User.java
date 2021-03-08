package unotebot.model;

// Модель пользователя

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    private int mUserId;
    private String mName;
    private LinkedHashMap<String, Note> mNotes = new LinkedHashMap<>();

    public User(int userId){
        this.mUserId = userId;
    }

    public void addNote(String noteText){
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        Note note = new Note(id, noteText);
        mNotes.put(id, note);
    }

    public void removeNote(String text){
        for(Map.Entry<String, Note> pair : mNotes.entrySet()){
            Note value = pair.getValue();
            if(value.getmNote().contains(text)){
                mNotes.remove(value.getmNoteId());
            }
        }
    }

    public void removeAllNotes(){
        mNotes.clear();
    }

    public LinkedHashMap<String, Note> getNotes(){
        return mNotes;
    }
}
