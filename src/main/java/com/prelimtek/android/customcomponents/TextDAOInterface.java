package com.prelimtek.android.customcomponents;

import java.util.List;

public interface TextDAOInterface {

    public boolean addNotes(String modelId, String noteText);

    public NotesModel[] getNotes(String modelId, Long afterThisDate, int pageSize);

    public List<NotesModel> getNotes(String modelId, Long afterThisDate, Long beforeThisDate, int pageSize);
}
