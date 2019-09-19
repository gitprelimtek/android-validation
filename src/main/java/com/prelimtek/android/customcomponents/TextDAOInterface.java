package com.prelimtek.android.customcomponents;

import java.util.List;

public interface TextDAOInterface {

    public boolean addNotes(NotesModel notes) throws Exception;
<<<<<<< HEAD
=======
  
    public boolean addNotes(String modelId, String noteText);
>>>>>>> 42e644bd127b7034aec76905b0e1cf8b03f80012

    public NotesModel[] getNotes(String modelId, Long afterThisDate, int rowCount, int pageOffset);

    public List<NotesModel> getNotes(String modelId, Long beforeThisDate, Long afterThisDate,  int rowCount, int pageOffset);

}
