package com.example;

import java.util.List;

import com.orm.query.Select;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.orm.SugarRecord;

import static com.orm.SugarRecord.save;

public class SugarActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.main);
         SugarRecord.deleteAll(Note.class);
         SugarRecord.deleteAll(TextNote.class);
         SugarRecord.deleteAll(Tag.class);
         SugarRecord.deleteAll(NewNote.class);
         initDb();
         Intent intent = new Intent(this, NoteListActivity.class);
         startActivity(intent);
    }

     private void initDb() {

         Tag t1 = new Tag("tag1");
         Tag t2 = new Tag("tag2");
         save(t1);
         save(t2);

         Note n1 = new Note(10, "note1", "description1", t1);
         Note n2 = new Note(11, "note2", "description2", t1);
         Note n3 = new Note(12, "note3", "description3", t2);
         Note n4 = new Note(13, "note4", "description4", t2);
 
         if (!n1.Validate()) Log.e("TEST", "Validation before saving failed!");
         
         TextNote textNote = new TextNote();
         textNote.desc = "Test";

         save(textNote);
         save(n1);
         save(n2);
         save(n3);
         save(n4);

         n1.setDescription("matrix");
         n1.setTitle("atrix");
         save(n1);
         n2.setDescription("matrix");
         n2.setTitle("satrix");
         save(n2);
         n3.setDescription("matrix");
         n3.setTitle("batrix");
         save(n3);

         List<Note> notes = Select.from(Note.class).list();
         if (notes.size() != 4) Log.e("TEST", "Note size not 4!");
         
         Note existingNote = Select.from(Note.class).where("TITLE = ?", new String[] { "atrix" }).first();
         if (existingNote == null) Log.e("TEST", "Note not found in database!");
         if (!existingNote.Validate()) Log.e("TEST", "Validation after loading failed!");
	}
}
