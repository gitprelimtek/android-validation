package com.prelimtek.android.customcomponents;

import java.util.List;

public interface TextDAOInterface {

    public boolean addNotes(NotesModel notes) throws Exception;

    public NotesModel[] getNotes(String modelId, Long afterThisDate, int rowCount, int pageOffset);

    public List<NotesModel> getNotes(String modelId, Long beforeThisDate, Long afterThisDate,  int rowCount, int pageOffset);

    public NotesModel getNotesById(String s);

    public boolean updateNote(NotesModel note);

    public boolean deleteNotes(NotesModel notes);

    public int getNotesCount(String id);

}
