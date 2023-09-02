package ott.hunter.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("DELETE FROM task")
    void deleteAll();

    @Insert
    void insert(Task task);

    @Delete
    void delete(List<Task> task);

    @Update
    void update(Task task);

}
